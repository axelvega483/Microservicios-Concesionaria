package com.Concesionaria.auth_service.config.security;

import com.Concesionaria.auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("usuarioSecurity")
public class UsuarioSecurity {
    @Autowired
    private UserRepository repo;

    public boolean isCurrentUser(Integer userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        String email = authentication.getName();
        return repo.findByEmail(email)
                .map(usuario -> usuario.getId().equals(userId))
                .orElse(false);
    }
}
