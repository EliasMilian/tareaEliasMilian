package com.twitter.demo.repositories;

import com.twitter.demo.entities.Comments;
import com.twitter.demo.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, UUID> {
    List<Comments> findAllCommentsByPostId(UUID postId);
}
