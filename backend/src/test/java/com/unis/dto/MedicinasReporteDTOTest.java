package com.unis.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MedicinasReporteDTOTest {

    private MedicinasReporteDTO medicinasReporteDTO;

    @BeforeEach
    void setUp() {
        medicinasReporteDTO = new MedicinasReporteDTO(1, "Paracetamol", 150);
    }

    @Test
    void constructor_conParametros_deberiaInicializarCampos() {
        // Arrange
        int popularidad = 2;
        String principioActivo = "Ibuprofeno";
        int totalRecetas = 200;

        // Act
        MedicinasReporteDTO nuevoDTO = new MedicinasReporteDTO(popularidad, principioActivo, totalRecetas);

        // Assert
        assertEquals(popularidad, nuevoDTO.popularidad);
        assertEquals(principioActivo, nuevoDTO.principioActivo);
        assertEquals(totalRecetas, nuevoDTO.totalRecetas);
    }

    @Test
    void constructor_conValoresNegativos_deberiaAsignarValoresNegativos() {
        // Arrange
        int popularidad = -1;
        String principioActivo = "Aspirina";
        int totalRecetas = -5;

        // Act
        MedicinasReporteDTO nuevoDTO = new MedicinasReporteDTO(popularidad, principioActivo, totalRecetas);

        // Assert
        assertEquals(popularidad, nuevoDTO.popularidad);
        assertEquals(principioActivo, nuevoDTO.principioActivo);
        assertEquals(totalRecetas, nuevoDTO.totalRecetas);
    }

    @Test
    void constructor_conValoresCero_deberiaAsignarValoresCero() {
        // Arrange
        int popularidad = 0;
        String principioActivo = "Placebo";
        int totalRecetas = 0;

        // Act
        MedicinasReporteDTO nuevoDTO = new MedicinasReporteDTO(popularidad, principioActivo, totalRecetas);

        // Assert
        assertEquals(popularidad, nuevoDTO.popularidad);
        assertEquals(principioActivo, nuevoDTO.principioActivo);
        assertEquals(totalRecetas, nuevoDTO.totalRecetas);
    }

    @Test
    void constructor_conStringNull_deberiaAsignarNull() {
        // Arrange
        int popularidad = 1;
        String principioActivo = null;
        int totalRecetas = 10;

        // Act
        MedicinasReporteDTO nuevoDTO = new MedicinasReporteDTO(popularidad, principioActivo, totalRecetas);

        // Assert
        assertEquals(popularidad, nuevoDTO.popularidad);
        assertNull(nuevoDTO.principioActivo);
        assertEquals(totalRecetas, nuevoDTO.totalRecetas);
    }

    @Test
    void constructor_conStringVacio_deberiaAsignarStringVacio() {
        // Arrange
        int popularidad = 1;
        String principioActivo = "";
        int totalRecetas = 10;

        // Act
        MedicinasReporteDTO nuevoDTO = new MedicinasReporteDTO(popularidad, principioActivo, totalRecetas);

        // Assert
        assertEquals(popularidad, nuevoDTO.popularidad);
        assertEquals("", nuevoDTO.principioActivo);
        assertEquals(totalRecetas, nuevoDTO.totalRecetas);
    }

    @Test
    void campos_deberianSerPublicos() {
        // Arrange
        int nuevaPopularidad = 5;
        String nuevoPrincipioActivo = "Omeprazol";
        int nuevoTotalRecetas = 300;

        // Act
        medicinasReporteDTO.popularidad = nuevaPopularidad;
        medicinasReporteDTO.principioActivo = nuevoPrincipioActivo;
        medicinasReporteDTO.totalRecetas = nuevoTotalRecetas;

        // Assert
        assertEquals(nuevaPopularidad, medicinasReporteDTO.popularidad);
        assertEquals(nuevoPrincipioActivo, medicinasReporteDTO.principioActivo);
        assertEquals(nuevoTotalRecetas, medicinasReporteDTO.totalRecetas);
    }

    @Test
    void constructor_conValoresMaximos_deberiaAsignarValoresMaximos() {
        // Arrange
        int popularidad = Integer.MAX_VALUE;
        String principioActivo = "Medicamento de prueba muy largo";
        int totalRecetas = Integer.MAX_VALUE;

        // Act
        MedicinasReporteDTO nuevoDTO = new MedicinasReporteDTO(popularidad, principioActivo, totalRecetas);

        // Assert
        assertEquals(popularidad, nuevoDTO.popularidad);
        assertEquals(principioActivo, nuevoDTO.principioActivo);
        assertEquals(totalRecetas, nuevoDTO.totalRecetas);
    }
}
