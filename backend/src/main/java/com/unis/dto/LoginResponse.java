package com.unis.dto;

public class LoginResponse {
    public String token;
    public User user;

    public LoginResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public static class User {
        public Long id;
        public String nombreUsuario;
        public String correo;
        public String roleName;

        public User(Long id, String nombreUsuario, String correo, String roleName) {
            this.id = id;
            this.nombreUsuario = nombreUsuario;
            this.correo = correo;
            this.roleName = roleName;
        }
    }
}
