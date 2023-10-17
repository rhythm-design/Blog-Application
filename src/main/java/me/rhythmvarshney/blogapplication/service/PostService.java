package me.rhythmvarshney.blogapplication.service;

import me.rhythmvarshney.blogapplication.entity.Post;
import me.rhythmvarshney.blogapplication.repositories.PostRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostService {

    private PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post findById(int id){
        Optional<Post> postOptional = postRepository.findById(id);

        if(postOptional.isEmpty()){
            throw new RuntimeException("Post not found by id: " + id);
        }
        return postOptional.get();
    }

    public void deleteById(int id){
        postRepository.deleteById(id);
        System.out.println("Successflully deleted post with id: " + id);
    }

    public void save(Post post){
        postRepository.save(post);
        System.out.println("Secessfully saved the post");
    }
}
