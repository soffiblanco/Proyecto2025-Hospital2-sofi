package com.unis.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.unis.model.Cita;
import com.unis.model.Doctor;
import com.unis.model.EstadoCita;
import com.unis.model.Paciente;
import com.unis.repository.CitaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;

class CitaServiceExtraTest {

  @Mock CitaRepository citaRepository;
  @Mock DoctorService doctorService;
  @Mock EntityManager entityManager;

  @InjectMocks CitaService citaService;

  @BeforeEach void init() { MockitoAnnotations.openMocks(this); }

  @Test
  void obtener_listar_y_por_id() {
    when(citaRepository.listAll()).thenReturn(List.of(new Cita()));
    assertEquals(1, citaService.obtenerCitas().size());

    Cita c = new Cita();
    when(citaRepository.findById(1L)).thenReturn(c);
    assertEquals(c, citaService.obtenerCitaPorId(1L));
  }

  @Test
  void buscar_doctor_por_id_optional() {
    Doctor d = new Doctor();
    when(doctorService.getDoctorById(7L)).thenReturn(Optional.of(d));
    assertEquals(d, citaService.buscarDoctorPorId(7L));
  }

  @Test
  void agendar_falta_ids_lanza() {
    Cita c = new Cita();
    assertThrows(IllegalArgumentException.class, () -> citaService.agendarCita(c));
  }

  @Test
  void agendar_doctor_o_paciente_no_encontrado_lanza() {
    Cita c = new Cita();
    c.setIdDoctor(1L); c.setIdPaciente(2L);
    when(entityManager.find(Doctor.class, 1L)).thenReturn(null);
    when(entityManager.find(Paciente.class, 2L)).thenReturn(new Paciente());
    assertThrows(IllegalArgumentException.class, () -> citaService.agendarCita(c));
  }

  @Test
  void agendar_ok_persiste() {
    Cita c = new Cita();
    c.setIdDoctor(1L); c.setIdPaciente(2L);
    when(entityManager.find(Doctor.class, 1L)).thenReturn(new Doctor());
    when(entityManager.find(Paciente.class, 2L)).thenReturn(new Paciente());
    doNothing().when(citaRepository).persist(c);
    assertDoesNotThrow(() -> citaService.agendarCita(c));
    verify(citaRepository).persist(c);
  }

  @Test
  void cancelar_ok_y_no_encontrada() {
    Cita c = new Cita();
    when(citaRepository.findById(1L)).thenReturn(c);
    citaService.cancelarCita(1L);
    assertEquals(EstadoCita.CANCELADA, c.getEstado());

    when(citaRepository.findById(99L)).thenReturn(null);
    assertThrows(IllegalArgumentException.class, () -> citaService.cancelarCita(99L));
  }

  @Test
  void actualizar_ok_y_no_encontrada() {
    Cita existente = new Cita();
    when(citaRepository.findById(1L)).thenReturn(existente);
    Cita in = new Cita(); in.setDiagnostico("dx"); in.setResultados("rs"); in.setEstado(EstadoCita.CONFIRMADA);
    citaService.actualizarCita(1L, in);
    assertEquals("dx", existente.getDiagnostico());

    when(citaRepository.findById(99L)).thenReturn(null);
    assertThrows(IllegalArgumentException.class, () -> citaService.actualizarCita(99L, new Cita()));
  }

  @Test
  void procesar_ok_y_no_encontrada() {
    Cita c = new Cita();
    when(citaRepository.findById(1L)).thenReturn(c);
    citaService.procesarCita(1L);
    assertEquals(EstadoCita.FINALIZADA, c.getEstado());

    when(citaRepository.findById(99L)).thenReturn(null);
    assertThrows(IllegalArgumentException.class, () -> citaService.procesarCita(99L));
  }

  @Test
  void crearDesdeJson_documento_faltante_lanza() {
    jakarta.json.JsonObject dto = jakarta.json.Json.createObjectBuilder().build();
    assertThrows(IllegalArgumentException.class, () -> citaService.crearCitaDesdeJson(dto));
  }
}
