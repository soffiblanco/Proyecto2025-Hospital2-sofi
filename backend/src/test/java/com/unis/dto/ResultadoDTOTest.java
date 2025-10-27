package com.unis.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ResultadoDTOTest {

    private ResultadoDTO resultadoDTO;

    @BeforeEach
    void setUp() {
        resultadoDTO = new ResultadoDTO();
    }

    @Test
    void constructor_deberiaInicializarCamposComoNull() {
        // Act
        ResultadoDTO nuevoResultadoDTO = new ResultadoDTO();

        // Assert
        assertNull(nuevoResultadoDTO.documento);
        assertNull(nuevoResultadoDTO.diagnostico);
        assertNull(nuevoResultadoDTO.resultados);
        assertNull(nuevoResultadoDTO.fecha);
        assertNull(nuevoResultadoDTO.idCita);
    }

    @Test
    void setDocumento_deberiaAsignarDocumento() {
        // Arrange
        String documento = "1234567890123";

        // Act
        resultadoDTO.documento = documento;

        // Assert
        assertEquals(documento, resultadoDTO.documento);
    }

    @Test
    void setDiagnostico_deberiaAsignarDiagnostico() {
        // Arrange
        String diagnostico = "Gripe común";

        // Act
        resultadoDTO.diagnostico = diagnostico;

        // Assert
        assertEquals(diagnostico, resultadoDTO.diagnostico);
    }

    @Test
    void setResultados_deberiaAsignarResultados() {
        // Arrange
        String resultados = "Paciente presenta síntomas de gripe, se recomienda reposo";

        // Act
        resultadoDTO.resultados = resultados;

        // Assert
        assertEquals(resultados, resultadoDTO.resultados);
    }

    @Test
    void setFecha_deberiaAsignarFecha() {
        // Arrange
        LocalDate fecha = LocalDate.of(2024, 12, 25);

        // Act
        resultadoDTO.fecha = fecha;

        // Assert
        assertEquals(fecha, resultadoDTO.fecha);
    }

    @Test
    void setIdCita_deberiaAsignarIdCita() {
        // Arrange
        Long idCita = 1L;

        // Act
        resultadoDTO.idCita = idCita;

        // Assert
        assertEquals(idCita, resultadoDTO.idCita);
    }

    @Test
    void campos_deberianSerPublicos() {
        // Arrange
        String documento = "1234567890123";
        String diagnostico = "Hipertensión";
        String resultados = "Presión arterial elevada";
        LocalDate fecha = LocalDate.now();
        Long idCita = 2L;

        // Act
        resultadoDTO.documento = documento;
        resultadoDTO.diagnostico = diagnostico;
        resultadoDTO.resultados = resultados;
        resultadoDTO.fecha = fecha;
        resultadoDTO.idCita = idCita;

        // Assert
        assertEquals(documento, resultadoDTO.documento);
        assertEquals(diagnostico, resultadoDTO.diagnostico);
        assertEquals(resultados, resultadoDTO.resultados);
        assertEquals(fecha, resultadoDTO.fecha);
        assertEquals(idCita, resultadoDTO.idCita);
    }
}
