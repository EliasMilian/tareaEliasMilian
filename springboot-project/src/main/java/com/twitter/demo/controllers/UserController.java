package com.twitter.demo.controllers;

import com.twitter.demo.entities.Post;
import com.twitter.demo.entities.dto.LoginRequestDto;
import com.twitter.demo.entities.dto.LoginResponseDto;
import com.twitter.demo.entities.dto.RegisterDto;
import com.twitter.demo.entities.dto.UserDto;
import com.twitter.demo.entities.dto.UserEncryptDto;
import com.twitter.demo.entities.dto.UserPostsDto;
import com.twitter.demo.security.JwtUtils;
import com.twitter.demo.services.PostService;
import com.twitter.demo.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    private static final Pattern UUID_REGEX =
            Pattern.compile("^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$");

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> createUser(@RequestBody @Valid RegisterDto info) {
        try {
            UserDto created = userService.createUser(info);
            String token = jwtUtils.generateJwtToken(created.getEmail());
            return ResponseEntity.status(201).body(new LoginResponseDto(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            String token = jwtUtils.generateJwtToken(request.getEmail());
            return ResponseEntity.ok(new LoginResponseDto(token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") String id) {
        if (id == null || !UUID_REGEX.matcher(id).matches()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            UserDto user = userService.getUser(id);
            return ResponseEntity.ok(user);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email")
    public ResponseEntity<UserDto> getUserByEmail(@RequestParam("email") String email) {
        try {
            UserDto user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/posts")
    public ResponseEntity<List<UserPostsDto>> getUserPosts(@PathVariable("id") String id) {
        if (id == null || !UUID_REGEX.matcher(id).matches()) {
            return ResponseEntity.badRequest().build();
        }
        UUID userId = UUID.fromString(id);
        List<UserPostsDto> posts = postService.getUserPosts(userId);
        return ResponseEntity.ok(posts);
    }


    @GetMapping("/likes")
    public ResponseEntity<List<Post>> getLikedPosts(@RequestParam("userId") String id) {
        if (id == null || !UUID_REGEX.matcher(id).matches()) {
            return ResponseEntity.badRequest().build();
        }
        UUID userId = UUID.fromString(id);
        List<Post> liked = postService.getLikedPosts(userId);
        return ResponseEntity.ok(liked);
    }

    @PostMapping("/admin/create")
    public ResponseEntity<LoginResponseDto> createAdmin(@RequestBody @Valid RegisterDto info) {
        try {
            UserDto created = userService.createAdmin(info);
            String token = jwtUtils.generateJwtToken(created.getEmail());
            return ResponseEntity.status(201).body(new LoginResponseDto(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserEncryptDto>> getAllUsersForAdmin() {
        List<UserEncryptDto> list = userService.getAllUsersForAdmin();
        return ResponseEntity.ok(list);
    }
}
