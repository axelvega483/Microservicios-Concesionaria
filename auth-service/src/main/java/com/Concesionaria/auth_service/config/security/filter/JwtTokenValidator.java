package com.Concesionaria.auth_service.config.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

public class JwtTokenValidator extends OncePerRequestFilter {
    private JwtUtils jwtUtils;

    public JwtTokenValidator(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        System.out.println("=== AUTH-SERVICE DEBUG ===");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Authorization header: " + (jwtToken != null ? "Presente" : "Ausente"));

        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
            try {
                DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
                String username = jwtUtils.extractUsername(decodedJWT);
                String authorities = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();

                System.out.println("Username: " + username);
                System.out.println("Authorities raw: '" + authorities + "'");

                Collection<? extends GrantedAuthority> authoritiesList = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

                System.out.println("Authorities list size: " + authoritiesList.size());
                for (GrantedAuthority auth : authoritiesList) {
                    System.out.println("  -> Authority: '" + auth.getAuthority() + "'");
                }

                // Verificar si tiene ADMIN
                boolean hasAdmin = authoritiesList.stream()
                        .anyMatch(a -> a.getAuthority().equals("ADMIN") ||
                                a.getAuthority().equals("ROLE_ADMIN"));
                System.out.println("Has ADMIN (any form): " + hasAdmin);

                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authoritiesList);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                System.err.println("Error validando token: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("NO HAY TOKEN!");
        }
        filterChain.doFilter(request, response);
    }
}