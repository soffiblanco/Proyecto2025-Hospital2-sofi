package com.unis.dto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unis.model.Receta;

public class RecetaDTOTest {

    @Mock
    private Receta receta;

    private RecetaDTO recetaDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar mock de Receta
        when(receta.getIdReceta()).thenReturn(1L);
        when(receta.getIdCita()).thenReturn(2L);
        when(receta.getFechaCreacion()).thenReturn(new Date());
        when(receta.getIdPaciente()).thenReturn(3L);
        when(receta.getIdDoctor()).thenReturn(4L);
        when(receta.getCodigoReceta()).thenReturn("REC-001");
        when(receta.getAnotaciones()).thenReturn("Tomar con agua");
        when(receta.getNotasEspeciales()).thenReturn("Evitar alcohol");
        when(receta.getMedicamentos()).thenReturn(new ArrayList<>());
        
        recetaDTO = new RecetaDTO(receta, "Juan Pérez");
    }

    @Test
    void constructor_conRecetaYNombrePaciente_deberiaInicializarCampos() {
        // Assert
        assertEquals(1L, recetaDTO.getIdReceta());
        assertEquals(2L, recetaDTO.getIdCita());
        assertNotNull(recetaDTO.getFechaCreacion());
        assertEquals(3L, recetaDTO.getIdPaciente());
        assertEquals(4L, recetaDTO.getIdDoctor());
        assertEquals("REC-001", recetaDTO.getCodigoReceta());
        assertEquals("Tomar con agua", recetaDTO.getAnotaciones());
        assertEquals("Evitar alcohol", recetaDTO.getNotasEspeciales());
        assertNotNull(recetaDTO.getMedicamentos());
        assertEquals("Juan Pérez", recetaDTO.getNombrePaciente());
    }

    @Test
    void gettersAndSetters_deberiaFuncionarCorrectamente() {
        // Arrange
        Long idReceta = 5L;
        Long idCita = 6L;
        Date fechaCreacion = new Date();
        Long idPaciente = 7L;
        Long idDoctor = 8L;
        String codigoReceta = "REC-002";
        String anotaciones = "Tomar después de comer";
        String notasEspeciales = "No manejar";
        List<?> medicamentos = new ArrayList<>();
        String nombrePaciente = "María García";

        // Act
        recetaDTO.setIdReceta(idReceta);
        recetaDTO.setIdCita(idCita);
        recetaDTO.setFechaCreacion(fechaCreacion);
        recetaDTO.setIdPaciente(idPaciente);
        recetaDTO.setIdDoctor(idDoctor);
        recetaDTO.setCodigoReceta(codigoReceta);
        recetaDTO.setAnotaciones(anotaciones);
        recetaDTO.setNotasEspeciales(notasEspeciales);
        recetaDTO.setMedicamentos(medicamentos);

        // Assert
        assertEquals(idReceta, recetaDTO.getIdReceta());
        assertEquals(idCita, recetaDTO.getIdCita());
        assertEquals(fechaCreacion, recetaDTO.getFechaCreacion());
        assertEquals(idPaciente, recetaDTO.getIdPaciente());
        assertEquals(idDoctor, recetaDTO.getIdDoctor());
        assertEquals(codigoReceta, recetaDTO.getCodigoReceta());
        assertEquals(anotaciones, recetaDTO.getAnotaciones());
        assertEquals(notasEspeciales, recetaDTO.getNotasEspeciales());
        assertEquals(medicamentos, recetaDTO.getMedicamentos());
    }

    @Test
    void setIdReceta_conValorNull_deberiaAsignarNull() {
        // Act
        recetaDTO.setIdReceta(null);

        // Assert
        assertNull(recetaDTO.getIdReceta());
    }

    @Test
    void setIdCita_conValorNegativo_deberiaAsignarValorNegativo() {
        // Arrange
        Long idCitaNegativo = -1L;

        // Act
        recetaDTO.setIdCita(idCitaNegativo);

        // Assert
        assertEquals(idCitaNegativo, recetaDTO.getIdCita());
    }

    @Test
    void setCodigoReceta_conStringVacio_deberiaAsignarStringVacio() {
        // Arrange
        String codigoVacio = "";

        // Act
        recetaDTO.setCodigoReceta(codigoVacio);

        // Assert
        assertEquals(codigoVacio, recetaDTO.getCodigoReceta());
    }

    @Test
    void setAnotaciones_conStringNull_deberiaAsignarNull() {
        // Act
        recetaDTO.setAnotaciones(null);

        // Assert
        assertNull(recetaDTO.getAnotaciones());
    }

    @Test
    void setNotasEspeciales_conStringNull_deberiaAsignarNull() {
        // Act
        recetaDTO.setNotasEspeciales(null);

        // Assert
        assertNull(recetaDTO.getNotasEspeciales());
    }

    @Test
    void setMedicamentos_conListaNull_deberiaAsignarNull() {
        // Act
        recetaDTO.setMedicamentos(null);

        // Assert
        assertNull(recetaDTO.getMedicamentos());
    }

    @Test
    void setFechaCreacion_conFechaNull_deberiaAsignarNull() {
        // Act
        recetaDTO.setFechaCreacion(null);

        // Assert
        assertNull(recetaDTO.getFechaCreacion());
    }

    @Test
    void constructor_conRecetaNull_deberiaLanzarExcepcion() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new RecetaDTO(null, "Paciente");
        });
    }

    @Test
    void constructor_conNombrePacienteNull_deberiaAsignarNull() {
        // Arrange
        when(receta.getIdReceta()).thenReturn(1L);
        when(receta.getIdCita()).thenReturn(2L);
        when(receta.getFechaCreacion()).thenReturn(new Date());
        when(receta.getIdPaciente()).thenReturn(3L);
        when(receta.getIdDoctor()).thenReturn(4L);
        when(receta.getCodigoReceta()).thenReturn("REC-001");
        when(receta.getAnotaciones()).thenReturn("Anotaciones");
        when(receta.getNotasEspeciales()).thenReturn("Notas");
        when(receta.getMedicamentos()).thenReturn(new ArrayList<>());

        // Act
        RecetaDTO nuevoDTO = new RecetaDTO(receta, null);

        // Assert
        assertNull(nuevoDTO.getNombrePaciente());
    }
}
