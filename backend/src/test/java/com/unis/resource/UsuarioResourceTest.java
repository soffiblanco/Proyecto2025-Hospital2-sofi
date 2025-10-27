package com.unis.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unis.model.Usuario;
import com.unis.service.UsuarioService;

import jakarta.ws.rs.core.Response;

public class UsuarioResourceTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioResource usuarioResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarUsuario_deberiaRegistrarUsuarioExitosamente() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setCorreo("test@example.com");
        usuario.setNombreUsuario("testuser");
        usuario.setContrasena("password");
        usuario.setId(1L);

        Usuario usuarioRegistrado = new Usuario();
        usuarioRegistrado.setId(1L);
        usuarioRegistrado.setCorreo("test@example.com");
        usuarioRegistrado.setNombreUsuario("testuser");

        when(usuarioService.registrarUsuario(any(Usuario.class))).thenReturn(usuarioRegistrado);

        // Act
        Response response = usuarioResource.registrarUsuario(usuario);

        // Assert
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(usuarioService).registrarUsuario(usuario);
    }

    @Test
    void registrarUsuario_cuandoServiceLanzaExcepcion_deberiaPropagarExcepcion() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setCorreo("test@example.com");
        
        when(usuarioService.registrarUsuario(any(Usuario.class)))
            .thenThrow(new RuntimeException("Error de validación"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            usuarioResource.registrarUsuario(usuario);
        });
    }
}
