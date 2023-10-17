package me.rhythmvarshney.blogapplication.repositories;

import me.rhythmvarshney.blogapplication.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Integer> {
}
