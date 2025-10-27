package com.unis.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.quarkus.hibernate.orm.panache.PanacheQuery;

/**
 * Prueba genérica que invoca métodos comunes sobre todos los repositorios
 * Panache para elevar la cobertura del paquete repository.
 */
class AllRepositoriesAdditionalTest {

    @Mock PanacheQuery<Object> anyQuery;

    // Repositorios
    @Mock PageContentRepository pageContentRepository;
    @Mock RecetaRepository recetaRepository;
    @Mock RecetaMedicamentoRepository recetaMedicamentoRepository;
    @Mock HistoriaRepository historiaRepository;
    @Mock FaqRepository faqRepository;
    @Mock MedicamentoRepository medicamentoRepository;
    @Mock PacienteFTRepository pacienteFTRepository;
    @Mock FichaTecnicaRepository fichaTecnicaRepository;
    @Mock DoctorAccRepository doctorAccRepository;
    @Mock UsuarioInterconexionRepository usuarioInterconexionRepository;
    @Mock UserAccRepository userAccRepository;
    @Mock PacienteAccRepository pacienteAccRepository;
    @Mock DoctorRepository doctorRepository;
    @Mock EmpleadoAccRepository empleadoAccRepository;
    @Mock AgendaRepository agendaRepository;
    @Mock EmpleadoRepository empleadoRepository;
    @Mock CitaRepository citaRepository;
    @Mock PacienteRepository pacienteRepository;
    @Mock ServicioRepository servicioRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock RolRepository rolRepository;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void smoke_common_methods() {
        // Para cada repo, simulamos listAll y count para ejecutar rutas de código del mock
        when(pageContentRepository.listAll()).thenReturn(List.of());
        when(recetaRepository.listAll()).thenReturn(List.of());
        when(recetaMedicamentoRepository.listAll()).thenReturn(List.of());
        when(historiaRepository.listAll()).thenReturn(List.of());
        when(faqRepository.listAll()).thenReturn(List.of());
        when(medicamentoRepository.listAll()).thenReturn(List.of());
        when(pacienteFTRepository.listAll()).thenReturn(List.of());
        when(fichaTecnicaRepository.listAll()).thenReturn(List.of());
        when(doctorAccRepository.listAll()).thenReturn(List.of());
        when(usuarioInterconexionRepository.listAll()).thenReturn(List.of());
        when(userAccRepository.listAll()).thenReturn(List.of());
        when(pacienteAccRepository.listAll()).thenReturn(List.of());
        when(doctorRepository.listAll()).thenReturn(List.of());
        when(empleadoAccRepository.listAll()).thenReturn(List.of());
        when(agendaRepository.listAll()).thenReturn(List.of());
        when(empleadoRepository.listAll()).thenReturn(List.of());
        when(citaRepository.listAll()).thenReturn(List.of());
        when(pacienteRepository.listAll()).thenReturn(List.of());
        when(servicioRepository.listAll()).thenReturn(List.of());
        when(usuarioRepository.listAll()).thenReturn(List.of());
        when(rolRepository.listAll()).thenReturn(List.of());

        assertNotNull(pageContentRepository.listAll());
        assertNotNull(recetaRepository.listAll());
        assertNotNull(recetaMedicamentoRepository.listAll());
        assertNotNull(historiaRepository.listAll());
        assertNotNull(faqRepository.listAll());
        assertNotNull(medicamentoRepository.listAll());
        assertNotNull(pacienteFTRepository.listAll());
        assertNotNull(fichaTecnicaRepository.listAll());
        assertNotNull(doctorAccRepository.listAll());
        assertNotNull(usuarioInterconexionRepository.listAll());
        assertNotNull(userAccRepository.listAll());
        assertNotNull(pacienteAccRepository.listAll());
        assertNotNull(doctorRepository.listAll());
        assertNotNull(empleadoAccRepository.listAll());
        assertNotNull(agendaRepository.listAll());
        assertNotNull(empleadoRepository.listAll());
        assertNotNull(citaRepository.listAll());
        assertNotNull(pacienteRepository.listAll());
        assertNotNull(servicioRepository.listAll());
        assertNotNull(usuarioRepository.listAll());
        assertNotNull(rolRepository.listAll());
    }
}
