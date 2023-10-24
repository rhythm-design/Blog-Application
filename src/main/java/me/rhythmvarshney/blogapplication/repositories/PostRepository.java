package me.rhythmvarshney.blogapplication.repositories;

import lombok.NonNull;
import me.rhythmvarshney.blogapplication.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post,Integer>, JpaSpecificationExecutor<Post> {

    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN p.tags t " +
            "WHERE LOWER(p.postContent) LIKE %:keyword% " +
            "OR LOWER(p.postTitle) LIKE %:keyword% " +
            "OR LOWER(p.author) LIKE %:keyword% " +
            "OR LOWER(t.name) LIKE %:keyword%")
    Set<Post> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT p.author FROM Post p")
    List<String> findAllAuthors();


    @Query("SELECT p FROM Post p WHERE p.isPublished = true AND p.author.email = :email")
    Page<Post> findAllPostsByUserEmail(@Param("email") String email, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.isPublished = false AND p.author.email = :email")
    Page<Post> findAllDraftPostsByUserEmail(@Param("email") String email, Pageable pageable);

}
