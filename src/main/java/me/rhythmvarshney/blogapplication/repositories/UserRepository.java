package me.rhythmvarshney.blogapplication.repositories;

import me.rhythmvarshney.blogapplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}
