package com.unis.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.unis.model.SolicitudHospital;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SolicitudHospitalServiceTest {

  @Mock SolicitudHospitalAPI aseguradoraClient;
  @InjectMocks SolicitudHospitalService service;

  @BeforeEach void init() { MockitoAnnotations.openMocks(this); }

  private SolicitudHospital sol(String aseguradora) {
    SolicitudHospital s = new SolicitudHospital();
    s.nombre = "Hosp"; s.direccion = "Dir"; s.telefono = "123"; s.aseguradora = aseguradora; s.estado = "NUEVA"; s.origen = "test";
    return s;
  }

  @Test
  void datos_incompletos_no_envia() {
    SolicitudHospital s = new SolicitudHospital();
    service.enviarSolicitud(s); // no debería lanzar
    verifyNoInteractions(aseguradoraClient);
  }

  @Test
  void envia_a_aseguradora_y_mongo() {
    doNothing().when(aseguradoraClient).enviarSolicitud(any());
    service.enviarSolicitud(sol("Aseguradora Uno"));
    verify(aseguradoraClient).enviarSolicitud(any());
  }

  @Test
  void actualizar_estado_no_explota() {
    service.actualizarEstado("id-1", "APROBADA");
    // método usa HttpURLConnection; probamos que no lanza excepción
    assertTrue(true);
  }
}
