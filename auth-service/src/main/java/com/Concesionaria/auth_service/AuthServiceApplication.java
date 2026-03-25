package com.Concesionaria.auth_service;

import com.Concesionaria.auth_service.DTO.UserGetDTO;
import com.Concesionaria.auth_service.model.User;
import com.Concesionaria.auth_service.service.IUserServicie;
import com.Concesionaria.auth_service.util.RolUser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}
    @Bean
    CommandLineRunner initDatabase(IUserServicie usuarioService
    ) {
        return args -> {
            List<UserGetDTO> usuarios = usuarioService.findAll();
            if (usuarios.isEmpty()) {
                User userADMIN = new User();
                userADMIN.setRol(RolUser.ADMIN);
                userADMIN.setNombre("ADMIN");
                userADMIN.setPassword("admin");
                userADMIN.setEmail("admin@admin.com");
                userADMIN.setDni("1234567");

                usuarioService.save(userADMIN);
                System.out.println("User administrador inicializado con éxito.");
            }
        };
    }
}
