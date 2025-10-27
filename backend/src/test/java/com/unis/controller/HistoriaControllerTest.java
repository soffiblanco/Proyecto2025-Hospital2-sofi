package com.unis.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.unis.model.Historia;
import com.unis.service.HistoriaService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.ws.rs.core.Response;

class HistoriaControllerTest {

    @Mock HistoriaService historiaService;
    @InjectMocks HistoriaController historiaController;

    @BeforeEach
    void init() { MockitoAnnotations.openMocks(this); }

    @Test
    void listar_ok_y_publicadas_ok() {
        when(historiaService.listar()).thenReturn(List.of(new Historia()));
        when(historiaService.listarPorEstado("PUBLICADO")).thenReturn(List.of(new Historia()));
        assertEquals(1, historiaController.getHistorias().size());
        assertEquals(1, historiaController.getHistoriasPublicadas().size());
    }

    @Test
    void obtener_id_ok_y_404() {
        Historia h = new Historia();
        when(historiaService.obtenerPorId(1L)).thenReturn(h);
        Response ok = historiaController.getHistoria(1L);
        assertEquals(200, ok.getStatus());
        assertEquals(h, ok.getEntity());

        when(historiaService.obtenerPorId(99L)).thenReturn(null);
        assertEquals(404, historiaController.getHistoria(99L).getStatus());
    }

    @Test
    void crear_validaciones_y_ok() {
        Historia invalida = new Historia();
        Response bad = historiaController.createHistoria(invalida);
        assertEquals(400, bad.getStatus());

        Historia in = new Historia();
        in.setEditorEmail("editor@ex.com");
        when(historiaService.crear(in)).thenReturn(in);
        Response created = historiaController.createHistoria(in);
        assertEquals(201, created.getStatus());
        assertEquals(in, created.getEntity());
    }

    @Test
    void actualizar_validaciones_y_ok() {
        when(historiaService.obtenerPorId(99L)).thenReturn(null);
        assertEquals(404, historiaController.updateHistoria(99L, new Historia()).getStatus());

        Historia existente = new Historia();
        when(historiaService.obtenerPorId(1L)).thenReturn(existente);

        Historia sinEditor = new Historia();
        assertEquals(400, historiaController.updateHistoria(1L, sinEditor).getStatus());

        Historia up = new Historia();
        up.setEditorEmail("editor@ex.com");
        when(historiaService.actualizar(eq(1L), any(Historia.class))).thenReturn(up);
        Response ok = historiaController.updateHistoria(1L, up);
        assertEquals(200, ok.getStatus());
    }

    @Test
    void eliminar_noContent_y_404() {
        when(historiaService.eliminar(1L)).thenReturn(true);
        when(historiaService.eliminar(99L)).thenReturn(false);
        assertEquals(204, historiaController.deleteHistoria(1L).getStatus());
        assertEquals(404, historiaController.deleteHistoria(99L).getStatus());
    }

    @Test
    void aprobar_y_rechazar_ok_y_404() {
        when(historiaService.obtenerPorId(1L)).thenReturn(new Historia());
        when(historiaService.actualizar(eq(1L), any(Historia.class))).thenReturn(new Historia());
        assertEquals(200, historiaController.aprobarHistoria(1L).getStatus());
        assertEquals(200, historiaController.rechazarHistoria(1L, "motivo").getStatus());

        when(historiaService.obtenerPorId(99L)).thenReturn(null);
        assertEquals(404, historiaController.aprobarHistoria(99L).getStatus());
        assertEquals(404, historiaController.rechazarHistoria(99L, "motivo").getStatus());
    }
}
