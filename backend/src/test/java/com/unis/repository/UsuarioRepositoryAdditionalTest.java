package com.unis.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.unis.model.Usuario;

import io.quarkus.hibernate.orm.panache.PanacheQuery;

@ExtendWith(MockitoExtension.class)
public class UsuarioRepositoryAdditionalTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PanacheQuery<Usuario> panacheQuery;

    @BeforeEach
    void setUp() {
        // Setup básico
    }

    @Test
    void listAll_deberiaRetornarListaVaciaCuandoNoHayUsuarios() {
        // Arrange
        when(usuarioRepository.listAll()).thenReturn(List.of());

        // Act
        List<Usuario> resultado = usuarioRepository.listAll();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(usuarioRepository).listAll();
    }

    @Test
    void listAll_deberiaRetornarTodosLosUsuarios() {
        // Arrange
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setNombreUsuario("usuario1");

        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNombreUsuario("usuario2");

        List<Usuario> usuarios = Arrays.asList(usuario1, usuario2);
        when(usuarioRepository.listAll()).thenReturn(usuarios);

        // Act
        List<Usuario> resultado = usuarioRepository.listAll();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(usuario1));
        assertTrue(resultado.contains(usuario2));
        verify(usuarioRepository).listAll();
    }

    @Test
    void findById_conIdValido_deberiaRetornarUsuario() {
        // Arrange
        Long id = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombreUsuario("testuser");

        when(usuarioRepository.findById(id)).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioRepository.findById(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("testuser", resultado.getNombreUsuario());
        verify(usuarioRepository).findById(id);
    }

    @Test
    void findById_conIdInexistente_deberiaRetornarNull() {
        // Arrange
        Long id = 999L;
        when(usuarioRepository.findById(id)).thenReturn(null);

        // Act
        Usuario resultado = usuarioRepository.findById(id);

        // Assert
        assertNull(resultado);
        verify(usuarioRepository).findById(id);
    }

    @Test
    void findByCorreo_conCorreoValido_deberiaRetornarUsuario() {
        // Arrange
        String correo = "test@example.com";
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreo(correo);

        when(usuarioRepository.findByCorreo(correo)).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioRepository.findByCorreo(correo);

        // Assert
        assertNotNull(resultado);
        assertEquals(correo, resultado.getCorreo());
        verify(usuarioRepository).findByCorreo(correo);
    }

    @Test
    void findByCorreo_conCorreoInexistente_deberiaRetornarNull() {
        // Arrange
        String correo = "nonexistent@example.com";
        when(usuarioRepository.findByCorreo(correo)).thenReturn(null);

        // Act
        Usuario resultado = usuarioRepository.findByCorreo(correo);

        // Assert
        assertNull(resultado);
        verify(usuarioRepository).findByCorreo(correo);
    }

    @Test
    void persist_deberiaGuardarUsuario() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("newuser");
        usuario.setCorreo("new@example.com");

        doNothing().when(usuarioRepository).persist(usuario);

        // Act & Assert
        assertDoesNotThrow(() -> usuarioRepository.persist(usuario));
        verify(usuarioRepository).persist(usuario);
    }

    @Test
    void delete_deberiaEliminarUsuario() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        doNothing().when(usuarioRepository).delete(usuario);

        // Act & Assert
        assertDoesNotThrow(() -> usuarioRepository.delete(usuario));
        verify(usuarioRepository).delete(usuario);
    }

    @Test
    void find_conQuery_deberiaRetornarPanacheQuery() {
        // Arrange
        String query = "nombreUsuario = ?1";
        when(usuarioRepository.find(query)).thenReturn(panacheQuery);

        // Act
        PanacheQuery<Usuario> resultado = usuarioRepository.find(query);

        // Assert
        assertNotNull(resultado);
        assertEquals(panacheQuery, resultado);
        verify(usuarioRepository).find(query);
    }

    @Test
    void find_conQueryYParametros_deberiaRetornarPanacheQuery() {
        // Arrange
        String query = "nombreUsuario = ?1";
        Object param = "testuser";
        when(usuarioRepository.find(query, param)).thenReturn(panacheQuery);

        // Act
        PanacheQuery<Usuario> resultado = usuarioRepository.find(query, param);

        // Assert
        assertNotNull(resultado);
        assertEquals(panacheQuery, resultado);
        verify(usuarioRepository).find(query, param);
    }

    @Test
    void count_deberiaRetornarNumeroDeUsuarios() {
        // Arrange
        long count = 5L;
        when(usuarioRepository.count()).thenReturn(count);

        // Act
        long resultado = usuarioRepository.count();

        // Assert
        assertEquals(count, resultado);
        verify(usuarioRepository).count();
    }

    @Test
    void count_conQuery_deberiaRetornarNumeroDeUsuarios() {
        // Arrange
        String query = "estado = ?1";
        int estado = 1;
        long count = 3L;
        when(usuarioRepository.count(query, estado)).thenReturn(count);

        // Act
        long resultado = usuarioRepository.count(query, estado);

        // Assert
        assertEquals(count, resultado);
        verify(usuarioRepository).count(query, estado);
    }
}



