package me.rhythmvarshney.blogapplication.dao;

import jakarta.persistence.EntityManager;
import me.rhythmvarshney.blogapplication.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TagDAO {

    private EntityManager entityManager;

    @Autowired
    public TagDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void save(Tag tag){
        entityManager.merge(tag);
    }
}
