package com.unis.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unis.model.Medicamento;
import com.unis.service.MedicamentoService;

import jakarta.ws.rs.core.Response;

class MedicamentoResourceTest {

    @Mock
    MedicamentoService medicamentoService;

    @InjectMocks
    MedicamentoResource medicamentoResource;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listar_ok() {
        when(medicamentoService.listarTodos()).thenReturn(List.of(new Medicamento()));
        List<Medicamento> out = medicamentoResource.listarTodos();
        assertEquals(1, out.size());
        verify(medicamentoService).listarTodos();
    }

    @Test
    void obtener_id_ok_y_404() {
        Medicamento m = new Medicamento();
        when(medicamentoService.obtenerPorId(1L)).thenReturn(m);
        Response ok = medicamentoResource.obtenerPorId(1L);
        assertEquals(200, ok.getStatus());
        assertEquals(m, ok.getEntity());

        when(medicamentoService.obtenerPorId(99L)).thenReturn(null);
        Response nf = medicamentoResource.obtenerPorId(99L);
        assertEquals(404, nf.getStatus());
    }

    @Test
    void crear_crea_201() {
        Medicamento in = new Medicamento();
        Medicamento created = new Medicamento();
        when(medicamentoService.crearMedicamento(in)).thenReturn(created);
        Response res = medicamentoResource.crearMedicamento(in);
        assertEquals(201, res.getStatus());
        assertEquals(created, res.getEntity());
        verify(medicamentoService).crearMedicamento(in);
    }

    @Test
    void actualizar_ok_y_404() {
        when(medicamentoService.actualizarMedicamento(eq(1L), any(Medicamento.class))).thenReturn(new Medicamento());
        when(medicamentoService.actualizarMedicamento(eq(99L), any(Medicamento.class))).thenReturn(null);

        assertEquals(200, medicamentoResource.actualizarMedicamento(1L, new Medicamento()).getStatus());
        assertEquals(404, medicamentoResource.actualizarMedicamento(99L, new Medicamento()).getStatus());
    }

    @Test
    void eliminar_noContent_y_404() {
        when(medicamentoService.eliminarMedicamento(1L)).thenReturn(true);
        when(medicamentoService.eliminarMedicamento(99L)).thenReturn(false);

        assertEquals(204, medicamentoResource.eliminarMedicamento(1L).getStatus());
        assertEquals(404, medicamentoResource.eliminarMedicamento(99L).getStatus());
    }
}
