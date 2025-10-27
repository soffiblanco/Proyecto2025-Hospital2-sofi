package com.unis.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.unis.model.Usuario;
import com.unis.service.UsuarioService;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ExtendWith(MockitoExtension.class)
class UsuarioResourceAdditionalTest {

    @Mock
    UsuarioService usuarioService;

    @InjectMocks
    UsuarioResource usuarioResource;

    @Test
    void registrarUsuario_valido_devuelve201_y_mapa() {
        Usuario in = new Usuario();
        in.setNombreUsuario("testuser");
        in.setCorreo("test@example.com");
        in.setContrasena("password123");

        Usuario creado = new Usuario();
        creado.setId(1L);
        creado.setNombreUsuario("testuser");
        creado.setCorreo("test@example.com");

        when(usuarioService.registrarUsuario(in)).thenReturn(creado);

        Response res = usuarioResource.registrarUsuario(in);
        assertNotNull(res);
        assertEquals(Response.Status.CREATED.getStatusCode(), res.getStatus());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) res.getEntity();
        assertEquals(1L, body.get("id"));
        assertEquals("test@example.com", body.get("correo"));
        assertEquals("Usuario registrado con éxito", body.get("mensaje"));

        verify(usuarioService).registrarUsuario(in);
    }

    @Test
    void registrarUsuario_serviceLanzaBadRequest_propagaExcepcion() {
        Usuario in = new Usuario();
        doThrow(new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build()))
                .when(usuarioService).registrarUsuario(in);
        assertThrows(WebApplicationException.class, () -> usuarioResource.registrarUsuario(in));
        verify(usuarioService).registrarUsuario(in);
    }

    @Test
    void registrarUsuario_serviceLanzaConflict_propagaExcepcion() {
        Usuario in = new Usuario();
        doThrow(new WebApplicationException(Response.status(Response.Status.CONFLICT).build()))
                .when(usuarioService).registrarUsuario(in);
        assertThrows(WebApplicationException.class, () -> usuarioResource.registrarUsuario(in));
        verify(usuarioService).registrarUsuario(in);
    }
}




