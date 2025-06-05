package com.twitter.demo.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Data
public class UserDto {

    private UUID id;
    private String name;
    private String email;
}
