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
public class CommentsDto {
    private UUID id;
    private String message;
    private UUID authorId;
    private UUID postId;
}
