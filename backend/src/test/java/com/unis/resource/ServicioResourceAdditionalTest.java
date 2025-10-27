package com.unis.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.unis.model.Servicio;
import com.unis.service.ServicioService;

import jakarta.ws.rs.core.Response;

@ExtendWith(MockitoExtension.class)
class ServicioResourceAdditionalTest {

    @Mock
    ServicioService servicioService;

    @InjectMocks
    ServicioResource servicioResource;

    @Test
    void listarServicios_ok() {
        when(servicioService.listarTodos()).thenReturn(List.of(new Servicio()));
        List<Servicio> out = servicioResource.listarServicios();
        assertEquals(1, out.size());
        verify(servicioService).listarTodos();
    }

    @Test
    void listarSubServicios_ok() {
        when(servicioService.listarSubServicios(1L)).thenReturn(Arrays.asList(new Servicio(), new Servicio()));
        List<Servicio> out = servicioResource.listarSubServicios(1L);
        assertEquals(2, out.size());
        verify(servicioService).listarSubServicios(1L);
    }

    @Test
    void agregarServicio_nombre_invalido_400() {
        Servicio s = new Servicio();
        s.nombre = "   ";
        Response res = servicioResource.agregarServicio(s);
        assertEquals(400, res.getStatus());
    }

    @Test
    void agregarServicio_ok_201() {
        Servicio in = new Servicio();
        in.nombre = "Nuevo";
        in.costo = 10.0;
        Servicio created = new Servicio();
        created.nombre = "Nuevo";
        when(servicioService.agregarServicio(in, null)).thenReturn(created);
        Response res = servicioResource.agregarServicio(in);
        assertEquals(201, res.getStatus());
        assertEquals(created, res.getEntity());
        verify(servicioService).agregarServicio(in, null);
    }

    @Test
    void agregarServicio_parent_no_existe_400() {
        Servicio in = new Servicio();
        in.nombre = "Nuevo";
        in.costo = 10.0;
        in.setParentId(123L);
        when(servicioService.buscarPorId(123L)).thenReturn(null);
        Response res = servicioResource.agregarServicio(in);
        assertEquals(400, res.getStatus());
    }

    @Test
    void eliminarServicio_ok_y_500() {
        doNothing().when(servicioService).eliminarServicio(1L);
        Response ok = servicioResource.eliminarServicio(1L);
        assertEquals(200, ok.getStatus());

        doThrow(new RuntimeException("DB error")).when(servicioService).eliminarServicio(2L);
        Response err = servicioResource.eliminarServicio(2L);
        assertEquals(500, err.getStatus());
    }
}




