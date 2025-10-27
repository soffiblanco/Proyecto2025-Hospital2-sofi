package com.unis.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PacienteCorrectTest {

    private Paciente paciente;

    @BeforeEach
    void setUp() {
        paciente = new Paciente();
    }

    @Test
    void constructor_deberiaInicializarValoresPorDefecto() {
        // Act
        Paciente nuevoPaciente = new Paciente();

        // Assert
        assertNull(nuevoPaciente.getIdPaciente());
        assertNull(nuevoPaciente.getIdUsuario());
        assertNull(nuevoPaciente.getApellido());
        assertNull(nuevoPaciente.getDocumento());
        assertNull(nuevoPaciente.getFechaNacimiento());
        assertNull(nuevoPaciente.getGenero());
        assertNull(nuevoPaciente.getTelefono());
        assertNull(nuevoPaciente.getFotografia());
        assertNull(nuevoPaciente.getUsuario());
        assertNotNull(nuevoPaciente.getCitas());
        assertTrue(nuevoPaciente.getCitas().isEmpty());
    }

    @Test
    void gettersAndSetters_deberiaFuncionarCorrectamente() {
        // Arrange
        Long idPaciente = 1L;
        Long idUsuario = 2L;
        String apellido = "Pérez";
        String documento = "12345678";
        Date fechaNacimiento = new Date();
        String genero = "M";
        String telefono = "1234567890";
        byte[] fotografia = new byte[]{1, 2, 3};

        // Act
        paciente.setIdPaciente(idPaciente);
        paciente.setIdUsuario(idUsuario);
        paciente.setApellido(apellido);
        paciente.setDocumento(documento);
        paciente.setFechaNacimiento(fechaNacimiento);
        paciente.setGenero(genero);
        paciente.setTelefono(telefono);
        paciente.setFotografia(fotografia);

        // Assert
        assertEquals(idPaciente, paciente.getIdPaciente());
        assertEquals(idUsuario, paciente.getIdUsuario());
        assertEquals(apellido, paciente.getApellido());
        assertEquals(documento, paciente.getDocumento());
        assertEquals(fechaNacimiento, paciente.getFechaNacimiento());
        assertEquals(genero, paciente.getGenero());
        assertEquals(telefono, paciente.getTelefono());
        assertArrayEquals(fotografia, paciente.getFotografia());
    }

    @Test
    void setCitas_conListaVacia_deberiaAsignarListaVacia() {
        // Arrange
        List<Cita> citasVacias = new ArrayList<>();

        // Act
        paciente.setCitas(citasVacias);

        // Assert
        assertNotNull(paciente.getCitas());
        assertTrue(paciente.getCitas().isEmpty());
    }

    @Test
    void setCitas_conListaNull_deberiaAsignarNull() {
        // Act
        paciente.setCitas(null);

        // Assert
        assertNull(paciente.getCitas());
    }

    @Test
    void setIdPaciente_conValorCero_deberiaAsignarValorCero() {
        // Arrange
        Long idCero = 0L;

        // Act
        paciente.setIdPaciente(idCero);

        // Assert
        assertEquals(idCero, paciente.getIdPaciente());
    }

    @Test
    void setIdPaciente_conValorNegativo_deberiaAsignarValorNegativo() {
        // Arrange
        Long idNegativo = -1L;

        // Act
        paciente.setIdPaciente(idNegativo);

        // Assert
        assertEquals(idNegativo, paciente.getIdPaciente());
    }

    @Test
    void setGenero_conValorNull_deberiaAsignarNull() {
        // Act
        paciente.setGenero(null);

        // Assert
        assertNull(paciente.getGenero());
    }

    @Test
    void setTelefono_conStringVacio_deberiaAsignarStringVacio() {
        // Arrange
        String telefonoVacio = "";

        // Act
        paciente.setTelefono(telefonoVacio);

        // Assert
        assertEquals(telefonoVacio, paciente.getTelefono());
    }

    @Test
    void setDocumento_conStringVacio_deberiaAsignarStringVacio() {
        // Arrange
        String documentoVacio = "";

        // Act
        paciente.setDocumento(documentoVacio);

        // Assert
        assertEquals(documentoVacio, paciente.getDocumento());
    }


    @Test
    void setApellido_conStringNull_deberiaAsignarNull() {
        // Act
        paciente.setApellido(null);

        // Assert
        assertNull(paciente.getApellido());
    }

    @Test
    void setFechaNacimiento_conFechaNull_deberiaAsignarNull() {
        // Act
        paciente.setFechaNacimiento(null);

        // Assert
        assertNull(paciente.getFechaNacimiento());
    }

    @Test
    void setFotografia_conArrayNull_deberiaAsignarNull() {
        // Act
        paciente.setFotografia(null);

        // Assert
        assertNull(paciente.getFotografia());
    }

    @Test
    void setUsuario_conUsuarioNull_deberiaAsignarNull() {
        // Act
        paciente.setUsuario(null);

        // Assert
        assertNull(paciente.getUsuario());
    }

    @Test
    void setUsuario_conUsuarioValido_deberiaAsignarUsuario() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombreUsuario("testuser");

        // Act
        paciente.setUsuario(usuario);

        // Assert
        assertEquals(usuario, paciente.getUsuario());
    }
}
