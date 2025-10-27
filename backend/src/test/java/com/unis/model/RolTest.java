package com.unis.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RolTest {

    private Rol rol;

    @BeforeEach
    void setUp() {
        rol = new Rol();
    }

    @Test
    void constructor_deberiaInicializarValoresPorDefecto() {
        // Act
        Rol nuevoRol = new Rol();

        // Assert
        assertNull(nuevoRol.getId());
        assertNull(nuevoRol.getRoleName());
    }

    @Test
    void gettersAndSetters_deberiaFuncionarCorrectamente() {
        // Arrange
        Long id = 1L;
        String roleName = "ADMIN";

        // Act
        rol.setId(id);
        rol.setRoleName(roleName);

        // Assert
        assertEquals(id, rol.getId());
        assertEquals(roleName, rol.getRoleName());
    }

    @Test
    void setId_conValorNull_deberiaAsignarNull() {
        // Act
        rol.setId(null);

        // Assert
        assertNull(rol.getId());
    }

    @Test
    void setRoleName_conValorNull_deberiaAsignarNull() {
        // Act
        rol.setRoleName(null);

        // Assert
        assertNull(rol.getRoleName());
    }

    @Test
    void setRoleName_conStringVacio_deberiaAsignarStringVacio() {
        // Arrange
        String roleNameVacio = "";

        // Act
        rol.setRoleName(roleNameVacio);

        // Assert
        assertEquals(roleNameVacio, rol.getRoleName());
    }

    @Test
    void setId_conValorNegativo_deberiaAsignarValorNegativo() {
        // Arrange
        Long idNegativo = -1L;

        // Act
        rol.setId(idNegativo);

        // Assert
        assertEquals(idNegativo, rol.getId());
    }
}
