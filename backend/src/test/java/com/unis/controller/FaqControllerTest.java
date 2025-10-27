package com.unis.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.unis.model.Faq;
import com.unis.service.FaqService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.ws.rs.core.Response;

class FaqControllerTest {

    @Mock FaqService faqService;
    @InjectMocks FaqController faqController;

    @BeforeEach
    void init() { MockitoAnnotations.openMocks(this); }

    @Test
    void listar_ok() {
        when(faqService.listarPreguntas()).thenReturn(List.of(new Faq()));
        assertEquals(1, faqController.listarPreguntas().size());
        verify(faqService).listarPreguntas();
    }

    @Test
    void obtener_id_ok_y_404() {
        Faq f = new Faq();
        when(faqService.buscarPorId(1L)).thenReturn(f);
        Response ok = faqController.obtenerFaqPorId(1L);
        assertEquals(200, ok.getStatus());
        assertEquals(f, ok.getEntity());

        when(faqService.buscarPorId(99L)).thenReturn(null);
        Response nf = faqController.obtenerFaqPorId(99L);
        assertEquals(404, nf.getStatus());
    }

    @Test
    void crear_validaciones_y_ok() {
        Faq invalida = new Faq();
        invalida.setPregunta("   ");
        Response bad = faqController.guardarPregunta(invalida);
        assertEquals(400, bad.getStatus());

        Faq sinEditor = new Faq();
        sinEditor.setPregunta("¿P?");
        assertThrows(IllegalArgumentException.class, () -> faqController.guardarPregunta(sinEditor));

        Faq valida = new Faq();
        valida.setPregunta("¿P?");
        valida.setEditadoPor("editor@ex.com");
        doNothing().when(faqService).guardarPregunta(valida);
        Response created = faqController.guardarPregunta(valida);
        assertEquals(201, created.getStatus());
        assertEquals(valida, created.getEntity());
    }

    @Test
    void editar_validaciones_y_ok() {
        when(faqService.buscarPorId(99L)).thenReturn(null);
        Response nf = faqController.editarPregunta(99L, new Faq());
        assertEquals(404, nf.getStatus());

        Faq existente = new Faq();
        when(faqService.buscarPorId(1L)).thenReturn(existente);

        Faq sinEditor = new Faq();
        Response bad = faqController.editarPregunta(1L, sinEditor);
        assertEquals(400, bad.getStatus());

        Faq update = new Faq();
        update.setEditadoPor("editor@ex.com");
        update.setPregunta("nueva");
        doNothing().when(faqService).actualizarFaq(any(Faq.class));
        Response ok = faqController.editarPregunta(1L, update);
        assertEquals(200, ok.getStatus());
    }

    @Test
    void eliminar_ok_y_404() {
        when(faqService.eliminarFaq(1L)).thenReturn(true);
        when(faqService.eliminarFaq(99L)).thenReturn(false);
        assertEquals(200, faqController.eliminarPregunta(1L).getStatus());
        assertEquals(404, faqController.eliminarPregunta(99L).getStatus());
    }

    @Test
    void pendientes_ok() {
        when(faqService.listarPorEstado("PROCESO")).thenReturn(List.of(new Faq()));
        assertEquals(1, faqController.listarPendientes().size());
    }

    @Test
    void aprobar_ok_y_404() {
        when(faqService.buscarPorId(1L)).thenReturn(new Faq());
        doNothing().when(faqService).actualizarFaq(any(Faq.class));
        assertEquals(200, faqController.aprobarPregunta(1L).getStatus());

        when(faqService.buscarPorId(99L)).thenReturn(null);
        assertEquals(404, faqController.aprobarPregunta(99L).getStatus());
    }

    @Test
    void rechazar_ok_y_404() {
        when(faqService.buscarPorId(1L)).thenReturn(new Faq());
        doNothing().when(faqService).actualizarFaq(any(Faq.class));
        assertEquals(200, faqController.rechazarPregunta(1L, "motivo").getStatus());

        when(faqService.buscarPorId(99L)).thenReturn(null);
        assertEquals(404, faqController.rechazarPregunta(99L, "motivo").getStatus());
    }
}
