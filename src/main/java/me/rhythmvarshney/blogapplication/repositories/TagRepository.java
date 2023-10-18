package me.rhythmvarshney.blogapplication.repositories;

import me.rhythmvarshney.blogapplication.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag,Integer> {
    @Query("SELECT t from Tag t WHERE t.name = :name")
    Tag findByName(String name);
}
