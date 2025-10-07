package com.unis.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.unis.dto.LoginRequest;
import com.unis.dto.LoginResponse;
import com.unis.model.Rol;
import com.unis.model.Usuario;
import com.unis.service.UsuarioService;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST controller for managing user-related operations.
 * <p>
 * Handles user registration, login, activation, role assignment,
 * listing of active/inactive users, and role retrieval.
 * </p>
 */
@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioController {

    @Inject
    UsuarioService usuarioService;

    @GET
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @POST
    @Path("/registro")
    @Transactional
    public Response registrarUsuario(Usuario usuario) {
        Usuario registrado = usuarioService.registrarUsuario(usuario);
        return Response.status(Response.Status.CREATED)
                       .entity(Map.of("id", registrado.getId(), "correo", registrado.getCorreo()))
                       .build();
    }

    @POST
    @Path("/login")
    @Transactional
    public Response login(LoginRequest req) {
        if (req == null || req.correo == null || req.contrasena == null
                || req.correo.isBlank() || req.contrasena.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "correo y contrasena requeridos")).build();
        }

        try {
            Optional<LoginResponse> out = usuarioService.intentarLogin(req.correo, req.contrasena);
            if (out.isEmpty()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Map.of("error", "Credenciales inválidas")).build();
            }
            return Response.ok(out.get()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Credenciales inválidas")).build();
        }
    }

    @GET
    @Path("/inactivos")
    public List<Usuario> listarUsuariosInactivos() {
        return usuarioService.listarUsuariosInactivos();
    }

    @GET
    @Path("/roles")
    public List<Rol> listarRoles() {
        return usuarioService.listarRoles();
    }

    @PUT
    @Path("/{id}/activar")
    @Transactional
    public Response activarUsuario(@PathParam("id") Long id, ActivarUsuarioDTO dto) {
        try {
            Usuario usuarioActivado = usuarioService.activarUsuario(id, dto.getRolId());
            return Response.ok(usuarioActivado).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(e.getMessage())
                           .build();
        }
    }

    // Nuevo endpoint para desactivar usuario
    @PUT
    @Path("/{id}/desactivar")
    @Transactional
    public Response desactivarUsuario(@PathParam("id") Long id) {
        try {
            Usuario usuarioDesactivado = usuarioService.desactivarUsuario(id);
            return Response.ok(usuarioDesactivado).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(e.getMessage())
                           .build();
        }
    }

    public static class ActivarUsuarioDTO {
        private Long rolId;

        public Long getRolId() {
            return rolId;
        }

        public void setRolId(Long rolId) {
            this.rolId = rolId;
        }
    }
}
