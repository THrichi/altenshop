package com.application.altenshop.services.impl;

import com.application.altenshop.dtos.UserRegisterDTO;
import com.application.altenshop.models.User;
import com.application.altenshop.repositories.UserRepository;
import com.application.altenshop.security.JWTService;
import com.application.altenshop.services.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(UserRegisterDTO dto) {
        // empêche la création d’un compte avec un email déjà existant
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("Un compte existe déjà avec cet email.");
        }

        User user = new User();
        user.setUsername(dto.username());
        user.setFirstname(dto.firstname());
        user.setEmail(dto.email());
        // hash du mot de passe avant sauvegarde
        user.setPassword(passwordEncoder.encode(dto.password()));

        return userRepository.save(user);
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // vérifie le mot de passe fourni
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mot de passe invalide");
        }

        // renvoie un JWT signé contenant l’email de l’utilisateur
        return jwtService.generateToken(user.getEmail());
    }

    public User findByEmail(String email) {
        // méthode utilitaire réutilisée dans le contrôleur pour récupérer l’utilisateur courant
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }
}
