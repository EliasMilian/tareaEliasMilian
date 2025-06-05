package com.twitter.demo.services;

import com.twitter.demo.entities.User;
import com.twitter.demo.entities.dto.RegisterDto;
import com.twitter.demo.entities.dto.UserDto;
import com.twitter.demo.entities.dto.UserEncryptDto;
import com.twitter.demo.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public UserDto createUser(RegisterDto userInfo) {
        validateEmailNotTaken(userInfo.getEmail());

        User user = new User();
        user.setName(userInfo.getName());
        user.setEmail(userInfo.getEmail());
        user.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        user.setRoles("ROLE_USER");

        User saved = userRepository.save(user);
        return new UserDto(saved.getId(), saved.getName(), saved.getEmail());
    }

    @Transactional
    public UserDto createAdmin(RegisterDto userInfo) {

        validateEmailNotTaken(userInfo.getEmail());

        long adminsCount = userRepository.countAdmins();
        if (adminsCount > 0) {

            boolean isAdmin = org.springframework.security.core.context.SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getAuthorities()
                    .stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin) {
                throw new AccessDeniedException("Solo un ADMIN puede crear otro ADMIN");
            }
        }

        User admin = new User();
        admin.setName(userInfo.getName());
        admin.setEmail(userInfo.getEmail());
        admin.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        admin.setRoles("ROLE_ADMIN");

        User saved = userRepository.save(admin);
        return new UserDto(saved.getId(), saved.getName(), saved.getEmail());
    }

    public UserDto getUser(String id) {
        User u = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con ID: " + id));
        return new UserDto(u.getId(), u.getName(), u.getEmail());
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(u -> new UserDto(u.getId(), u.getName(), u.getEmail()))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserEncryptDto> getAllUsersForAdmin() {
        return userRepository.findAll()
                .stream()
                .map(u -> new UserEncryptDto(
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.getPassword() // contraseña encriptada
                ))
                .collect(Collectors.toList());
    }

    public UserDto getUserByEmail(String email) {
        User u = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con email: " + email));
        return new UserDto(u.getId(), u.getName(), u.getEmail());
    }

    private void validateEmailNotTaken(String email) {
        if (userRepository.findUserByEmail(email).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado: " + email);
        }
    }
}
