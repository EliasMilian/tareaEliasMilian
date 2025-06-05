package com.twitter.demo.services;

import com.twitter.demo.entities.User;
import com.twitter.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String emailOrId) throws UsernameNotFoundException {
        // Podemos soportar búsqueda por email o incluso por ID:
        Optional<User> optUser;
        if (isUUID(emailOrId)) {
            UUID id = UUID.fromString(emailOrId);
            optUser = userRepository.findById(id);
        } else {
            optUser = userRepository.findUserByEmail(emailOrId);
        }

        User user = optUser.orElseThrow(() ->
                new UsernameNotFoundException("User not found with email/id: " + emailOrId));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                getAuthorities(user.getRoles())
        );
    }


    private Collection<? extends GrantedAuthority> getAuthorities(String rolesCSV) {
        return Arrays.stream(rolesCSV.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }


    private boolean isUUID(String input) {
        try {
            UUID.fromString(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
