package com.unis.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unis.model.Servicio;
import com.unis.service.ServicioService;

import jakarta.ws.rs.core.Response;

public class ServicioResourceTest {

    @Mock
    private ServicioService servicioService;

    @InjectMocks
    private ServicioResource servicioResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarServicios_deberiaRetornarListaDeServicios() {
        // Arrange
        Servicio servicio1 = new Servicio();
        servicio1.id = 1L;
        servicio1.nombre = "Servicio 1";
        
        Servicio servicio2 = new Servicio();
        servicio2.id = 2L;
        servicio2.nombre = "Servicio 2";
        
        List<Servicio> servicios = Arrays.asList(servicio1, servicio2);
        when(servicioService.listarTodos()).thenReturn(servicios);

        // Act
        List<Servicio> resultado = servicioResource.listarServicios();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(servicios, resultado);
        verify(servicioService).listarTodos();
    }

    @Test
    void listarSubServicios_deberiaRetornarSubServicios() {
        // Arrange
        Long parentId = 1L;
        Servicio subServicio1 = new Servicio();
        subServicio1.id = 2L;
        subServicio1.nombre = "Subservicio 1";
        
        Servicio subServicio2 = new Servicio();
        subServicio2.id = 3L;
        subServicio2.nombre = "Subservicio 2";
        
        List<Servicio> subServicios = Arrays.asList(subServicio1, subServicio2);
        when(servicioService.listarSubServicios(parentId)).thenReturn(subServicios);

        // Act
        List<Servicio> resultado = servicioResource.listarSubServicios(parentId);

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(subServicios, resultado);
        verify(servicioService).listarSubServicios(parentId);
    }

    @Test
    void agregarServicio_conServicioValido_deberiaCrearServicio() {
        // Arrange
        Servicio servicio = new Servicio();
        servicio.nombre = "Nuevo Servicio";
        servicio.costo = 100.0;
        servicio.cubiertoSeguro = true;
        servicio.id = 1L;

        when(servicioService.agregarServicio(any(Servicio.class), any())).thenReturn(servicio);

        // Act
        Response response = servicioResource.agregarServicio(servicio);

        // Assert
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(servicioService).agregarServicio(servicio, null);
    }

    @Test
    void agregarServicio_conServicioNull_deberiaRetornarBadRequest() {
        // Act
        Response response = servicioResource.agregarServicio(null);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void agregarServicio_conNombreVacio_deberiaRetornarBadRequest() {
        // Arrange
        Servicio servicio = new Servicio();
        servicio.nombre = "";

        // Act
        Response response = servicioResource.agregarServicio(servicio);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void agregarServicio_conNombreSoloEspacios_deberiaRetornarBadRequest() {
        // Arrange
        Servicio servicio = new Servicio();
        servicio.nombre = "   ";

        // Act
        Response response = servicioResource.agregarServicio(servicio);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void agregarSubServicio_conDatosValidos_deberiaAgregarSubServicio() {
        // Arrange
        Long parentId = 1L;
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("subServicioId", 2L);

        doNothing().when(servicioService).agregarSubServicio(parentId, 2L);

        // Act
        Response response = servicioResource.agregarSubServicio(parentId, requestBody);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(servicioService).agregarSubServicio(parentId, 2L);
    }

    @Test
    void agregarSubServicio_sinSubServicioId_deberiaRetornarBadRequest() {
        // Arrange
        Long parentId = 1L;
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("otroCampo", "valor");

        // Act
        Response response = servicioResource.agregarSubServicio(parentId, requestBody);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void agregarSubServicio_conSubServicioIdNoNumerico_deberiaRetornarBadRequest() {
        // Arrange
        Long parentId = 1L;
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("subServicioId", "no-es-numero");

        // Act
        Response response = servicioResource.agregarSubServicio(parentId, requestBody);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void eliminarServicio_deberiaEliminarServicio() {
        // Arrange
        Long servicioId = 1L;
        doNothing().when(servicioService).eliminarServicio(servicioId);

        // Act
        Response response = servicioResource.eliminarServicio(servicioId);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(servicioService).eliminarServicio(servicioId);
    }

    @Test
    void eliminarServicio_cuandoServiceLanzaExcepcion_deberiaRetornarInternalServerError() {
        // Arrange
        Long servicioId = 1L;
        doThrow(new RuntimeException("Error de base de datos")).when(servicioService).eliminarServicio(servicioId);

        // Act
        Response response = servicioResource.eliminarServicio(servicioId);

        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void eliminarRelacion_conRelacionExistente_deberiaEliminarRelacion() {
        // Arrange
        Long servicioPadreId = 1L;
        Long subServicioId = 2L;
        when(servicioService.eliminarRelacion(servicioPadreId, subServicioId)).thenReturn(true);

        // Act
        Response response = servicioResource.eliminarRelacion(servicioPadreId, subServicioId);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(servicioService).eliminarRelacion(servicioPadreId, subServicioId);
    }

    @Test
    void eliminarRelacion_conRelacionNoExistente_deberiaRetornarNotFound() {
        // Arrange
        Long servicioPadreId = 1L;
        Long subServicioId = 2L;
        when(servicioService.eliminarRelacion(servicioPadreId, subServicioId)).thenReturn(false);

        // Act
        Response response = servicioResource.eliminarRelacion(servicioPadreId, subServicioId);

        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void eliminarRelacion_cuandoServiceLanzaExcepcion_deberiaRetornarInternalServerError() {
        // Arrange
        Long servicioPadreId = 1L;
        Long subServicioId = 2L;
        when(servicioService.eliminarRelacion(servicioPadreId, subServicioId))
            .thenThrow(new RuntimeException("Error de base de datos"));

        // Act
        Response response = servicioResource.eliminarRelacion(servicioPadreId, subServicioId);

        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }
}
