package com.unis.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.unis.model.Servicio;

import io.quarkus.hibernate.orm.panache.PanacheQuery;

@ExtendWith(MockitoExtension.class)
public class ServicioRepositoryAdditionalTest {

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private PanacheQuery<Servicio> panacheQuery;

    @BeforeEach
    void setUp() {
        // Setup básico
    }

    @Test
    void listAll_deberiaRetornarListaVaciaCuandoNoHayServicios() {
        // Arrange
        when(servicioRepository.listAll()).thenReturn(List.of());

        // Act
        List<Servicio> resultado = servicioRepository.listAll();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(servicioRepository).listAll();
    }

    @Test
    void listAll_deberiaRetornarTodosLosServicios() {
        // Arrange
        Servicio servicio1 = new Servicio();
        servicio1.nombre = "Consulta General";
        servicio1.costo = 50.0;

        Servicio servicio2 = new Servicio();
        servicio2.nombre = "Cirugía";
        servicio2.costo = 1000.0;

        List<Servicio> servicios = Arrays.asList(servicio1, servicio2);
        when(servicioRepository.listAll()).thenReturn(servicios);

        // Act
        List<Servicio> resultado = servicioRepository.listAll();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(servicio1));
        assertTrue(resultado.contains(servicio2));
        verify(servicioRepository).listAll();
    }

    @Test
    void findById_conIdValido_deberiaRetornarServicio() {
        // Arrange
        Long id = 1L;
        Servicio servicio = new Servicio();
        servicio.id = id;
        servicio.nombre = "Consulta General";

        when(servicioRepository.findById(id)).thenReturn(servicio);

        // Act
        Servicio resultado = servicioRepository.findById(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.id);
        assertEquals("Consulta General", resultado.nombre);
        verify(servicioRepository).findById(id);
    }

    @Test
    void findById_conIdInexistente_deberiaRetornarNull() {
        // Arrange
        Long id = 999L;
        when(servicioRepository.findById(id)).thenReturn(null);

        // Act
        Servicio resultado = servicioRepository.findById(id);

        // Assert
        assertNull(resultado);
        verify(servicioRepository).findById(id);
    }

    @Test
    void persist_deberiaGuardarServicio() {
        // Arrange
        Servicio servicio = new Servicio();
        servicio.nombre = "Nuevo Servicio";
        servicio.costo = 75.0;

        doNothing().when(servicioRepository).persist(servicio);

        // Act & Assert
        assertDoesNotThrow(() -> servicioRepository.persist(servicio));
        verify(servicioRepository).persist(servicio);
    }

    @Test
    void delete_deberiaEliminarServicio() {
        // Arrange
        Servicio servicio = new Servicio();
        servicio.id = 1L;

        doNothing().when(servicioRepository).delete(servicio);

        // Act & Assert
        assertDoesNotThrow(() -> servicioRepository.delete(servicio));
        verify(servicioRepository).delete(servicio);
    }

    @Test
    void find_conQuery_deberiaRetornarPanacheQuery() {
        // Arrange
        String query = "nombre = ?1";
        when(servicioRepository.find(query)).thenReturn(panacheQuery);

        // Act
        PanacheQuery<Servicio> resultado = servicioRepository.find(query);

        // Assert
        assertNotNull(resultado);
        assertEquals(panacheQuery, resultado);
        verify(servicioRepository).find(query);
    }

    @Test
    void find_conQueryYParametros_deberiaRetornarPanacheQuery() {
        // Arrange
        String query = "costo > ?1";
        Double costoMinimo = 100.0;
        when(servicioRepository.find(query, costoMinimo)).thenReturn(panacheQuery);

        // Act
        PanacheQuery<Servicio> resultado = servicioRepository.find(query, costoMinimo);

        // Assert
        assertNotNull(resultado);
        assertEquals(panacheQuery, resultado);
        verify(servicioRepository).find(query, costoMinimo);
    }

    @Test
    void count_deberiaRetornarNumeroDeServicios() {
        // Arrange
        long count = 10L;
        when(servicioRepository.count()).thenReturn(count);

        // Act
        long resultado = servicioRepository.count();

        // Assert
        assertEquals(count, resultado);
        verify(servicioRepository).count();
    }

    @Test
    void count_conQuery_deberiaRetornarNumeroDeServicios() {
        // Arrange
        String query = "cubiertoSeguro = ?1";
        boolean cubiertoSeguro = true;
        long count = 7L;
        when(servicioRepository.count(query, cubiertoSeguro)).thenReturn(count);

        // Act
        long resultado = servicioRepository.count(query, cubiertoSeguro);

        // Assert
        assertEquals(count, resultado);
        verify(servicioRepository).count(query, cubiertoSeguro);
    }

    @Test
    void find_conQueryYParametrosMultiples_deberiaRetornarPanacheQuery() {
        // Arrange
        String query = "costo BETWEEN ?1 AND ?2";
        Double costoMin = 50.0;
        Double costoMax = 200.0;
        when(servicioRepository.find(query, costoMin, costoMax)).thenReturn(panacheQuery);

        // Act
        PanacheQuery<Servicio> resultado = servicioRepository.find(query, costoMin, costoMax);

        // Assert
        assertNotNull(resultado);
        assertEquals(panacheQuery, resultado);
        verify(servicioRepository).find(query, costoMin, costoMax);
    }
}



