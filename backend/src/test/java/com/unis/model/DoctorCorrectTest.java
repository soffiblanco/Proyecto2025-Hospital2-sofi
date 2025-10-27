package com.unis.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DoctorCorrectTest {

    private Doctor doctor;

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
    }

    @Test
    void constructor_deberiaInicializarValoresPorDefecto() {
        // Act
        Doctor nuevoDoctor = new Doctor();

        // Assert
        assertNull(nuevoDoctor.getIdDoctor());
        assertNull(nuevoDoctor.getIdUsuario());
        assertNull(nuevoDoctor.getApellido());
        assertNull(nuevoDoctor.getDocumento());
        assertNull(nuevoDoctor.getFechaNacimiento());
        assertNull(nuevoDoctor.getGenero());
        assertNull(nuevoDoctor.getTelefono());
        assertNull(nuevoDoctor.getEspecialidad());
        assertNull(nuevoDoctor.getNumeroColegiado());
        assertNull(nuevoDoctor.getHorarioAtencion());
        assertNull(nuevoDoctor.getFechaGraduacion());
        assertNull(nuevoDoctor.getUniversidadGraduacion());
        assertNull(nuevoDoctor.getUsuario());
        assertNotNull(nuevoDoctor.getCitas());
        assertTrue(nuevoDoctor.getCitas().isEmpty());
    }

    @Test
    void gettersAndSetters_deberiaFuncionarCorrectamente() {
        // Arrange
        Long idDoctor = 1L;
        Long idUsuario = 2L;
        String apellido = "García";
        String documento = "87654321";
        Date fechaNacimiento = new Date();
        String genero = "F";
        String telefono = "0987654321";
        String especialidad = "Cardiología";
        String numeroColegiado = "COL123456";
        String horarioAtencion = "8:00-17:00";
        Date fechaGraduacion = new Date();
        String universidadGraduacion = "Universidad Nacional";

        // Act
        doctor.setIdDoctor(idDoctor);
        doctor.setIdUsuario(idUsuario);
        doctor.setApellido(apellido);
        doctor.setDocumento(documento);
        doctor.setFechaNacimiento(fechaNacimiento);
        doctor.setGenero(genero);
        doctor.setTelefono(telefono);
        doctor.setEspecialidad(especialidad);
        doctor.setNumeroColegiado(numeroColegiado);
        doctor.setHorarioAtencion(horarioAtencion);
        doctor.setFechaGraduacion(fechaGraduacion);
        doctor.setUniversidadGraduacion(universidadGraduacion);

        // Assert
        assertEquals(idDoctor, doctor.getIdDoctor());
        assertEquals(idUsuario, doctor.getIdUsuario());
        assertEquals(apellido, doctor.getApellido());
        assertEquals(documento, doctor.getDocumento());
        assertEquals(fechaNacimiento, doctor.getFechaNacimiento());
        assertEquals(genero, doctor.getGenero());
        assertEquals(telefono, doctor.getTelefono());
        assertEquals(especialidad, doctor.getEspecialidad());
        assertEquals(numeroColegiado, doctor.getNumeroColegiado());
        assertEquals(horarioAtencion, doctor.getHorarioAtencion());
        assertEquals(fechaGraduacion, doctor.getFechaGraduacion());
        assertEquals(universidadGraduacion, doctor.getUniversidadGraduacion());
    }

    @Test
    void setCitas_conListaVacia_deberiaAsignarListaVacia() {
        // Arrange
        List<Cita> citasVacias = new ArrayList<>();

        // Act
        doctor.setCitas(citasVacias);

        // Assert
        assertNotNull(doctor.getCitas());
        assertTrue(doctor.getCitas().isEmpty());
    }

    @Test
    void setCitas_conListaNull_deberiaAsignarNull() {
        // Act
        doctor.setCitas(null);

        // Assert
        assertNull(doctor.getCitas());
    }

    @Test
    void setIdDoctor_conValorCero_deberiaAsignarValorCero() {
        // Arrange
        Long idCero = 0L;

        // Act
        doctor.setIdDoctor(idCero);

        // Assert
        assertEquals(idCero, doctor.getIdDoctor());
    }

    @Test
    void setIdDoctor_conValorNegativo_deberiaAsignarValorNegativo() {
        // Arrange
        Long idNegativo = -1L;

        // Act
        doctor.setIdDoctor(idNegativo);

        // Assert
        assertEquals(idNegativo, doctor.getIdDoctor());
    }

    @Test
    void setGenero_conValorNull_deberiaAsignarNull() {
        // Act
        doctor.setGenero(null);

        // Assert
        assertNull(doctor.getGenero());
    }

    @Test
    void setTelefono_conStringVacio_deberiaAsignarStringVacio() {
        // Arrange
        String telefonoVacio = "";

        // Act
        doctor.setTelefono(telefonoVacio);

        // Assert
        assertEquals(telefonoVacio, doctor.getTelefono());
    }

    @Test
    void setDocumento_conStringVacio_deberiaAsignarStringVacio() {
        // Arrange
        String documentoVacio = "";

        // Act
        doctor.setDocumento(documentoVacio);

        // Assert
        assertEquals(documentoVacio, doctor.getDocumento());
    }

    @Test
    void setApellido_conStringNull_deberiaAsignarNull() {
        // Act
        doctor.setApellido(null);

        // Assert
        assertNull(doctor.getApellido());
    }

    @Test
    void setFechaNacimiento_conFechaNull_deberiaAsignarNull() {
        // Act
        doctor.setFechaNacimiento(null);

        // Assert
        assertNull(doctor.getFechaNacimiento());
    }

    @Test
    void setEspecialidad_conStringNull_deberiaAsignarNull() {
        // Act
        doctor.setEspecialidad(null);

        // Assert
        assertNull(doctor.getEspecialidad());
    }

    @Test
    void setNumeroColegiado_conStringNull_deberiaAsignarNull() {
        // Act
        doctor.setNumeroColegiado(null);

        // Assert
        assertNull(doctor.getNumeroColegiado());
    }

    @Test
    void setHorarioAtencion_conStringNull_deberiaAsignarNull() {
        // Act
        doctor.setHorarioAtencion(null);

        // Assert
        assertNull(doctor.getHorarioAtencion());
    }

    @Test
    void setFechaGraduacion_conFechaNull_deberiaAsignarNull() {
        // Act
        doctor.setFechaGraduacion(null);

        // Assert
        assertNull(doctor.getFechaGraduacion());
    }

    @Test
    void setUniversidadGraduacion_conStringNull_deberiaAsignarNull() {
        // Act
        doctor.setUniversidadGraduacion(null);

        // Assert
        assertNull(doctor.getUniversidadGraduacion());
    }

    @Test
    void setUsuario_conUsuarioNull_deberiaAsignarNull() {
        // Act
        doctor.setUsuario(null);

        // Assert
        assertNull(doctor.getUsuario());
    }

    @Test
    void setUsuario_conUsuarioValido_deberiaAsignarUsuario() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombreUsuario("doctoruser");

        // Act
        doctor.setUsuario(usuario);

        // Assert
        assertEquals(usuario, doctor.getUsuario());
    }
}
