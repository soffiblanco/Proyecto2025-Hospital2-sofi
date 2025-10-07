package com.unis.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.unis.dto.LoginResponse;
import com.unis.model.Rol;
import com.unis.model.Usuario;
import com.unis.repository.RolRepository;
import com.unis.repository.UsuarioRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * Servicio que implementa la lógica de negocio relacionada con los usuarios.
 * Se encarga de la gestión, registro, autenticación y administración (activación y asignación de roles)
 * de los usuarios.
 */
@ApplicationScoped
public class UsuarioService {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    RolRepository rolRepository;

    @Inject
    EntityManager entityManager;

    /**
     * Retorna la lista de todos los usuarios registrados.
     *
     * @return Lista de objetos {@link Usuario}.
     */
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.listAll();
    }

    public Optional<LoginResponse> intentarLogin(String correo, String contrasena) {
        if (isBlank(correo) || isBlank(contrasena)) {
            return Optional.empty();
        }

        Object[] fila;
        try {
            fila = entityManager
                    .createQuery(
                            "select u.id, u.nombreUsuario, u.correo, u.contrasena, r.roleName "
                                    + "from Usuario u left join u.rol r where u.correo = :correo",
                            Object[].class)
                    .setParameter("correo", correo.trim())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } catch (PersistenceException e) {
            if (isRolConstraintMissing(e)) {
                return Optional.empty();
            }
            throw new WebApplicationException("Error al iniciar sesión", e,
                    Response.Status.INTERNAL_SERVER_ERROR);
        }

        if (fila == null) {
            return Optional.empty();
        }

        String contrasenaAlmacenada = (String) fila[3];
        if (contrasenaAlmacenada == null || !contrasenaAlmacenada.equals(contrasena)) {
            return Optional.empty();
        }

        LoginResponse.User user = new LoginResponse.User(
                (Long) fila[0],
                (String) fila[1],
                (String) fila[2],
                (String) fila[4]
        );

        return Optional.of(new LoginResponse("dummy-token", user));
    }

    /**
     * Obtiene un usuario según el correo electrónico.
     *
     * @param correo Correo del usuario.
     * @return Objeto {@link Usuario} encontrado o null si no existe.
     */
    public Usuario obtenerUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    /**
     * Registra un nuevo usuario verificando que el correo no esté duplicado.
     *
     * @param usuario Objeto {@link Usuario} a registrar.
     * @throws WebApplicationException si el correo ya está registrado.
     */
    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuario == null || isBlank(usuario.getNombreUsuario())
                || isBlank(usuario.getCorreo()) || isBlank(usuario.getContrasena())) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Campos requeridos: nombreUsuario, correo, contrasena"))
                    .build());
        }

        String correo = usuario.getCorreo().trim();

        Long existentes = entityManager
                .createQuery("select count(u) from Usuario u where u.correo = :correo", Long.class)
                .setParameter("correo", correo)
                .getSingleResult();

        if (existentes != 0) {
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", "Correo ya registrado"))
                    .build());
        }

        Long rolId = (usuario.getRol() != null && usuario.getRol().getId() != null)
                ? usuario.getRol().getId()
                : 1L;

        Rol rol = resolveRolReference(rolId);

        Usuario nuevo = new Usuario();
        nuevo.setNombreUsuario(usuario.getNombreUsuario());
        nuevo.setCorreo(correo);
        nuevo.setContrasena(usuario.getContrasena());
        nuevo.setRol(rol);

        try {
            entityManager.persist(nuevo);
            entityManager.flush();
        } catch (PersistenceException e) {
            if (isCorreoConstraintViolation(e)) {
                throw new WebApplicationException(Response.status(Response.Status.CONFLICT)
                        .entity(Map.of("error", "Correo ya registrado"))
                        .build());
            }
            throw new WebApplicationException("Error al registrar usuario", e,
                    Response.Status.INTERNAL_SERVER_ERROR);
        }

        return nuevo;
    }

    /**
     * Retorna la lista de usuarios que se encuentran inactivos (estado = 0).
     *
     * @return Lista de usuarios inactivos.
     */
    public List<Usuario> listarUsuariosInactivos() {
        return usuarioRepository.find("estado", 0).list();
    }

    /**
     * Activa un usuario asignándole un rol y cambiando su estado a activo (1).
     *
     * @param idUsuario Identificador del usuario a activar.
     * @param idRol     Identificador del rol a asignar.
     * @return Usuario actualizado con el rol asignado y estado activo.
     * @throws WebApplicationException si el usuario no existe o si el rol no es válido.
     */
    @Transactional
    public Usuario activarUsuario(Long idUsuario, Long idRol) {
        Usuario usuario = usuarioRepository.findById(idUsuario);
        if (usuario == null) {
            throw new WebApplicationException("Usuario no encontrado", Response.Status.NOT_FOUND);
        }
        Rol rol = rolRepository.findById(idRol);
        if (rol == null) {
            throw new WebApplicationException("Rol no válido", Response.Status.BAD_REQUEST);
        }
        usuario.setRol(rol);
        usuario.setEstado(1);
        return usuario;
    }

    /**
     * Desactiva un usuario cambiando su estado a 0.
     *
     * @param idUsuario Identificador del usuario a desactivar.
     * @return Usuario actualizado con estado inactivo (0).
     * @throws WebApplicationException si el usuario no existe.
     */
    @Transactional
    public Usuario desactivarUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario);
        if (usuario == null) {
            throw new WebApplicationException("Usuario no encontrado", Response.Status.NOT_FOUND);
        }
        usuario.setEstado(0);
        return usuario;
    }

    /**
     * Retorna la lista de todos los roles disponibles.
     *
     * @return Lista de objetos {@link Rol}.
     */
    public List<Rol> listarRoles() {
        return rolRepository.listAll();
    }

    private Rol resolveRolReference(Long rolId) {
        try {
            Rol reference = entityManager.getReference(Rol.class, rolId);
            reference.getId();
            return reference;
        } catch (jakarta.persistence.EntityNotFoundException ex) {
            Rol fallback = entityManager
                    .createQuery("select r from Rol r where r.roleName = :nombre", Rol.class)
                    .setParameter("nombre", "USER")
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (fallback != null) {
                try {
                    Rol reference = entityManager.getReference(Rol.class, fallback.getId());
                    reference.getId();
                    return reference;
                } catch (jakarta.persistence.EntityNotFoundException ignored) {
                    // caer en error estándar
                }
            }

            throw new WebApplicationException(Response.serverError()
                    .entity(Map.of("error", "Rol por defecto no disponible"))
                    .build());
        }
    }

    private boolean isCorreoConstraintViolation(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            String message = current.getMessage();
            if (message != null) {
                String upper = message.toUpperCase();
                if (upper.contains("SQLITE_CONSTRAINT") || upper.contains("CONSTRAINT FAILED")
                        || upper.contains("UNIQUE")) {
                    if (upper.contains("CORREO") || upper.contains("USUARIO.CORREO")) {
                        return true;
                    }
                }
            }
            current = current.getCause();
        }
        return false;
    }

    private boolean isRolConstraintMissing(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            String message = current.getMessage();
            if (message != null && message.contains("FetchNotFoundException")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
