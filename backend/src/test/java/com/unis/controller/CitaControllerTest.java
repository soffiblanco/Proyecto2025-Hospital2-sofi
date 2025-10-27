package com.unis.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.unis.model.Cita;
import com.unis.service.CitaService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CitaControllerTest {

    @Mock CitaService citaService;
    @InjectMocks CitaController citaController;

    @BeforeEach void init() { MockitoAnnotations.openMocks(this); }

    @Test
    void listar_y_obtener_ok() {
        when(citaService.obtenerCitas()).thenReturn(List.of(new Cita()));
        assertEquals(1, citaController.obtenerCitas().size());

        Cita c = new Cita();
        when(citaService.obtenerCitaPorId(1L)).thenReturn(c);
        assertEquals(c, citaController.obtenerCitaPorId(1L));
    }

    @Test
    void agendar_horario_fuera_de_rango_lanza_IAE() {
        Cita c = new Cita();
        c.setHoraInicio("07:00");
        c.setHoraFin("09:00");
        assertThrows(IllegalArgumentException.class, () -> citaController.agendarCita(c));
    }

    @Test
    void agendar_fin_antes_de_inicio_lanza_IAE() {
        Cita c = new Cita();
        c.setHoraInicio("10:00");
        c.setHoraFin("09:00");
        assertThrows(IllegalArgumentException.class, () -> citaController.agendarCita(c));
    }

    @Test
    void agendar_formato_invalido_lanza_IAE() {
        Cita c = new Cita();
        c.setHoraInicio("xx");
        c.setHoraFin("yy");
        assertThrows(IllegalArgumentException.class, () -> citaController.agendarCita(c));
    }

    @Test
    void agendar_ok() {
        Cita c = new Cita();
        c.setHoraInicio("09:00");
        c.setHoraFin("10:00");
        doNothing().when(citaService).agendarCita(c);
        assertDoesNotThrow(() -> citaController.agendarCita(c));
        verify(citaService).agendarCita(c);
    }

    @Test
    void cancelar_ok() {
        doNothing().when(citaService).cancelarCita(1L);
        citaController.cancelarCita(1L);
        verify(citaService).cancelarCita(1L);
    }
}
