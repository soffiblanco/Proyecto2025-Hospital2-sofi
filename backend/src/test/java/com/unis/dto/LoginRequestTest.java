package com.unis.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoginRequestTest {

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
    }

    @Test
    void constructor_deberiaInicializarCamposComoNull() {
        // Act
        LoginRequest nuevoLoginRequest = new LoginRequest();

        // Assert
        assertNull(nuevoLoginRequest.correo);
        assertNull(nuevoLoginRequest.contrasena);
    }

    @Test
    void setCorreo_deberiaAsignarCorreo() {
        // Arrange
        String correo = "test@example.com";

        // Act
        loginRequest.correo = correo;

        // Assert
        assertEquals(correo, loginRequest.correo);
    }

    @Test
    void setContrasena_deberiaAsignarContrasena() {
        // Arrange
        String contrasena = "password123";

        // Act
        loginRequest.contrasena = contrasena;

        // Assert
        assertEquals(contrasena, loginRequest.contrasena);
    }

    @Test
    void campos_deberianSerPublicos() {
        // Arrange
        String correo = "test@example.com";
        String contrasena = "password123";

        // Act
        loginRequest.correo = correo;
        loginRequest.contrasena = contrasena;

        // Assert
        assertEquals(correo, loginRequest.correo);
        assertEquals(contrasena, loginRequest.contrasena);
    }
}
