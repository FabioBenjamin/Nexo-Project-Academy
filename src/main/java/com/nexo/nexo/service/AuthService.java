package com.nexo.nexo.service;

import com.nexo.nexo.dto.LoginUserDTO;
import com.nexo.nexo.dto.RegisterUserDTO;
import com.nexo.nexo.exceptions.EmailAlreadyExistsException;
import com.nexo.nexo.exceptions.InvalidCredentialsException;
import com.nexo.nexo.exceptions.UsernameAlreadyExistsException;
import com.nexo.nexo.models.User;
import com.nexo.nexo.repository.UserRepository;
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
        
        // Valid email format
        if (!EmailValidator.getInstance().isValid(dto.getEmail())) {
            throw new IllegalArgumentException("Invalid email");
        }

        // Valid email already exists
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already in use");
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
