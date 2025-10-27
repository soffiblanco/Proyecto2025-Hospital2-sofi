package com.unis.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unis.model.Empleado;
import com.unis.service.EmpleadoService;

import jakarta.ws.rs.core.Response;

class EmpleadoResourceTest {

    @Mock
    EmpleadoService empleadoService;

    @InjectMocks
    EmpleadoResource empleadoResource;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listar_ok() {
        when(empleadoService.getAllEmpleados()).thenReturn(List.of(new Empleado()));
        List<Empleado> out = empleadoResource.obtenerTodosLosEmpleados();
        assertEquals(1, out.size());
        verify(empleadoService).getAllEmpleados();
    }

    @Test
    void obtener_por_id_ok_y_404() {
        Empleado e = new Empleado();
        when(empleadoService.getEmpleadoById(1L)).thenReturn(Optional.of(e));
        Response ok = empleadoResource.obtenerEmpleado(1L);
        assertEquals(200, ok.getStatus());
        assertEquals(e, ok.getEntity());

        when(empleadoService.getEmpleadoById(99L)).thenReturn(Optional.empty());
        Response nf = empleadoResource.obtenerEmpleado(99L);
        assertEquals(404, nf.getStatus());
    }

    @Test
    void registrar_crea_201() {
        doNothing().when(empleadoService).registrarEmpleado(any(Empleado.class));
        Response res = empleadoResource.registrarEmpleado(new Empleado());
        assertEquals(201, res.getStatus());
        verify(empleadoService).registrarEmpleado(any(Empleado.class));
    }

    @Test
    void actualizar_ok_y_404() {
        when(empleadoService.actualizarEmpleado(eq(1L), any(Empleado.class))).thenReturn(true);
        when(empleadoService.actualizarEmpleado(eq(99L), any(Empleado.class))).thenReturn(false);

        assertEquals(200, empleadoResource.actualizarEmpleado(1L, new Empleado()).getStatus());
        assertEquals(404, empleadoResource.actualizarEmpleado(99L, new Empleado()).getStatus());
    }

    @Test
    void eliminar_ok_y_404() {
        when(empleadoService.eliminarEmpleado(1L)).thenReturn(true);
        when(empleadoService.eliminarEmpleado(99L)).thenReturn(false);

        assertEquals(200, empleadoResource.eliminarEmpleado(1L).getStatus());
        assertEquals(404, empleadoResource.eliminarEmpleado(99L).getStatus());
    }
}
