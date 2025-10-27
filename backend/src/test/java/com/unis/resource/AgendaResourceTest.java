package com.unis.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unis.model.Agenda;
import com.unis.service.AgendaService;

import jakarta.ws.rs.core.Response;

class AgendaResourceTest {

    @Mock
    AgendaService agendaService;

    @InjectMocks
    AgendaResource agendaResource;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtener_por_doctor_ok() {
        when(agendaService.obtenerAgendasPorDoctor(1L)).thenReturn(List.of(new Agenda()));
        List<Agenda> out = agendaResource.obtenerAgendasPorDoctor(1L);
        assertEquals(1, out.size());
        verify(agendaService).obtenerAgendasPorDoctor(1L);
    }

    @Test
    void crear_crea_201() {
        doNothing().when(agendaService).crearAgenda(any(Agenda.class));
        Response res = agendaResource.crearAgenda(new Agenda());
        assertEquals(201, res.getStatus());
        verify(agendaService).crearAgenda(any(Agenda.class));
    }

    @Test
    void actualizar_ok() {
        doNothing().when(agendaService).actualizarAgenda(eq(1L), any(Agenda.class));
        Response res = agendaResource.actualizarAgenda(1L, new Agenda());
        assertEquals(200, res.getStatus());
        verify(agendaService).actualizarAgenda(eq(1L), any(Agenda.class));
    }
}
