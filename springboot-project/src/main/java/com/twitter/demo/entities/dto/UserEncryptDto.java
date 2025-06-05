package com.twitter.demo.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEncryptDto {
    private UUID id;
    private String name;
    private String email;
    private String encryptedPassword;
}
