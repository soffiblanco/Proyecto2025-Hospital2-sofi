package com.unis.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unis.dto.LoginRequest;
import com.unis.dto.LoginResponse;
import com.unis.model.Rol;
import com.unis.model.Usuario;
import com.unis.service.UsuarioService;

import jakarta.ws.rs.core.Response;

public class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarUsuarios_deberiaRetornarListaDeUsuarios() {
        // Arrange
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setCorreo("user1@example.com");
        
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setCorreo("user2@example.com");
        
        List<Usuario> usuarios = Arrays.asList(usuario1, usuario2);
        when(usuarioService.listarUsuarios()).thenReturn(usuarios);

        // Act
        List<Usuario> resultado = usuarioController.listarUsuarios();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(usuarios, resultado);
        verify(usuarioService).listarUsuarios();
    }

    @Test
    void registrarUsuario_deberiaRegistrarUsuarioExitosamente() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setCorreo("test@example.com");
        usuario.setNombreUsuario("testuser");
        
        Usuario usuarioRegistrado = new Usuario();
        usuarioRegistrado.setId(1L);
        usuarioRegistrado.setCorreo("test@example.com");
        
        when(usuarioService.registrarUsuario(any(Usuario.class))).thenReturn(usuarioRegistrado);

        // Act
        Response response = usuarioController.registrarUsuario(usuario);

        // Assert
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(usuarioService).registrarUsuario(usuario);
    }

    @Test
    void login_conCredencialesValidas_deberiaRetornarLoginResponse() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.correo = "test@example.com";
        request.contrasena = "password";
        
        LoginResponse loginResponse = new LoginResponse("token", new LoginResponse.User(1L, "user", "test@example.com", "USER"));
        when(usuarioService.intentarLogin(anyString(), anyString())).thenReturn(Optional.of(loginResponse));

        // Act
        Response response = usuarioController.login(request);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(usuarioService).intentarLogin("test@example.com", "password");
    }

    @Test
    void login_conCredencialesInvalidas_deberiaRetornarUnauthorized() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.correo = "test@example.com";
        request.contrasena = "wrongpassword";
        
        when(usuarioService.intentarLogin(anyString(), anyString())).thenReturn(Optional.empty());

        // Act
        Response response = usuarioController.login(request);

        // Assert
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void login_conRequestNull_deberiaRetornarBadRequest() {
        // Act
        Response response = usuarioController.login(null);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void login_conCorreoVacio_deberiaRetornarBadRequest() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.correo = "";
        request.contrasena = "password";

        // Act
        Response response = usuarioController.login(request);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void listarUsuariosInactivos_deberiaRetornarListaDeUsuariosInactivos() {
        // Arrange
        Usuario usuarioInactivo = new Usuario();
        usuarioInactivo.setId(1L);
        usuarioInactivo.setEstado(0);
        
        List<Usuario> usuariosInactivos = Arrays.asList(usuarioInactivo);
        when(usuarioService.listarUsuariosInactivos()).thenReturn(usuariosInactivos);

        // Act
        List<Usuario> resultado = usuarioController.listarUsuariosInactivos();

        // Assert
        assertEquals(1, resultado.size());
        assertEquals(0, resultado.get(0).getEstado());
        verify(usuarioService).listarUsuariosInactivos();
    }

    @Test
    void listarRoles_deberiaRetornarListaDeRoles() {
        // Arrange
        Rol rol1 = new Rol();
        rol1.setId(1L);
        rol1.setRoleName("USER");
        
        Rol rol2 = new Rol();
        rol2.setId(2L);
        rol2.setRoleName("ADMIN");
        
        List<Rol> roles = Arrays.asList(rol1, rol2);
        when(usuarioService.listarRoles()).thenReturn(roles);

        // Act
        List<Rol> resultado = usuarioController.listarRoles();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(roles, resultado);
        verify(usuarioService).listarRoles();
    }

    @Test
    void activarUsuario_deberiaActivarUsuarioExitosamente() {
        // Arrange
        Long usuarioId = 1L;
        UsuarioController.ActivarUsuarioDTO dto = new UsuarioController.ActivarUsuarioDTO();
        dto.setRolId(2L);
        
        Usuario usuarioActivado = new Usuario();
        usuarioActivado.setId(usuarioId);
        usuarioActivado.setEstado(1);
        
        when(usuarioService.activarUsuario(usuarioId, 2L)).thenReturn(usuarioActivado);

        // Act
        Response response = usuarioController.activarUsuario(usuarioId, dto);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(usuarioService).activarUsuario(usuarioId, 2L);
    }

    @Test
    void activarUsuario_cuandoServiceLanzaExcepcion_deberiaRetornarBadRequest() {
        // Arrange
        Long usuarioId = 1L;
        UsuarioController.ActivarUsuarioDTO dto = new UsuarioController.ActivarUsuarioDTO();
        dto.setRolId(2L);
        
        when(usuarioService.activarUsuario(usuarioId, 2L))
            .thenThrow(new RuntimeException("Usuario no encontrado"));

        // Act
        Response response = usuarioController.activarUsuario(usuarioId, dto);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void desactivarUsuario_deberiaDesactivarUsuarioExitosamente() {
        // Arrange
        Long usuarioId = 1L;
        Usuario usuarioDesactivado = new Usuario();
        usuarioDesactivado.setId(usuarioId);
        usuarioDesactivado.setEstado(0);
        
        when(usuarioService.desactivarUsuario(usuarioId)).thenReturn(usuarioDesactivado);

        // Act
        Response response = usuarioController.desactivarUsuario(usuarioId);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(usuarioService).desactivarUsuario(usuarioId);
    }

    @Test
    void desactivarUsuario_cuandoServiceLanzaExcepcion_deberiaRetornarBadRequest() {
        // Arrange
        Long usuarioId = 1L;
        when(usuarioService.desactivarUsuario(usuarioId))
            .thenThrow(new RuntimeException("Usuario no encontrado"));

        // Act
        Response response = usuarioController.desactivarUsuario(usuarioId);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }
}
