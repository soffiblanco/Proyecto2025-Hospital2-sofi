package com.unis.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ModeracionReporteDTOTest {

    private ModeracionReporteDTO moderacionReporteDTO;

    @BeforeEach
    void setUp() {
        moderacionReporteDTO = new ModeracionReporteDTO();
    }

    @Test
    void constructorPorDefecto_deberiaInicializarConValoresPorDefecto() {
        // Act
        ModeracionReporteDTO nuevoDTO = new ModeracionReporteDTO();

        // Assert
        assertEquals(0, nuevoDTO.getNumeroOrden());
        assertNull(nuevoDTO.getUsuario());
        assertEquals(0, nuevoDTO.getTotalRechazos());
    }

    @Test
    void constructorConParametros_deberiaInicializarCampos() {
        // Arrange
        int numeroOrden = 1;
        String usuario = "admin";
        int totalRechazos = 5;

        // Act
        ModeracionReporteDTO nuevoDTO = new ModeracionReporteDTO(numeroOrden, usuario, totalRechazos);

        // Assert
        assertEquals(numeroOrden, nuevoDTO.getNumeroOrden());
        assertEquals(usuario, nuevoDTO.getUsuario());
        assertEquals(totalRechazos, nuevoDTO.getTotalRechazos());
    }

    @Test
    void gettersAndSetters_deberiaFuncionarCorrectamente() {
        // Arrange
        int numeroOrden = 2;
        String usuario = "moderador";
        int totalRechazos = 10;

        // Act
        moderacionReporteDTO.setNumeroOrden(numeroOrden);
        moderacionReporteDTO.setUsuario(usuario);
        moderacionReporteDTO.setTotalRechazos(totalRechazos);

        // Assert
        assertEquals(numeroOrden, moderacionReporteDTO.getNumeroOrden());
        assertEquals(usuario, moderacionReporteDTO.getUsuario());
        assertEquals(totalRechazos, moderacionReporteDTO.getTotalRechazos());
    }

    @Test
    void setNumeroOrden_conValorNegativo_deberiaAsignarValorNegativo() {
        // Arrange
        int numeroOrdenNegativo = -1;

        // Act
        moderacionReporteDTO.setNumeroOrden(numeroOrdenNegativo);

        // Assert
        assertEquals(numeroOrdenNegativo, moderacionReporteDTO.getNumeroOrden());
    }

    @Test
    void setTotalRechazos_conValorCero_deberiaAsignarValorCero() {
        // Arrange
        int totalRechazosCero = 0;

        // Act
        moderacionReporteDTO.setTotalRechazos(totalRechazosCero);

        // Assert
        assertEquals(totalRechazosCero, moderacionReporteDTO.getTotalRechazos());
    }

    @Test
    void setUsuario_conValorNull_deberiaAsignarNull() {
        // Act
        moderacionReporteDTO.setUsuario(null);

        // Assert
        assertNull(moderacionReporteDTO.getUsuario());
    }

    @Test
    void setUsuario_conStringVacio_deberiaAsignarStringVacio() {
        // Arrange
        String usuarioVacio = "";

        // Act
        moderacionReporteDTO.setUsuario(usuarioVacio);

        // Assert
        assertEquals(usuarioVacio, moderacionReporteDTO.getUsuario());
    }

    @Test
    void constructor_conValoresMaximos_deberiaAsignarValoresMaximos() {
        // Arrange
        int numeroOrden = Integer.MAX_VALUE;
        String usuario = "usuario_maximo";
        int totalRechazos = Integer.MAX_VALUE;

        // Act
        ModeracionReporteDTO nuevoDTO = new ModeracionReporteDTO(numeroOrden, usuario, totalRechazos);

        // Assert
        assertEquals(numeroOrden, nuevoDTO.getNumeroOrden());
        assertEquals(usuario, nuevoDTO.getUsuario());
        assertEquals(totalRechazos, nuevoDTO.getTotalRechazos());
    }

    @Test
    void setNumeroOrden_conValorCero_deberiaAsignarValorCero() {
        // Arrange
        int numeroOrdenCero = 0;

        // Act
        moderacionReporteDTO.setNumeroOrden(numeroOrdenCero);

        // Assert
        assertEquals(numeroOrdenCero, moderacionReporteDTO.getNumeroOrden());
    }

    @Test
    void setTotalRechazos_conValorNegativo_deberiaAsignarValorNegativo() {
        // Arrange
        int totalRechazosNegativo = -5;

        // Act
        moderacionReporteDTO.setTotalRechazos(totalRechazosNegativo);

        // Assert
        assertEquals(totalRechazosNegativo, moderacionReporteDTO.getTotalRechazos());
    }
}
