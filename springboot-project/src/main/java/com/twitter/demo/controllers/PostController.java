package com.twitter.demo.controllers;

import com.twitter.demo.entities.dto.CreatePostDto;
import com.twitter.demo.entities.dto.LikeDto;
import com.twitter.demo.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<Void> createPost(@RequestBody CreatePostDto request){
        postService.createPost(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/likes")
    public ResponseEntity<Void> likePost(@RequestBody LikeDto request){
        postService.likePost(request.getUserId(), request.getPostId());
        return ResponseEntity.noContent().build();
    }

}
