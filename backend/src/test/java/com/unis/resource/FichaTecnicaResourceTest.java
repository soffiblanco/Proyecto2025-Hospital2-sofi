package com.unis.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unis.model.FichaTecnica;
import com.unis.service.FichaTecnicaService;

class FichaTecnicaResourceTest {

    @Mock
    FichaTecnicaService fichaTecnicaService;

    @InjectMocks
    FichaTecnicaResource fichaTecnicaResource;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listar_ok() {
        when(fichaTecnicaService.getAllFichas()).thenReturn(List.of(new FichaTecnica()));
        assertEquals(1, fichaTecnicaResource.obtenerTodasLasFichas().size());
        verify(fichaTecnicaService).getAllFichas();
    }

    @Test
    void registrar_ok() {
        doNothing().when(fichaTecnicaService).registrarFicha(any(FichaTecnica.class));
        fichaTecnicaResource.registrarFicha(new FichaTecnica());
        verify(fichaTecnicaService).registrarFicha(any(FichaTecnica.class));
    }
}
