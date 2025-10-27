package com.unis.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.unis.model.EstadoCita;

public class CitaTest {

    private Cita cita;

    @BeforeEach
    void setUp() {
        cita = new Cita();
    }

    @Test
    void constructor_deberiaInicializarValoresPorDefecto() {
        // Act
        Cita nuevaCita = new Cita();

        // Assert
        assertNull(nuevaCita.getIdCita());
        assertNull(nuevaCita.getDoctor());
        assertNull(nuevaCita.getPaciente());
        assertNull(nuevaCita.getIdDoctor());
        assertNull(nuevaCita.getIdPaciente());
        assertNull(nuevaCita.getFecha());
        assertNull(nuevaCita.getHoraInicio());
        assertNull(nuevaCita.getHoraFin());
        assertNull(nuevaCita.getMotivo());
        assertNull(nuevaCita.getIdHospital());
        assertNull(nuevaCita.getIdServicio());
        assertNull(nuevaCita.getIdAseguradora());
        assertNull(nuevaCita.getNumeroAutorizacion());
        assertNull(nuevaCita.getDiagnostico());
        assertNull(nuevaCita.getResultados());
        assertNull(nuevaCita.getEstado());
    }

    @Test
    void gettersAndSetters_deberiaFuncionarCorrectamente() {
        // Arrange
        Long idCita = 1L;
        Long idDoctor = 2L;
        Long idPaciente = 3L;
        LocalDate fecha = LocalDate.of(2024, 12, 25);
        String horaInicio = "09:00";
        String horaFin = "10:00";
        String motivo = "Consulta general";
        Long idHospital = 1L;
        Long idServicio = 2L;
        Long idAseguradora = 3L;
        String numeroAutorizacion = "AUTH123";
        String diagnostico = "Gripe común";
        String resultados = "Reposo y medicación";
        EstadoCita estado = EstadoCita.FINALIZADA;

        // Act
        cita.setIdCita(idCita);
        cita.setIdDoctor(idDoctor);
        cita.setIdPaciente(idPaciente);
        cita.setFecha(fecha);
        cita.setHoraInicio(horaInicio);
        cita.setHoraFin(horaFin);
        cita.setMotivo(motivo);
        cita.setIdHospital(idHospital);
        cita.setIdServicio(idServicio);
        cita.setIdAseguradora(idAseguradora);
        cita.setNumeroAutorizacion(numeroAutorizacion);
        cita.setDiagnostico(diagnostico);
        cita.setResultados(resultados);
        cita.setEstado(estado);

        // Assert
        assertEquals(idCita, cita.getIdCita());
        assertEquals(idDoctor, cita.getIdDoctor());
        assertEquals(idPaciente, cita.getIdPaciente());
        assertEquals(fecha, cita.getFecha());
        assertEquals(horaInicio, cita.getHoraInicio());
        assertEquals(horaFin, cita.getHoraFin());
        assertEquals(motivo, cita.getMotivo());
        assertEquals(idHospital, cita.getIdHospital());
        assertEquals(idServicio, cita.getIdServicio());
        assertEquals(idAseguradora, cita.getIdAseguradora());
        assertEquals(numeroAutorizacion, cita.getNumeroAutorizacion());
        assertEquals(diagnostico, cita.getDiagnostico());
        assertEquals(resultados, cita.getResultados());
        assertEquals(estado, cita.getEstado());
    }

    @Test
    void setDoctor_deberiaAsignarDoctor() {
        // Arrange
        Doctor doctor = new Doctor();
        doctor.setIdDoctor(1L);
        doctor.setApellido("García");

        // Act
        cita.setDoctor(doctor);

        // Assert
        assertEquals(doctor, cita.getDoctor());
    }

    @Test
    void setPaciente_deberiaAsignarPaciente() {
        // Arrange
        Paciente paciente = new Paciente();
        paciente.setIdPaciente(1L);
        paciente.setApellido("López");

        // Act
        cita.setPaciente(paciente);

        // Assert
        assertEquals(paciente, cita.getPaciente());
    }

    @Test
    void setIdCita_conValorNegativo_deberiaAsignarValorNegativo() {
        // Arrange
        Long idNegativo = -1L;

        // Act
        cita.setIdCita(idNegativo);

        // Assert
        assertEquals(idNegativo, cita.getIdCita());
    }

    @Test
    void setIdDoctor_conValorCero_deberiaAsignarValorCero() {
        // Arrange
        Long idCero = 0L;

        // Act
        cita.setIdDoctor(idCero);

        // Assert
        assertEquals(idCero, cita.getIdDoctor());
    }

    @Test
    void setEstado_conValorNull_deberiaAsignarNull() {
        // Act
        cita.setEstado(null);

        // Assert
        assertNull(cita.getEstado());
    }
}
