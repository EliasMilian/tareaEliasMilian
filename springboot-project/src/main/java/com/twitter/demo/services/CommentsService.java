package com.twitter.demo.services;

import com.twitter.demo.entities.Comments;
import com.twitter.demo.entities.Post;
import com.twitter.demo.entities.User;
import com.twitter.demo.entities.dto.CommentsDto;
import com.twitter.demo.repositories.CommentsRepository;
import com.twitter.demo.repositories.PostRepository;
import com.twitter.demo.repositories.UserRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@NoArgsConstructor
public class CommentsService {
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    public void createComment(CommentsDto Info) {
        Comments comment = new Comments();
        comment.setMessage(Info.getMessage());

        UUID authorId = UUID.fromString(Info.getAuthorId().toString());
        Optional<User> user = userRepository.findById(authorId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        comment.setAuthor(user.get());

        UUID postId = UUID.fromString(Info.getPostId().toString());
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new RuntimeException("Post not found");
        }
        comment.setPost(post.get());

        commentsRepository.save(comment);
    }

    public Comments getComment(String id) {
        UUID commentId = UUID.fromString(id);
        Optional<Comments> optionalComment = commentsRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            throw new RuntimeException("Comment not found");
        }
        return optionalComment.get();
    }

    public List<Comments> getComments() {
        return commentsRepository.findAll();
    }

    public List<Comments> getCommentsByPost(String postId) {
        UUID postUUID = UUID.fromString(postId);
        Optional<Post> optionalPost = postRepository.findById(postUUID);
        if (optionalPost.isEmpty()) {
            throw new RuntimeException("Post not found");
        }
        return commentsRepository.findAllCommentsByPostId(postUUID);
    }

    public Comments updateComment(String CommentId, CommentsDto Info) {
        UUID commentId = UUID.fromString(CommentId);
        Optional<Comments> optionalComment = commentsRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            throw new RuntimeException("Comment not found");
        }
        Comments comment = optionalComment.get();
        comment.setMessage(Info.getMessage());

        UUID authorId = UUID.fromString(Info.getAuthorId().toString());
        Optional<User> user = userRepository.findById(authorId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        comment.setAuthor(user.get());

        UUID postId = UUID.fromString(Info.getPostId().toString());
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new RuntimeException("Post not found");
        }
        comment.setPost(post.get());

        return commentsRepository.save(comment);
    }

    public void deleteComment(String id){
        UUID commentId = UUID.fromString(id);
        Optional<Comments> optionalComment = commentsRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            throw new RuntimeException("Comment not found");
        }
        commentsRepository.deleteById(commentId);
    }
}
