package me.rhythmvarshney.blogapplication.repositories;

import me.rhythmvarshney.blogapplication.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentsRepository extends JpaRepository<Comment,Integer> {
}
