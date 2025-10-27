package com.unis.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RepositoryInstantiationTest {

    @Test
    void instanciar_todos_los_repositorios_no_debe_ser_nulo() {
        assertNotNull(new UsuarioRepository());
        assertNotNull(new UserAccRepository());
        assertNotNull(new UsuarioInterAccRepository());
        assertNotNull(new UsuarioInterconexionRepository());
        assertNotNull(new ServicioRepository());
        assertNotNull(new PageContentRepository());
        assertNotNull(new RecetaMedicamentoRepository());
        assertNotNull(new RolRepository());
        assertNotNull(new RecetaRepository());
        assertNotNull(new PacienteRepository());
        assertNotNull(new PacienteAccRepository());
        assertNotNull(new MedicamentoRepository());
        assertNotNull(new HistoriaRepository());
        assertNotNull(new PacienteFTRepository());
        assertNotNull(new FichaTecnicaRepository());
        assertNotNull(new FaqRepository());
        assertNotNull(new EmpleadoRepository());
        assertNotNull(new EmpleadoAccRepository());
        assertNotNull(new DoctorRepository());
        assertNotNull(new DoctorAccRepository());
        assertNotNull(new CitaRepository());
        assertNotNull(new AgendaRepository());
    }
}
