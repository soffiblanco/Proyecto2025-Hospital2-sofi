package com.unis.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoginResponseTest {

    private LoginResponse loginResponse;
    private LoginResponse.User user;

    @BeforeEach
    void setUp() {
        user = new LoginResponse.User(1L, "testuser", "test@example.com", "USER");
        loginResponse = new LoginResponse("dummy-token", user);
    }

    @Test
    void constructor_deberiaInicializarTokenYUser() {
        // Arrange
        String token = "test-token";
        LoginResponse.User user = new LoginResponse.User(1L, "user", "user@example.com", "ADMIN");

        // Act
        LoginResponse response = new LoginResponse(token, user);

        // Assert
        assertEquals(token, response.token);
        assertEquals(user, response.user);
    }

    @Test
    void userConstructor_deberiaInicializarTodosLosCampos() {
        // Arrange
        Long id = 2L;
        String nombreUsuario = "admin";
        String correo = "admin@example.com";
        String roleName = "ADMIN";

        // Act
        LoginResponse.User newUser = new LoginResponse.User(id, nombreUsuario, correo, roleName);

        // Assert
        assertEquals(id, newUser.id);
        assertEquals(nombreUsuario, newUser.nombreUsuario);
        assertEquals(correo, newUser.correo);
        assertEquals(roleName, newUser.roleName);
    }

    @Test
    void userCampos_deberianSerPublicos() {
        // Arrange
        Long id = 3L;
        String nombreUsuario = "doctor";
        String correo = "doctor@example.com";
        String roleName = "DOCTOR";

        // Act
        user.id = id;
        user.nombreUsuario = nombreUsuario;
        user.correo = correo;
        user.roleName = roleName;

        // Assert
        assertEquals(id, user.id);
        assertEquals(nombreUsuario, user.nombreUsuario);
        assertEquals(correo, user.correo);
        assertEquals(roleName, user.roleName);
    }

    @Test
    void loginResponseCampos_deberianSerPublicos() {
        // Arrange
        String token = "new-token";
        LoginResponse.User newUser = new LoginResponse.User(4L, "patient", "patient@example.com", "PATIENT");

        // Act
        loginResponse.token = token;
        loginResponse.user = newUser;

        // Assert
        assertEquals(token, loginResponse.token);
        assertEquals(newUser, loginResponse.user);
    }
}
