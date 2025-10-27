package com.unis.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.unis.model.Paciente;
import com.unis.service.PacienteService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.ws.rs.core.Response;

class PacienteResourceTest {

  @Mock
  PacienteService pacienteService;

  @InjectMocks
  PacienteResource pacienteResource;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void listar_vacio_ok() {
    when(pacienteService.getAllPacientes()).thenReturn(List.of());
    List<Paciente> out = pacienteResource.obtenerTodosLosPacientes();
    assertEquals(0, out.size());
  }

  @Test
  void listar_con_elementos_ok() {
    when(pacienteService.getAllPacientes()).thenReturn(List.of(new Paciente()));
    List<Paciente> out = pacienteResource.obtenerTodosLosPacientes();
    assertTrue(out.size() >= 1);
  }

  @Test
  void obtener_por_id_ok_y_404() {
    when(pacienteService.getPacienteById(1L)).thenReturn(Optional.of(new Paciente()));
    Response ok = pacienteResource.obtenerPaciente(1L);
    assertEquals(200, ok.getStatus());

    when(pacienteService.getPacienteById(99L)).thenReturn(Optional.empty());
    Response nf = pacienteResource.obtenerPaciente(99L);
    assertEquals(404, nf.getStatus());
  }

  @Test
  void registrar_crea_201() {
    doNothing().when(pacienteService).registrarPaciente(any(Paciente.class));
    Response res = pacienteResource.registrarPaciente(new Paciente());
    assertEquals(201, res.getStatus());
    verify(pacienteService).registrarPaciente(any(Paciente.class));
  }

  @Test
  void actualizar_ok_y_404() {
    when(pacienteService.actualizarPaciente(eq(1L), any(Paciente.class))).thenReturn(true);
    when(pacienteService.actualizarPaciente(eq(99L), any(Paciente.class))).thenReturn(false);

    assertEquals(200, pacienteResource.actualizarPaciente(1L, new Paciente()).getStatus());
    assertEquals(404, pacienteResource.actualizarPaciente(99L, new Paciente()).getStatus());
  }

  @Test
  void eliminar_ok_y_404() {
    when(pacienteService.eliminarPaciente(1L)).thenReturn(true);
    when(pacienteService.eliminarPaciente(99L)).thenReturn(false);

    assertEquals(200, pacienteResource.eliminarPaciente(1L).getStatus());
    assertEquals(404, pacienteResource.eliminarPaciente(99L).getStatus());
    verify(pacienteService).eliminarPaciente(1L);
  }
}
