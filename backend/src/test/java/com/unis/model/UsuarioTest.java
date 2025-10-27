package com.unis.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UsuarioTest {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
    }

    @Test
    void constructor_deberiaInicializarValoresPorDefecto() {
        // Act
        Usuario nuevoUsuario = new Usuario();

        // Assert
        assertEquals(0, nuevoUsuario.getEstado());
        assertNotNull(nuevoUsuario.getFechaCreaction());
    }

    @Test
    void gettersAndSetters_deberiaFuncionarCorrectamente() {
        // Arrange
        Long id = 1L;
        String nombreUsuario = "testuser";
        String correo = "test@example.com";
        String contrasena = "password";
        int estado = 1;
        Date fechaCreacion = new Date();

        // Act
        usuario.setId(id);
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setCorreo(correo);
        usuario.setContrasena(contrasena);
        usuario.setEstado(estado);
        usuario.setFechaCreaction(fechaCreacion);

        // Assert
        assertEquals(id, usuario.getId());
        assertEquals(nombreUsuario, usuario.getNombreUsuario());
        assertEquals(correo, usuario.getCorreo());
        assertEquals(contrasena, usuario.getContrasena());
        assertEquals(estado, usuario.getEstado());
        assertEquals(fechaCreacion, usuario.getFechaCreaction());
    }

    @Test
    void setRol_deberiaAsignarRolCorrectamente() {
        // Arrange
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setRoleName("USER");

        // Act
        usuario.setRol(rol);

        // Assert
        assertEquals(rol, usuario.getRol());
    }

    @Test
    void prePersist_cuandoEstadoEsCero_deberiaCambiarA1() {
        // Arrange
        usuario.setEstado(0);

        // Act
        usuario.prePersist();

        // Assert
        assertEquals(1, usuario.getEstado());
    }

    @Test
    void prePersist_cuandoEstadoNoEsCero_deberiaMantenerEstado() {
        // Arrange
        usuario.setEstado(2);

        // Act
        usuario.prePersist();

        // Assert
        assertEquals(2, usuario.getEstado());
    }

    @Test
    void prePersist_cuandoFechaCreacionEsNull_deberiaAsignarFechaActual() {
        // Arrange
        usuario.setFechaCreaction(null);

        // Act
        usuario.prePersist();

        // Assert
        assertNotNull(usuario.getFechaCreaction());
        assertTrue(usuario.getFechaCreaction().getTime() <= System.currentTimeMillis());
    }

    @Test
    void prePersist_cuandoFechaCreacionNoEsNull_deberiaMantenerFecha() {
        // Arrange
        Date fechaOriginal = new Date(1000000L);
        usuario.setFechaCreaction(fechaOriginal);

        // Act
        usuario.prePersist();

        // Assert
        assertEquals(fechaOriginal, usuario.getFechaCreaction());
    }
}
