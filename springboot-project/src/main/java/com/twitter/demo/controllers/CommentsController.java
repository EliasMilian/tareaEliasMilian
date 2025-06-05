package com.twitter.demo.controllers;

import com.twitter.demo.entities.Comments;
import com.twitter.demo.entities.dto.CommentsDto;
import com.twitter.demo.entities.dto.ResponseCommentDto;
import com.twitter.demo.services.CommentsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    private static final Pattern UUID_REGEX = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    //crear un comentario
    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody @Valid CommentsDto request) {
        try {
          commentsService.createComment(request);
          return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //obtener todos los comentarios de un post
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<ResponseCommentDto>> getAllCommentsByPost(@PathVariable("postId") String id) {
        if (id == null || id.isEmpty() || !UUID_REGEX.matcher(id).matches()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            List<Comments> comments = commentsService.getCommentsByPost(id);
            List<ResponseCommentDto> dtoList = comments.stream()
                    .map(c -> new ResponseCommentDto(
                            c.getId(),
                            c.getMessage(),
                            c.getAuthor().getId(),
                            c.getPost().getId()
                    ))
                    .toList();
            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //obtener un comentario por id
    @GetMapping("/{id}")
    public ResponseEntity<ResponseCommentDto> getCommentById(@PathVariable("id") String id) {
        if (id == null || !UUID_REGEX.matcher(id).matches()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Comments comment = commentsService.getComment(id);
            ResponseCommentDto dto = new ResponseCommentDto(
                    comment.getId(),
                    comment.getMessage(),
                    comment.getAuthor().getId(),
                    comment.getPost().getId()
            );
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") String id) {
        if (id == null || !UUID_REGEX.matcher(id).matches()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            commentsService.deleteComment(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
