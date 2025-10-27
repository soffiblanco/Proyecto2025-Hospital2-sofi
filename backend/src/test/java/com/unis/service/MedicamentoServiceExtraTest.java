package com.unis.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.unis.model.Medicamento;
import com.unis.repository.MedicamentoRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MedicamentoServiceExtraTest {

  @Mock MedicamentoRepository repo;
  @InjectMocks MedicamentoService service;

  @BeforeEach void init() { MockitoAnnotations.openMocks(this); }

  @Test
  void listar_y_por_id() {
    when(repo.listAll()).thenReturn(List.of(new Medicamento()));
    assertEquals(1, service.listarTodos().size());

    Medicamento m = new Medicamento();
    when(repo.findById(1L)).thenReturn(m);
    assertEquals(m, service.obtenerPorId(1L));
  }

  @Test
  void crear_actualizar_eliminar() {
    Medicamento in = new Medicamento();
    doNothing().when(repo).persist(in);
    assertEquals(in, service.crearMedicamento(in));

    Medicamento existente = new Medicamento();
    when(repo.findById(1L)).thenReturn(existente);
    Medicamento nuevo = new Medicamento(); nuevo.setPrincipioActivo("ibup");
    assertNotNull(service.actualizarMedicamento(1L, nuevo));

    when(repo.findById(99L)).thenReturn(null);
    assertNull(service.actualizarMedicamento(99L, nuevo));

    when(repo.deleteById(5L)).thenReturn(true);
    assertTrue(service.eliminarMedicamento(5L));
  }
}
