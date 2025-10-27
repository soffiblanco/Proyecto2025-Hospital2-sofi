package com.unis.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unis.model.Cita;
import com.unis.model.Doctor;
import com.unis.service.CitaService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.ws.rs.core.Response;

class CitaResourceTest {

    @Mock
    CitaService citaService;

    @InjectMocks
    CitaResource citaResource;

    ObjectMapper mapper;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        mapper = new ObjectMapper();
    }

    @Test
    void listar_ok() {
        when(citaService.obtenerCitas()).thenReturn(List.of(new Cita()));
        assertEquals(1, citaResource.obtenerCitas().size());
        verify(citaService).obtenerCitas();
    }

    @Test
    void obtener_por_id_ok() {
        when(citaService.obtenerCitaPorId(1L)).thenReturn(new Cita());
        assertNotNull(citaResource.obtenerCita(1L));
        verify(citaService).obtenerCitaPorId(1L);
    }

    @Test
    void agendar_validaciones_400() {
        Cita sinInicio = new Cita();
        sinInicio.setHoraFin("10:00");
        Response r1 = citaResource.agendarCita(sinInicio);
        assertEquals(400, r1.getStatus());

        Cita sinFin = new Cita();
        sinFin.setHoraInicio("09:00");
        Response r2 = citaResource.agendarCita(sinFin);
        assertEquals(400, r2.getStatus());

        Cita inicioDespuesFin = new Cita();
        inicioDespuesFin.setHoraInicio("11:00");
        inicioDespuesFin.setHoraFin("10:00");
        Response r3 = citaResource.agendarCita(inicioDespuesFin);
        assertEquals(400, r3.getStatus());
    }

    @Test
    void agendar_ok_201() {
        Cita c = new Cita();
        c.setHoraInicio("09:00");
        c.setHoraFin("10:00");
        doNothing().when(citaService).agendarCita(c);
        Response res = citaResource.agendarCita(c);
        assertEquals(201, res.getStatus());
        verify(citaService).agendarCita(c);
    }

    @Test
    void actualizar_ok_y_404() {
        doNothing().when(citaService).actualizarCita(eq(1L), any(Cita.class));
        Response ok = citaResource.actualizarCita(1L, new Cita());
        assertEquals(200, ok.getStatus());

        doThrow(new IllegalArgumentException("no existe")).when(citaService).actualizarCita(eq(99L), any(Cita.class));
        Response nf = citaResource.actualizarCita(99L, new Cita());
        assertEquals(404, nf.getStatus());
    }

    @Test
    void cancelar_ok_y_404() {
        doNothing().when(citaService).cancelarCita(1L);
        Response ok = citaResource.cancelarCita(1L);
        assertEquals(200, ok.getStatus());

        doThrow(new RuntimeException("no existe")).when(citaService).cancelarCita(99L);
        Response nf = citaResource.cancelarCita(99L);
        assertEquals(404, nf.getStatus());
    }

    @Test
    void reasignar_ok_y_404_y_400() {
        ObjectNode body = mapper.createObjectNode();
        body.put("idDoctor", 7);

        Doctor d = new Doctor();
        when(citaService.buscarDoctorPorId(7L)).thenReturn(d);
        doNothing().when(citaService).reasignarDoctor(1L, d);
        Response ok = citaResource.reasignarDoctor(1L, body);
        assertEquals(200, ok.getStatus());

        when(citaService.buscarDoctorPorId(7L)).thenReturn(null);
        Response nf = citaResource.reasignarDoctor(1L, body);
        assertEquals(404, nf.getStatus());

        doThrow(new IllegalArgumentException("no existe"))
            .when(citaService).reasignarDoctor(eq(99L), any(Doctor.class));
        Response nf2 = citaResource.reasignarDoctor(99L, body);
        assertEquals(404, nf2.getStatus());

        when(citaService.buscarDoctorPorId(7L)).thenReturn(d);
        doThrow(new RuntimeException("bad req")).when(citaService).reasignarDoctor(eq(1L), any(Doctor.class));
        Response bad = citaResource.reasignarDoctor(1L, body);
        assertEquals(400, bad.getStatus());
    }

    @Test
    void procesar_ok_y_404_y_500() {
        ObjectNode body = mapper.createObjectNode();
        body.put("diagnostico", "dx");
        body.put("resultados", "rs");

        doNothing().when(citaService).procesarCitaYEnviarResultados(eq(1L), anyString(), anyString());
        Response ok = citaResource.procesarCita(1L, body);
        assertEquals(200, ok.getStatus());

        doThrow(new IllegalArgumentException("no existe"))
            .when(citaService).procesarCitaYEnviarResultados(eq(99L), anyString(), anyString());
        Response nf = citaResource.procesarCita(99L, body);
        assertEquals(404, nf.getStatus());

        doThrow(new RuntimeException("boom"))
            .when(citaService).procesarCitaYEnviarResultados(eq(2L), anyString(), anyString());
        Response err = citaResource.procesarCita(2L, body);
        assertEquals(500, err.getStatus());
    }

    @Test
    void recibir_externa_ok_y_400() throws Exception {
        ObjectNode body = mapper.createObjectNode();
        body.put("id", 1);

        doNothing().when(citaService).crearCitaDesdeJson(any());
        Response ok = citaResource.recibirDesdeAseguradora((com.fasterxml.jackson.databind.node.ObjectNode) body);
        assertEquals(201, ok.getStatus());

        doThrow(new RuntimeException("bad"))
            .when(citaService).crearCitaDesdeJson(any());
        Response bad = citaResource.recibirDesdeAseguradora((com.fasterxml.jackson.databind.node.ObjectNode) body);
        assertEquals(400, bad.getStatus());
    }
}
