package com.unis.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.unis.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UsuarioRepositoryTest {

    @InjectMocks
    UsuarioRepository usuarioRepository;

    @Mock
    PanacheQuery<Usuario> panacheQuery;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByCorreo_devuelve_usuario() {
        Usuario u = new Usuario();
        UsuarioRepository repo = spy(new UsuarioRepository());
        doReturn(panacheQuery).when(repo).find(eq("correo"), eq("a@b.com"));
        when(panacheQuery.firstResult()).thenReturn(u);

        Usuario out = repo.findByCorreo("a@b.com");
        assertEquals(u, out);
        verify(repo).find("correo", "a@b.com");
        verify(panacheQuery).firstResult();
    }
}