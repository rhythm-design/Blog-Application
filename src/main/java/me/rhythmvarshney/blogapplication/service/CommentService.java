package me.rhythmvarshney.blogapplication.service;

import lombok.extern.slf4j.Slf4j;
import me.rhythmvarshney.blogapplication.entity.Comment;
import me.rhythmvarshney.blogapplication.repositories.CommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CommentService {

    private CommentsRepository commentsRepository;

    @Autowired
    public CommentService(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }

    public Comment findById(int id){
        Optional<Comment> commentOptional = commentsRepository.findById(id);
        if(commentOptional.isEmpty()){
            throw new RuntimeException("Comment Not present with id: " + id);
        }
        return commentOptional.get();
    }

    public void deleteById(int id){
        commentsRepository.deleteById(id);
        log.trace("Successfully deleted comment by id: " + id);
    }

    public void save(Comment comment){
        commentsRepository.save(comment);
        log.trace("Successfully saved comment: " + comment);
    }

}
