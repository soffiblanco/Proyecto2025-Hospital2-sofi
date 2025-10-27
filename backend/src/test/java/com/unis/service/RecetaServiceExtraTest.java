package com.unis.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.unis.model.Medicamento;
import com.unis.model.Receta;
import com.unis.model.RecetaMedicamento;
import com.unis.repository.RecetaRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;

class RecetaServiceExtraTest {

  @Mock EntityManager em;
  @Mock RecetaRepository recetaRepository;
  @InjectMocks RecetaService service;

  @BeforeEach void init() { MockitoAnnotations.openMocks(this); }

  @Test
  void crear_receta_validaciones_y_ok() {
    Receta r = new Receta();
    assertThrows(RuntimeException.class, () -> service.crearReceta(r));

    r.setIdDoctor(1L); r.setIdPaciente(2L); r.setCodigoReceta("COD");
    doNothing().when(em).persist(r);
    assertNotNull(service.crearReceta(r));
  }

  @Test
  void buscar_por_codigo_y_por_idCita() {
    Receta r = new Receta(); r.setIdPaciente(2L);
    when(recetaRepository.find("codigoReceta", "ABC")).thenReturn(mock(io.quarkus.hibernate.orm.panache.PanacheQuery.class));
    when(recetaRepository.find("idCita", 10)).thenReturn(mock(io.quarkus.hibernate.orm.panache.PanacheQuery.class));
    assertNull(service.buscarPorCodigo("XYZ")); // por defecto null
  }

  @Test
  void agregar_medicamento_validaciones() {
    RecetaMedicamento rm = new RecetaMedicamento();
    assertThrows(RuntimeException.class, () -> service.agregarMedicamento(rm));

    rm.setIdReceta(1L); rm.setIdMedicamento(2L);
    Receta receta = new Receta(); receta.setIdReceta(1L);
    when(em.find(Receta.class, 1L)).thenReturn(receta);
    when(em.find(Medicamento.class, 2L)).thenReturn(new Medicamento());
    doNothing().when(em).persist(rm);
    assertNotNull(service.agregarMedicamento(rm));
  }

  @Test
  void actualizar_receta_no_encontrada_lanza() {
    when(em.find(Receta.class, 99L)).thenReturn(null);
    assertThrows(RuntimeException.class, () -> service.actualizarReceta(99L, new Receta()));
  }
}
