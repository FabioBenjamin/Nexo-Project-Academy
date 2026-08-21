package com.nexus.nexus.service;

import com.nexus.nexus.dto.RegisterUserDTO;
import com.nexus.nexus.exceptions.EmailAlreadyExistsException;
import com.nexus.nexus.exceptions.UsernameAlreadyExistsException;
import com.nexus.nexus.models.User;
import com.nexus.nexus.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    // final creates constant var. So there will be only one userRepository instance
    // that will be created once and cannot be modified
    // dependencies
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void register(RegisterUserDTO dto){

        // valid username
        if (userRepository.existsByUsername(dto.getUsername())){
            throw new UsernameAlreadyExistsException("Username is taken. Please choose another one");
        }
        // valid email
        if (userRepository.existsByEmail(dto.getEmail())){
            throw new EmailAlreadyExistsException("Email is already in use");
            // Depois tem que fazer a validação de email usando EmailValidator
        }

        // Pra fazer: Pegar senha, criptografar em hash e salvar em new_user

        User new_user = new User();

        new_user.setEmail(dto.getEmail());
        new_user.setUsername(dto.getUsername());
        // Falta criptografar a senha para hash
        new_user.setPassword(dto.getPassword());

        userRepository.save(new_user);
    }
}
