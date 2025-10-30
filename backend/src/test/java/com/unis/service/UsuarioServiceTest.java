package com.unis.service;

import com.unis.dto.LoginResponse;
import com.unis.model.Rol;
import com.unis.model.Usuario;
import com.unis.repository.RolRepository;
import com.unis.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UsuarioServiceTest {

    private UsuarioService usuarioService;
    private UsuarioRepository usuarioRepository; // se mantiene por inyección, aunque no lo usemos
    private RolRepository rolRepository;         // idem
    private EntityManager entityManager;

    // Mocks para el query encadenado de conteo
    @SuppressWarnings("unchecked")
    private TypedQuery<Long> countQuery = mock(TypedQuery.class);

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        rolRepository = mock(RolRepository.class);
        entityManager = mock(EntityManager.class);

        usuarioService = new UsuarioService();
        usuarioService.usuarioRepository = usuarioRepository;
        usuarioService.rolRepository = rolRepository;
        usuarioService.entityManager = entityManager;

        // Stubs comunes para el query de conteo
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter(eq("correo"), anyString())).thenReturn(countQuery);
    }

    @Test
    void registrarUsuario_cuandoPayloadInvalido_deberiaBadRequest() {
        Usuario invalido = new Usuario(); // falta nombreUsuario/correo/contrasena
        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> usuarioService.registrarUsuario(invalido));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    void registrarUsuario_cuandoCorreoNoExiste_deberiaPersistirYRetornarUsuario() {
        // 0 = no duplicado
        when(countQuery.getSingleResult()).thenReturn(0L);

        // Rol por defecto (id 1) vía reference
        Rol rolRef = new Rol();
        rolRef.setId(1L);
        when(entityManager.getReference(eq(Rol.class), eq(1L))).thenReturn(rolRef);

        // No lanzar al persist/flush
        doNothing().when(entityManager).persist(any(Usuario.class));
        doNothing().when(entityManager).flush();

        Usuario input = new Usuario();
        input.setNombreUsuario("Pepe");
        input.setCorreo("nuevo@email.com");
        input.setContrasena("123");

        Usuario creado = assertDoesNotThrow(() -> usuarioService.registrarUsuario(input));
        assertNotNull(creado);

        // Capturamos lo persistido para validar campos
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(entityManager).persist(captor.capture());
        Usuario persistido = captor.getValue();

        assertEquals("Pepe", persistido.getNombreUsuario());
        assertEquals("nuevo@email.com", persistido.getCorreo());
        assertEquals("123", persistido.getContrasena());
        assertNotNull(persistido.getRol());
        assertEquals(1L, persistido.getRol().getId());
    }

    @Test
    void registrarUsuario_cuandoCorreoYaExiste_deberiaConflict409() {
        // 1 = ya existe
        when(countQuery.getSingleResult()).thenReturn(1L);

        Usuario input = new Usuario();
        input.setNombreUsuario("Ana");
        input.setCorreo("repetido@email.com");
        input.setContrasena("123");

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> usuarioService.registrarUsuario(input));

        assertEquals(Response.Status.CONFLICT.getStatusCode(), ex.getResponse().getStatus());
        // Si quieres, también puedes inspeccionar el body JSON:
        Object entity = ex.getResponse().getEntity();
        if (entity instanceof Map<?, ?> map) {
            assertTrue(String.valueOf(map.get("error")).contains("Correo ya registrado"));
        }
        // Asegura que NO se llamó a persist
        verify(entityManager, never()).persist(any(Usuario.class));
    }
}
