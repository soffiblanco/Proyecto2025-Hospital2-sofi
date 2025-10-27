package com.unis.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DoctorTest {

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
        String documento = "12345678";
        Date fechaNacimiento = new Date();
        String genero = "M";
        String telefono = "555-1234";
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
    void setUsuario_deberiaAsignarUsuario() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombreUsuario("doctor1");

        // Act
        doctor.setUsuario(usuario);

        // Assert
        assertEquals(usuario, doctor.getUsuario());
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
        doctor.setCitas(citas);

        // Assert
        assertEquals(2, doctor.getCitas().size());
        assertEquals(citas, doctor.getCitas());
    }

    @Test
    void setCitas_conListaNull_deberiaAsignarNull() {
        // Act
        doctor.setCitas(null);

        // Assert
        assertNull(doctor.getCitas());
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
    void setIdUsuario_conValorCero_deberiaAsignarValorCero() {
        // Arrange
        Long idCero = 0L;

        // Act
        doctor.setIdUsuario(idCero);

        // Assert
        assertEquals(idCero, doctor.getIdUsuario());
    }
}
