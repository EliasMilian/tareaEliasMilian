package com.twitter.demo.repositories;

import com.twitter.demo.entities.Post;
import com.twitter.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    public List<Post> findAllByAuthor(User author);

}
