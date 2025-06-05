package com.twitter.demo.controllers;

import com.twitter.demo.entities.User;
import com.twitter.demo.entities.dto.LoginRequestDto;
import com.twitter.demo.entities.dto.LoginResponseDto;
import com.twitter.demo.entities.dto.RegisterDto;
import com.twitter.demo.repositories.UserRepository;
import com.twitter.demo.security.JwtUtils;
import com.twitter.demo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody @Valid RegisterDto registerDto) {
        if (userRepository.findUserByEmail(registerDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        User user = new User();
        user.setName(registerDto.getName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRoles("USER");

        userRepository.save(user);
        return ResponseEntity.status(201).build();
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> authenticateUser(
            @RequestBody @Valid LoginRequestDto loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            String token = jwtUtils.generateJwtToken(loginRequest.getEmail());
            return ResponseEntity.ok(new LoginResponseDto(token));

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).build();
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).build();
        }
    }
}
