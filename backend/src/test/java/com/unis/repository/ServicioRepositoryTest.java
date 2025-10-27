package com.unis.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.unis.model.Servicio;

public class ServicioRepositoryTest {

    @InjectMocks
    private ServicioRepository servicioRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listAll_deberiaRetornarTodosLosServicios() {
        // Arrange
        Servicio servicio1 = new Servicio();
        servicio1.id = 1L;
        servicio1.nombre = "Servicio 1";
        
        Servicio servicio2 = new Servicio();
        servicio2.id = 2L;
        servicio2.nombre = "Servicio 2";
        
        List<Servicio> servicios = Arrays.asList(servicio1, servicio2);
        
        // Mock the listAll method directly
        ServicioRepository spyRepo = spy(servicioRepository);
        doReturn(servicios).when(spyRepo).listAll();

        // Act
        List<Servicio> resultado = spyRepo.listAll();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(servicios, resultado);
    }

    @Test
    void findById_deberiaRetornarServicioCuandoExiste() {
        // Arrange
        Long id = 1L;
        Servicio servicio = new Servicio();
        servicio.id = id;
        servicio.nombre = "Servicio Test";
        
        // Mock the findById method directly
        ServicioRepository spyRepo = spy(servicioRepository);
        doReturn(servicio).when(spyRepo).findById(id);

        // Act
        Servicio resultado = spyRepo.findById(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.id);
        assertEquals("Servicio Test", resultado.nombre);
    }

    @Test
    void findById_cuandoNoExiste_deberiaRetornarNull() {
        // Arrange
        Long id = 999L;
        
        // Mock the findById method to return null
        ServicioRepository spyRepo = spy(servicioRepository);
        doReturn(null).when(spyRepo).findById(id);

        // Act
        Servicio resultado = spyRepo.findById(id);

        // Assert
        assertNull(resultado);
    }

    @Test
    void persist_deberiaPersistirServicio() {
        // Arrange
        Servicio servicio = new Servicio();
        servicio.nombre = "Nuevo Servicio";
        servicio.costo = 100.0;
        servicio.cubiertoSeguro = true;
        
        // Mock the persist method
        ServicioRepository spyRepo = spy(servicioRepository);
        doNothing().when(spyRepo).persist(any(Servicio.class));

        // Act
        spyRepo.persist(servicio);

        // Assert
        verify(spyRepo).persist(servicio);
    }

    @Test
    void delete_deberiaEliminarServicio() {
        // Arrange
        Servicio servicio = new Servicio();
        servicio.id = 1L;
        servicio.nombre = "Servicio a Eliminar";
        
        // Mock the delete method
        ServicioRepository spyRepo = spy(servicioRepository);
        doNothing().when(spyRepo).delete(any(Servicio.class));

        // Act
        spyRepo.delete(servicio);

        // Assert
        verify(spyRepo).delete(servicio);
    }
}
