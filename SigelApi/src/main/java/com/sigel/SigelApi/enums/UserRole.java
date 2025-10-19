package com.sigel.SigelApi.enums;

public enum UserRole {
    ALUMNO("ROLE_ALUMNO"),
    MAESTRO("ROLE_MAESTRO"),
    ADMINISTRADOR("ROLE_ADMIN");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}