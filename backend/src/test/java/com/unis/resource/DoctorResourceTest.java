package com.unis.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unis.model.Doctor;
import com.unis.service.DoctorService;

import jakarta.ws.rs.core.Response;

public class DoctorResourceTest {

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private DoctorResource doctorResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerTodosLosDoctores_deberiaRetornarListaDeDoctores() {
        // Arrange
        Doctor doctor1 = new Doctor();
        doctor1.setIdDoctor(1L);
        doctor1.setApellido("García");
        
        Doctor doctor2 = new Doctor();
        doctor2.setIdDoctor(2L);
        doctor2.setApellido("López");
        
        List<Doctor> doctores = Arrays.asList(doctor1, doctor2);
        when(doctorService.getAllDoctores()).thenReturn(doctores);

        // Act
        List<Doctor> resultado = doctorResource.obtenerTodosLosDoctores();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(doctores, resultado);
        verify(doctorService).getAllDoctores();
    }

    @Test
    void obtenerDoctor_cuandoDoctorExiste_deberiaRetornarDoctor() {
        // Arrange
        Long id = 1L;
        Doctor doctor = new Doctor();
        doctor.setIdDoctor(id);
        doctor.setApellido("Martínez");
        
        when(doctorService.getDoctorById(id)).thenReturn(Optional.of(doctor));

        // Act
        Response response = doctorResource.obtenerDoctor(id);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(doctorService).getDoctorById(id);
    }

    @Test
    void obtenerDoctor_cuandoDoctorNoExiste_deberiaRetornarNotFound() {
        // Arrange
        Long id = 999L;
        when(doctorService.getDoctorById(id)).thenReturn(Optional.empty());

        // Act
        Response response = doctorResource.obtenerDoctor(id);

        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(doctorService).getDoctorById(id);
    }

    @Test
    void registrarDoctor_deberiaRegistrarDoctorExitosamente() {
        // Arrange
        Doctor doctor = new Doctor();
        doctor.setApellido("Rodríguez");
        doctor.setEspecialidad("Cardiología");
        
        doNothing().when(doctorService).registrarDoctor(any(Doctor.class));

        // Act
        Response response = doctorResource.registrarDoctor(doctor);

        // Assert
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(doctorService).registrarDoctor(doctor);
    }

    @Test
    void actualizarDoctor_cuandoDoctorExiste_deberiaActualizarDoctor() {
        // Arrange
        Long id = 1L;
        Doctor doctor = new Doctor();
        doctor.setApellido("Actualizado");
        
        when(doctorService.actualizarDoctor(id, doctor)).thenReturn(true);

        // Act
        Response response = doctorResource.actualizarDoctor(id, doctor);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(doctorService).actualizarDoctor(id, doctor);
    }

    @Test
    void actualizarDoctor_cuandoDoctorNoExiste_deberiaRetornarNotFound() {
        // Arrange
        Long id = 999L;
        Doctor doctor = new Doctor();
        doctor.setApellido("No existe");
        
        when(doctorService.actualizarDoctor(id, doctor)).thenReturn(false);

        // Act
        Response response = doctorResource.actualizarDoctor(id, doctor);

        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(doctorService).actualizarDoctor(id, doctor);
    }

    @Test
    void eliminarDoctor_cuandoDoctorExiste_deberiaEliminarDoctor() {
        // Arrange
        Long id = 1L;
        when(doctorService.eliminarDoctor(id)).thenReturn(true);

        // Act
        Response response = doctorResource.eliminarDoctor(id);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(doctorService).eliminarDoctor(id);
    }

    @Test
    void eliminarDoctor_cuandoDoctorNoExiste_deberiaRetornarNotFound() {
        // Arrange
        Long id = 999L;
        when(doctorService.eliminarDoctor(id)).thenReturn(false);

        // Act
        Response response = doctorResource.eliminarDoctor(id);

        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(doctorService).eliminarDoctor(id);
    }
}
