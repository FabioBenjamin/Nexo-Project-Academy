package com.nexus.nexus.service;

import com.nexus.nexus.dto.LoginUserDTO;
import com.nexus.nexus.dto.RegisterUserDTO;
import com.nexus.nexus.exceptions.EmailAlreadyExistsException;
import com.nexus.nexus.exceptions.InvalidCredentialsException;
import com.nexus.nexus.exceptions.UsernameAlreadyExistsException;
import com.nexus.nexus.models.User;
import com.nexus.nexus.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    // final creates constant var. So there will be only one userRepository instance
    // that will be created once and cannot be modified
    // dependencies
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
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

        // Hash password
        String hash_password = passwordEncoder.encode(dto.getPassword());

        User new_user = new User();

        new_user.setEmail(dto.getEmail());
        new_user.setUsername(dto.getUsername());
        new_user.setPassword(hash_password);

        userRepository.save(new_user);
    }

    public Long login(LoginUserDTO dto){

        // find user by username if exists, else throw exception
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid Credentials"));

        // Verify password
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Invalid Credentials");
        }

        return user.getUser_id();

    }
}
