package com.unis.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.unis.model.Doctor;
import com.unis.service.DoctorService;

import jakarta.ws.rs.core.Response;

@ExtendWith(MockitoExtension.class)
class DoctorResourceAdditionalTest {

    @Mock
    DoctorService doctorService;

    @InjectMocks
    DoctorResource doctorResource;

    @Test
    void listar_ok() {
        Doctor d1 = new Doctor();
        Doctor d2 = new Doctor();
        when(doctorService.getAllDoctores()).thenReturn(Arrays.asList(d1, d2));

        List<Doctor> out = doctorResource.obtenerTodosLosDoctores();
        assertEquals(2, out.size());
        verify(doctorService).getAllDoctores();
    }

    @Test
    void obtener_por_id_ok() {
        Doctor d = new Doctor();
        when(doctorService.getDoctorById(1L)).thenReturn(Optional.of(d));
        Response res = doctorResource.obtenerDoctor(1L);
        assertEquals(Response.Status.OK.getStatusCode(), res.getStatus());
        assertEquals(d, res.getEntity());
        verify(doctorService).getDoctorById(1L);
    }

    @Test
    void obtener_por_id_no_encontrado() {
        when(doctorService.getDoctorById(99L)).thenReturn(Optional.empty());
        Response res = doctorResource.obtenerDoctor(99L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), res.getStatus());
        verify(doctorService).getDoctorById(99L);
    }

    @Test
    void registrar_crea_201() {
        doNothing().when(doctorService).registrarDoctor(any(Doctor.class));
        Response res = doctorResource.registrarDoctor(new Doctor());
        assertEquals(Response.Status.CREATED.getStatusCode(), res.getStatus());
        verify(doctorService).registrarDoctor(any(Doctor.class));
    }

    @Test
    void actualizar_ok_y_not_found() {
        when(doctorService.actualizarDoctor(eq(1L), any(Doctor.class))).thenReturn(true);
        when(doctorService.actualizarDoctor(eq(99L), any(Doctor.class))).thenReturn(false);

        assertEquals(200, doctorResource.actualizarDoctor(1L, new Doctor()).getStatus());
        assertEquals(404, doctorResource.actualizarDoctor(99L, new Doctor()).getStatus());
    }

    @Test
    void eliminar_ok_y_not_found() {
        when(doctorService.eliminarDoctor(1L)).thenReturn(true);
        when(doctorService.eliminarDoctor(99L)).thenReturn(false);

        assertEquals(200, doctorResource.eliminarDoctor(1L).getStatus());
        assertEquals(404, doctorResource.eliminarDoctor(99L).getStatus());
    }
}




