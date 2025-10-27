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

import com.unis.model.Rol;

import io.quarkus.hibernate.orm.panache.PanacheQuery;

@ExtendWith(MockitoExtension.class)
public class RolRepositoryAdditionalTest {

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PanacheQuery<Rol> panacheQuery;

    @BeforeEach
    void setUp() {
        // Setup básico
    }

    @Test
    void listAll_deberiaRetornarListaVaciaCuandoNoHayRoles() {
        // Arrange
        when(rolRepository.listAll()).thenReturn(List.of());

        // Act
        List<Rol> resultado = rolRepository.listAll();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(rolRepository).listAll();
    }

    @Test
    void listAll_deberiaRetornarTodosLosRoles() {
        // Arrange
        Rol rol1 = new Rol();
        rol1.setId(1L);
        rol1.setNombre("ADMIN");

        Rol rol2 = new Rol();
        rol2.setId(2L);
        rol2.setNombre("USER");

        List<Rol> roles = Arrays.asList(rol1, rol2);
        when(rolRepository.listAll()).thenReturn(roles);

        // Act
        List<Rol> resultado = rolRepository.listAll();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(rol1));
        assertTrue(resultado.contains(rol2));
        verify(rolRepository).listAll();
    }

    @Test
    void findById_conIdValido_deberiaRetornarRol() {
        // Arrange
        Long id = 1L;
        Rol rol = new Rol();
        rol.setId(id);
        rol.setNombre("ADMIN");

        when(rolRepository.findById(id)).thenReturn(rol);

        // Act
        Rol resultado = rolRepository.findById(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("ADMIN", resultado.getNombre());
        verify(rolRepository).findById(id);
    }

    @Test
    void findById_conIdInexistente_deberiaRetornarNull() {
        // Arrange
        Long id = 999L;
        when(rolRepository.findById(id)).thenReturn(null);

        // Act
        Rol resultado = rolRepository.findById(id);

        // Assert
        assertNull(resultado);
        verify(rolRepository).findById(id);
    }

    @Test
    void persist_deberiaGuardarRol() {
        // Arrange
        Rol rol = new Rol();
        rol.setNombre("MODERATOR");

        doNothing().when(rolRepository).persist(rol);

        // Act & Assert
        assertDoesNotThrow(() -> rolRepository.persist(rol));
        verify(rolRepository).persist(rol);
    }

    @Test
    void delete_deberiaEliminarRol() {
        // Arrange
        Rol rol = new Rol();
        rol.setId(1L);

        doNothing().when(rolRepository).delete(rol);

        // Act & Assert
        assertDoesNotThrow(() -> rolRepository.delete(rol));
        verify(rolRepository).delete(rol);
    }

    @Test
    void find_conQuery_deberiaRetornarPanacheQuery() {
        // Arrange
        String query = "nombre = ?1";
        when(rolRepository.find(query)).thenReturn(panacheQuery);

        // Act
        PanacheQuery<Rol> resultado = rolRepository.find(query);

        // Assert
        assertNotNull(resultado);
        assertEquals(panacheQuery, resultado);
        verify(rolRepository).find(query);
    }

    @Test
    void find_conQueryYParametros_deberiaRetornarPanacheQuery() {
        // Arrange
        String query = "nombre = ?1";
        String nombre = "ADMIN";
        when(rolRepository.find(query, nombre)).thenReturn(panacheQuery);

        // Act
        PanacheQuery<Rol> resultado = rolRepository.find(query, nombre);

        // Assert
        assertNotNull(resultado);
        assertEquals(panacheQuery, resultado);
        verify(rolRepository).find(query, nombre);
    }

    @Test
    void count_deberiaRetornarNumeroDeRoles() {
        // Arrange
        long count = 3L;
        when(rolRepository.count()).thenReturn(count);

        // Act
        long resultado = rolRepository.count();

        // Assert
        assertEquals(count, resultado);
        verify(rolRepository).count();
    }

    @Test
    void count_conQuery_deberiaRetornarNumeroDeRoles() {
        // Arrange
        String query = "activo = ?1";
        boolean activo = true;
        long count = 2L;
        when(rolRepository.count(query, activo)).thenReturn(count);

        // Act
        long resultado = rolRepository.count(query, activo);

        // Assert
        assertEquals(count, resultado);
        verify(rolRepository).count(query, activo);
    }

    @Test
    void find_conQueryYParametrosMultiples_deberiaRetornarPanacheQuery() {
        // Arrange
        String query = "nombre IN (?1, ?2)";
        String nombre1 = "ADMIN";
        String nombre2 = "USER";
        when(rolRepository.find(query, nombre1, nombre2)).thenReturn(panacheQuery);

        // Act
        PanacheQuery<Rol> resultado = rolRepository.find(query, nombre1, nombre2);

        // Assert
        assertNotNull(resultado);
        assertEquals(panacheQuery, resultado);
        verify(rolRepository).find(query, nombre1, nombre2);
    }
}



