package com.unis.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unis.model.Rol;

public class RolRepositoryTest {

    @InjectMocks
    private RolRepository rolRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listAll_deberiaRetornarTodosLosRoles() {
        // Arrange
        Rol rol1 = new Rol();
        rol1.setId(1L);
        rol1.setRoleName("USER");
        
        Rol rol2 = new Rol();
        rol2.setId(2L);
        rol2.setRoleName("ADMIN");
        
        List<Rol> roles = Arrays.asList(rol1, rol2);
        
        // Mock the listAll method directly
        RolRepository spyRepo = spy(rolRepository);
        doReturn(roles).when(spyRepo).listAll();

        // Act
        List<Rol> resultado = spyRepo.listAll();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(roles, resultado);
    }

    @Test
    void findById_deberiaRetornarRolCuandoExiste() {
        // Arrange
        Long id = 1L;
        Rol rol = new Rol();
        rol.setId(id);
        rol.setRoleName("USER");
        
        // Mock the findById method directly
        RolRepository spyRepo = spy(rolRepository);
        doReturn(rol).when(spyRepo).findById(id);

        // Act
        Rol resultado = spyRepo.findById(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("USER", resultado.getRoleName());
    }

    @Test
    void findById_cuandoNoExiste_deberiaRetornarNull() {
        // Arrange
        Long id = 999L;
        
        // Mock the findById method to return null
        RolRepository spyRepo = spy(rolRepository);
        doReturn(null).when(spyRepo).findById(id);

        // Act
        Rol resultado = spyRepo.findById(id);

        // Assert
        assertNull(resultado);
    }

    @Test
    void persist_deberiaPersistirRol() {
        // Arrange
        Rol rol = new Rol();
        rol.setRoleName("DOCTOR");
        
        // Mock the persist method
        RolRepository spyRepo = spy(rolRepository);
        doNothing().when(spyRepo).persist(any(Rol.class));

        // Act
        spyRepo.persist(rol);

        // Assert
        verify(spyRepo).persist(rol);
    }

    @Test
    void delete_deberiaEliminarRol() {
        // Arrange
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setRoleName("USER");
        
        // Mock the delete method
        RolRepository spyRepo = spy(rolRepository);
        doNothing().when(spyRepo).delete(any(Rol.class));

        // Act
        spyRepo.delete(rol);

        // Assert
        verify(spyRepo).delete(rol);
    }
}
