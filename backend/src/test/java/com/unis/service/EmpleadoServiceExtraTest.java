package com.unis.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.unis.model.Empleado;
import com.unis.repository.EmpleadoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;
import jakarta.ws.rs.WebApplicationException;

class EmpleadoServiceExtraTest {

  @Mock EmpleadoRepository repo;
  @Mock EntityManager em;
  @InjectMocks EmpleadoService service;

  @BeforeEach void init() { MockitoAnnotations.openMocks(this); }

  @Test
  void listar_y_por_id() {
    when(repo.listAll()).thenReturn(List.of(new Empleado()));
    assertEquals(1, service.getAllEmpleados().size());

    Empleado e = new Empleado();
    when(repo.findByIdOptional(1L)).thenReturn(Optional.of(e));
    assertTrue(service.getEmpleadoById(1L).isPresent());
  }

  @Test
  void registrar_y_actualizar_y_eliminar_manejan_excepciones() {
    // registrar: simulamos excepción con mensaje ORA-20001
    doThrow(new RuntimeException("ORA-20001: Duplicate email"))
        .when(em).createNativeQuery(anyString());
    assertThrows(WebApplicationException.class, () -> service.registrarEmpleado(new Empleado()));

    // actualizar: no lanza
    assertDoesNotThrow(() -> service.actualizarEmpleado(1L, new Empleado()));

    // eliminar: no lanza
    assertDoesNotThrow(() -> service.eliminarEmpleado(1L));
  }
}
