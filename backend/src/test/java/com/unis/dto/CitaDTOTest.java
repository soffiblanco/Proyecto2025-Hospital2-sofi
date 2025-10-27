package com.unis.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CitaDTOTest {

    private CitaDTO citaDTO;

    @BeforeEach
    void setUp() {
        citaDTO = new CitaDTO();
    }

    @Test
    void constructor_deberiaInicializarCamposComoNull() {
        // Act
        CitaDTO nuevoCitaDTO = new CitaDTO();

        // Assert
        assertNull(nuevoCitaDTO.dpi);
        assertNull(nuevoCitaDTO.nombre);
        assertNull(nuevoCitaDTO.apellido);
        assertNull(nuevoCitaDTO.fecha);
        assertNull(nuevoCitaDTO.horaInicio);
        assertNull(nuevoCitaDTO.horaFin);
        assertNull(nuevoCitaDTO.motivo);
        assertNull(nuevoCitaDTO.idHospital);
        assertNull(nuevoCitaDTO.idServicio);
        assertNull(nuevoCitaDTO.idAseguradora);
        assertNull(nuevoCitaDTO.numeroAutorizacion);
    }

    @Test
    void setDpi_deberiaAsignarDpi() {
        // Arrange
        String dpi = "1234567890123";

        // Act
        citaDTO.dpi = dpi;

        // Assert
        assertEquals(dpi, citaDTO.dpi);
    }

    @Test
    void setNombre_deberiaAsignarNombre() {
        // Arrange
        String nombre = "Juan";

        // Act
        citaDTO.nombre = nombre;

        // Assert
        assertEquals(nombre, citaDTO.nombre);
    }

    @Test
    void setApellido_deberiaAsignarApellido() {
        // Arrange
        String apellido = "Pérez";

        // Act
        citaDTO.apellido = apellido;

        // Assert
        assertEquals(apellido, citaDTO.apellido);
    }

    @Test
    void setFecha_deberiaAsignarFecha() {
        // Arrange
        LocalDate fecha = LocalDate.of(2024, 12, 25);

        // Act
        citaDTO.fecha = fecha;

        // Assert
        assertEquals(fecha, citaDTO.fecha);
    }

    @Test
    void setHoraInicio_deberiaAsignarHoraInicio() {
        // Arrange
        String horaInicio = "09:00";

        // Act
        citaDTO.horaInicio = horaInicio;

        // Assert
        assertEquals(horaInicio, citaDTO.horaInicio);
    }

    @Test
    void setHoraFin_deberiaAsignarHoraFin() {
        // Arrange
        String horaFin = "10:00";

        // Act
        citaDTO.horaFin = horaFin;

        // Assert
        assertEquals(horaFin, citaDTO.horaFin);
    }

    @Test
    void setMotivo_deberiaAsignarMotivo() {
        // Arrange
        String motivo = "Consulta general";

        // Act
        citaDTO.motivo = motivo;

        // Assert
        assertEquals(motivo, citaDTO.motivo);
    }

    @Test
    void setIdHospital_deberiaAsignarIdHospital() {
        // Arrange
        Long idHospital = 1L;

        // Act
        citaDTO.idHospital = idHospital;

        // Assert
        assertEquals(idHospital, citaDTO.idHospital);
    }

    @Test
    void setIdServicio_deberiaAsignarIdServicio() {
        // Arrange
        Long idServicio = 2L;

        // Act
        citaDTO.idServicio = idServicio;

        // Assert
        assertEquals(idServicio, citaDTO.idServicio);
    }

    @Test
    void setIdAseguradora_deberiaAsignarIdAseguradora() {
        // Arrange
        Long idAseguradora = 3L;

        // Act
        citaDTO.idAseguradora = idAseguradora;

        // Assert
        assertEquals(idAseguradora, citaDTO.idAseguradora);
    }

    @Test
    void setNumeroAutorizacion_deberiaAsignarNumeroAutorizacion() {
        // Arrange
        String numeroAutorizacion = "AUTH123456";

        // Act
        citaDTO.numeroAutorizacion = numeroAutorizacion;

        // Assert
        assertEquals(numeroAutorizacion, citaDTO.numeroAutorizacion);
    }

    @Test
    void campos_deberianSerPublicos() {
        // Arrange
        String dpi = "1234567890123";
        String nombre = "María";
        String apellido = "González";
        LocalDate fecha = LocalDate.now();
        String horaInicio = "14:00";
        String horaFin = "15:00";
        String motivo = "Revisión";
        Long idHospital = 1L;
        Long idServicio = 2L;
        Long idAseguradora = 3L;
        String numeroAutorizacion = "AUTH789";

        // Act
        citaDTO.dpi = dpi;
        citaDTO.nombre = nombre;
        citaDTO.apellido = apellido;
        citaDTO.fecha = fecha;
        citaDTO.horaInicio = horaInicio;
        citaDTO.horaFin = horaFin;
        citaDTO.motivo = motivo;
        citaDTO.idHospital = idHospital;
        citaDTO.idServicio = idServicio;
        citaDTO.idAseguradora = idAseguradora;
        citaDTO.numeroAutorizacion = numeroAutorizacion;

        // Assert
        assertEquals(dpi, citaDTO.dpi);
        assertEquals(nombre, citaDTO.nombre);
        assertEquals(apellido, citaDTO.apellido);
        assertEquals(fecha, citaDTO.fecha);
        assertEquals(horaInicio, citaDTO.horaInicio);
        assertEquals(horaFin, citaDTO.horaFin);
        assertEquals(motivo, citaDTO.motivo);
        assertEquals(idHospital, citaDTO.idHospital);
        assertEquals(idServicio, citaDTO.idServicio);
        assertEquals(idAseguradora, citaDTO.idAseguradora);
        assertEquals(numeroAutorizacion, citaDTO.numeroAutorizacion);
    }
}
