package com.unis.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.unis.dto.ModeracionReporteDTO;
import com.unis.service.ReporteModeracionService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ReporteModeracionControllerTest {

    @Mock ReporteModeracionService service;
    @InjectMocks ReporteModeracionController controller;

    @BeforeEach
    void init() { MockitoAnnotations.openMocks(this); }

    @Test
    void fechas_validas_ok() {
        when(service.obtenerUsuariosConRechazos(any(), any(), eq(5)))
            .thenReturn(List.of(new ModeracionReporteDTO("u", 1L)));
        assertEquals(1, controller.obtener("2024-01-01", "2024-01-31", 5).size());
    }

    @Test
    void fecha_invalida_lanza_excepcion_400() {
        assertThrows(jakarta.ws.rs.WebApplicationException.class,
            () -> controller.obtener("2024-13-01", "2024-01-31", 10));
    }
}
