package com.unis.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.unis.model.Medicamento;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

class MedicamentoRepositoryTest {

    PanacheQuery<Medicamento> panacheQuery;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        panacheQuery = mock(PanacheQuery.class);
    }

    @Test
    void buscarPorNombre_devuelve_med() {
        Medicamento expected = new Medicamento();
        MedicamentoRepository repo = spy(new MedicamentoRepository());
        doReturn(panacheQuery).when(repo).find(eq("principioActivo"), eq("ibuprofeno"));
        when(panacheQuery.firstResult()).thenReturn(expected);

        Medicamento out = repo.buscarPorNombre("ibuprofeno");
        assertEquals(expected, out);
        verify(repo).find("principioActivo", "ibuprofeno");
        verify(panacheQuery).firstResult();
    }
}
