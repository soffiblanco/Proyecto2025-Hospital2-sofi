package com.unis.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.unis.dto.LoginResponse;
import com.unis.model.Rol;
import com.unis.model.Usuario;
import com.unis.repository.RolRepository;
import com.unis.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.WebApplicationException;

class UsuarioServiceExtraTest {

  @Mock UsuarioRepository usuarioRepository;
  @Mock RolRepository rolRepository;
  @Mock EntityManager entityManager;

  @InjectMocks UsuarioService usuarioService;

  @BeforeEach void init() { MockitoAnnotations.openMocks(this); }

  @Test
  void listar_y_roles() {
    when(usuarioRepository.listAll()).thenReturn(List.of(new Usuario()));
    assertEquals(1, usuarioService.listarUsuarios().size());
    when(rolRepository.listAll()).thenReturn(List.of(new Rol()));
    assertEquals(1, usuarioService.listarRoles().size());
  }

  @Test
  void login_campos_blank() {
    assertTrue(usuarioService.intentarLogin(null, "x").isEmpty());
    assertTrue(usuarioService.intentarLogin(" ", "x").isEmpty());
  }

  @Test
  void login_error_persistence_retorna_500() {
    when(entityManager.createQuery(anyString(), eq(Object[].class)))
        .thenThrow(new PersistenceException("boom"));
    assertThrows(WebApplicationException.class,
        () -> usuarioService.intentarLogin("a@b.com", "p").orElseThrow());
  }

  @Test
  void registrar_validaciones_y_duplicado() {
    Usuario u = new Usuario();
    assertThrows(WebApplicationException.class, () -> usuarioService.registrarUsuario(null));

    u.setCorreo("a@b.com");
    when(usuarioRepository.findByCorreo("a@b.com")).thenReturn(new Usuario());
    assertThrows(WebApplicationException.class, () -> usuarioService.registrarUsuario(u));

    when(usuarioRepository.findByCorreo("a@b.com")).thenReturn(null);
    doNothing().when(usuarioRepository).persist(u);
    assertEquals(u, usuarioService.registrarUsuario(u));
  }

  @Test
  void activar_y_desactivar_usuario() {
    Usuario u = new Usuario();
    when(usuarioRepository.findById(1L)).thenReturn(u);
    Rol r = new Rol(); r.setId(2L);
    when(rolRepository.findById(2L)).thenReturn(r);
    Usuario out = usuarioService.activarUsuario(1L, 2L);
    assertEquals(1, out.getEstado());

    when(usuarioRepository.findById(99L)).thenReturn(null);
    assertThrows(WebApplicationException.class, () -> usuarioService.activarUsuario(99L, 2L));

    when(usuarioRepository.findById(3L)).thenReturn(new Usuario());
    when(rolRepository.findById(7L)).thenReturn(null);
    assertThrows(WebApplicationException.class, () -> usuarioService.activarUsuario(3L, 7L));

    Usuario u2 = new Usuario();
    when(usuarioRepository.findById(5L)).thenReturn(u2);
    assertEquals(0, usuarioService.desactivarUsuario(5L).getEstado());

    when(usuarioRepository.findById(6L)).thenReturn(null);
    assertThrows(WebApplicationException.class, () -> usuarioService.desactivarUsuario(6L));
  }
}
