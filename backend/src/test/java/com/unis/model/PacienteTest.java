package com.unis.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PacienteTest {

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
        String apellido = "López";
        String documento = "87654321";
        Date fechaNacimiento = new Date();
        String genero = "F";
        String telefono = "555-9876";
        byte[] fotografia = "foto".getBytes();

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
    void setUsuario_deberiaAsignarUsuario() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombreUsuario("paciente1");

        // Act
        paciente.setUsuario(usuario);

        // Assert
        assertEquals(usuario, paciente.getUsuario());
    }

    @Test
    void setCitas_deberiaAsignarListaDeCitas() {
        // Arrange
        List<Cita> citas = new ArrayList<>();
        Cita cita1 = new Cita();
        Cita cita2 = new Cita();
        citas.add(cita1);
        citas.add(cita2);

        // Act
        paciente.setCitas(citas);

        // Assert
        assertEquals(2, paciente.getCitas().size());
        assertEquals(citas, paciente.getCitas());
    }

    @Test
    void setCitas_conListaNull_deberiaAsignarNull() {
        // Act
        paciente.setCitas(null);

        // Assert
        assertNull(paciente.getCitas());
    }

    @Test
    void getNombre_deberiaRetornarApellidoYDocumento() {
        // Arrange
        String apellido = "Martínez";
        String documento = "12345678";
        paciente.setApellido(apellido);
        paciente.setDocumento(documento);

        // Act
        String nombre = paciente.getNombre();

        // Assert
        assertEquals(apellido + " " + documento, nombre);
    }

    @Test
    void getNombre_conDocumentoNull_deberiaRetornarApellidoYStringVacio() {
        // Arrange
        String apellido = "González";
        paciente.setApellido(apellido);
        paciente.setDocumento(null);

        // Act
        String nombre = paciente.getNombre();

        // Assert
        assertEquals(apellido + " ", nombre);
    }

    @Test
    void setFotografia_conArrayVacio_deberiaAsignarArrayVacio() {
        // Arrange
        byte[] fotoVacia = new byte[0];

        // Act
        paciente.setFotografia(fotoVacia);

        // Assert
        assertArrayEquals(fotoVacia, paciente.getFotografia());
    }

    @Test
    void setFotografia_conNull_deberiaAsignarNull() {
        // Act
        paciente.setFotografia(null);

        // Assert
        assertNull(paciente.getFotografia());
    }
}
