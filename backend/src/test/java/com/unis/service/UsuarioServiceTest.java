package com.unis.service;

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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UsuarioServiceTest {

    private UsuarioService usuarioService;
    private UsuarioRepository usuarioRepository; // no lo usa registrarUsuario, pero lo dejamos mockeado
    private RolRepository rolRepository;         // no lo usa registrarUsuario, pero lo dejamos mockeado
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        rolRepository = mock(RolRepository.class);
        entityManager = mock(EntityManager.class);

        usuarioService = new UsuarioService();
        usuarioService.usuarioRepository = usuarioRepository;
        usuarioService.rolRepository = rolRepository;
        usuarioService.entityManager = entityManager;
    }

    @Test
    void registrarUsuario_cuandoCorreoNoExiste_deberiaPersistir() {
        // Arrange: usuario válido con campos mínimos requeridos
        Usuario entrada = new Usuario();
        entrada.setNombreUsuario("Nuevo User");
        entrada.setCorreo("nuevo@email.com");
        entrada.setContrasena("Secret123!");

        // Mock: query de conteo -> 0L (no existe)
        @SuppressWarnings("unchecked")
        TypedQuery<Long> countQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(
                eq("select count(u) from Usuario u where u.correo = :correo"),
                eq(Long.class))
        ).thenReturn(countQuery);
        when(countQuery.setParameter(eq("correo"), anyString())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(0L);

        // Mock: resolveRolReference(1L) -> getReference OK
        Rol rolRef = new Rol();
        rolRef.setId(1L);
        rolRef.setRoleName("USER");
        when(entityManager.getReference(eq(Rol.class), eq(1L))).thenReturn(rolRef);

        // Mock: persist/flush (void)
        doNothing().when(entityManager).persist(any(Usuario.class));
        doNothing().when(entityManager).flush();

        // Act + Assert
        assertDoesNotThrow(() -> {
            Usuario creado = usuarioService.registrarUsuario(entrada);
            assertNotNull(creado);
            assertEquals("nuevo@email.com", creado.getCorreo());
            assertEquals("Nuevo User", creado.getNombreUsuario());
            assertEquals("Secret123!", creado.getContrasena());
            assertNotNull(creado.getRol());
            assertEquals(1L, creado.getRol().getId());
        });

        // Verifica que realmente se intentó persistir
        verify(entityManager, times(1)).persist(any(Usuario.class));
        verify(entityManager, times(1)).flush();

        // No debería llamar a persist del repository aquí
        verify(usuarioRepository, never()).persist(any());
    }

    @Test
    void registrarUsuario_cuandoCorreoYaExiste_deberiaLanzarConflict409ConMensaje() {
        // Arrange
        Usuario entrada = new Usuario();
        entrada.setNombreUsuario("Alguien");
        entrada.setCorreo("repetido@email.com");
        entrada.setContrasena("Secret123!");

        // Mock: query de conteo -> 1L (ya existe)
        @SuppressWarnings("unchecked")
        TypedQuery<Long> countQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(
                eq("select count(u) from Usuario u where u.correo = :correo"),
                eq(Long.class))
        ).thenReturn(countQuery);
        when(countQuery.setParameter(eq("correo"), anyString())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(1L);

        // Act
        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            usuarioService.registrarUsuario(entrada);
        });

        // Assert: 409 CONFLICT y payload {"error":"Correo ya registrado"}
        assertEquals(Response.Status.CONFLICT.getStatusCode(), ex.getResponse().getStatus());

        Object entity = ex.getResponse().getEntity();
        assertNotNull(entity);
        assertTrue(entity instanceof Map, "El entity debe ser un Map con el error");
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) entity;
        assertEquals("Correo ya registrado", map.get("error"));

        // No debe persistir si hubo conflicto
        verify(entityManager, never()).persist(any(Usuario.class));
        verify(entityManager, never()).flush();
        verify(usuarioRepository, never()).persist(any());
    }
}
