package com.Concesionaria.auth_service.service;

import com.Concesionaria.auth_service.model.User;
import com.Concesionaria.auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImp implements UserDetailsService {
    @Autowired
    private UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User no encontrado con email: " + email));
        if (!user.isActivo()) {
            throw new UsernameNotFoundException("User no activo con email: " + email);
        }
        return user;
    }


}
