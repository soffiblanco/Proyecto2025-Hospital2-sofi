package com.unis.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioCorrectTest {

    private Servicio servicio;

    @BeforeEach
    void setUp() {
        servicio = new Servicio();
    }

    @Test
    void constructor_deberiaInicializarValoresPorDefecto() {
        // Act
        Servicio nuevoServicio = new Servicio();

        // Assert
        assertNull(nuevoServicio.nombre);
        assertEquals(0.0, nuevoServicio.costo);
        assertFalse(nuevoServicio.cubiertoSeguro);
        assertNull(nuevoServicio.servicioPadre);
        assertNotNull(nuevoServicio.subServicios);
        assertTrue(nuevoServicio.subServicios.isEmpty());
    }

    @Test
    void setNombre_deberiaAsignarNombre() {
        // Arrange
        String nombre = "Consulta General";

        // Act
        servicio.nombre = nombre;

        // Assert
        assertEquals(nombre, servicio.nombre);
    }

    @Test
    void setCosto_deberiaAsignarCosto() {
        // Arrange
        double costo = 50.0;

        // Act
        servicio.costo = costo;

        // Assert
        assertEquals(costo, servicio.costo);
    }

    @Test
    void setCubiertoSeguro_deberiaAsignarCubiertoSeguro() {
        // Arrange
        boolean cubiertoSeguro = true;

        // Act
        servicio.cubiertoSeguro = cubiertoSeguro;

        // Assert
        assertTrue(servicio.cubiertoSeguro);
    }

    @Test
    void setServicioPadre_deberiaAsignarServicioPadre() {
        // Arrange
        Servicio servicioPadre = new Servicio();
        servicioPadre.nombre = "Servicio Padre";

        // Act
        servicio.servicioPadre = servicioPadre;

        // Assert
        assertEquals(servicioPadre, servicio.servicioPadre);
    }

    @Test
    void setSubServicios_deberiaAsignarSubServicios() {
        // Arrange
        Set<Servicio> subServicios = new LinkedHashSet<>();
        Servicio sub1 = new Servicio();
        sub1.nombre = "Sub Servicio 1";
        subServicios.add(sub1);

        // Act
        servicio.subServicios = subServicios;

        // Assert
        assertEquals(subServicios, servicio.subServicios);
        assertTrue(servicio.subServicios.contains(sub1));
    }

    @Test
    void getParentId_conServicioPadreNull_deberiaRetornarNull() {
        // Arrange
        servicio.servicioPadre = null;

        // Act
        Long parentId = servicio.getParentId();

        // Assert
        assertNull(parentId);
    }

    @Test
    void getParentId_conServicioPadreValido_deberiaRetornarIdDelPadre() {
        // Arrange
        Servicio servicioPadre = new Servicio();
        servicioPadre.id = 1L;
        servicio.servicioPadre = servicioPadre;

        // Act
        Long parentId = servicio.getParentId();

        // Assert
        assertEquals(1L, parentId);
    }

    @Test
    void setNombre_conStringNull_deberiaAsignarNull() {
        // Act
        servicio.nombre = null;

        // Assert
        assertNull(servicio.nombre);
    }

    @Test
    void setNombre_conStringVacio_deberiaAsignarStringVacio() {
        // Arrange
        String nombreVacio = "";

        // Act
        servicio.nombre = nombreVacio;

        // Assert
        assertEquals(nombreVacio, servicio.nombre);
    }

    @Test
    void setCosto_conValorNegativo_deberiaAsignarValorNegativo() {
        // Arrange
        double costoNegativo = -10.0;

        // Act
        servicio.costo = costoNegativo;

        // Assert
        assertEquals(costoNegativo, servicio.costo);
    }

    @Test
    void setCosto_conValorCero_deberiaAsignarValorCero() {
        // Arrange
        double costoCero = 0.0;

        // Act
        servicio.costo = costoCero;

        // Assert
        assertEquals(costoCero, servicio.costo);
    }

    @Test
    void setServicioPadre_conServicioNull_deberiaAsignarNull() {
        // Act
        servicio.servicioPadre = null;

        // Assert
        assertNull(servicio.servicioPadre);
    }

    @Test
    void setSubServicios_conSetNull_deberiaAsignarNull() {
        // Act
        servicio.subServicios = null;

        // Assert
        assertNull(servicio.subServicios);
    }

    @Test
    void setSubServicios_conSetVacio_deberiaAsignarSetVacio() {
        // Arrange
        Set<Servicio> subServiciosVacios = new LinkedHashSet<>();

        // Act
        servicio.subServicios = subServiciosVacios;

        // Assert
        assertNotNull(servicio.subServicios);
        assertTrue(servicio.subServicios.isEmpty());
    }

    @Test
    void campos_deberianSerPublicos() {
        // Arrange
        String nombre = "Cirugía";
        double costo = 1000.0;
        boolean cubiertoSeguro = true;
        Servicio servicioPadre = new Servicio();
        Set<Servicio> subServicios = new LinkedHashSet<>();

        // Act
        servicio.nombre = nombre;
        servicio.costo = costo;
        servicio.cubiertoSeguro = cubiertoSeguro;
        servicio.servicioPadre = servicioPadre;
        servicio.subServicios = subServicios;

        // Assert
        assertEquals(nombre, servicio.nombre);
        assertEquals(costo, servicio.costo);
        assertEquals(cubiertoSeguro, servicio.cubiertoSeguro);
        assertEquals(servicioPadre, servicio.servicioPadre);
        assertEquals(subServicios, servicio.subServicios);
    }
}
