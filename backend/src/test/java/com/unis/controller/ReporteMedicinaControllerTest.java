package com.unis.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.unis.dto.MedicinasReporteDTO;
import com.unis.service.ReporteMedicinaService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ReporteMedicinaControllerTest {

    @Mock ReporteMedicinaService service;
    @InjectMocks ReporteMedicinaController controller;

    @BeforeEach void init() { MockitoAnnotations.openMocks(this); }

    @Test
    void fechas_validas_ok() {
        when(service.obtenerReporte(any(), any(), eq(3)))
            .thenReturn(List.of(new MedicinasReporteDTO("m", 1L)));
        assertEquals(1, controller.obtener("2024-01-01", "2024-01-31", 3).size());
    }

    @Test
    void fecha_invalida_lanza_excepcion_400() {
        assertThrows(jakarta.ws.rs.WebApplicationException.class,
            () -> controller.obtener("2024-02-30", "2024-01-31", 10));
    }
}
