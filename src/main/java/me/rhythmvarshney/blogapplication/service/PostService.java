package me.rhythmvarshney.blogapplication.service;

import me.rhythmvarshney.blogapplication.entity.Post;
import me.rhythmvarshney.blogapplication.repositories.PostRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private List<Post> findAllOrderByPublishTimeAsc(){
        return postRepository.findAll(Sort.by(Sort.Direction.ASC,"publishTime"));
    }

    private List<Post> findAllOrderByPublishTimeDesc(){
        return postRepository.findAll(Sort.by(Sort.Direction.DESC,"publishTime"));
    }

    public List<Post> findAll(){
        return postRepository.findAll();
    }

    private List<Post> findAllOrderByAuthorAsc(){
        return postRepository.findAll(Sort.by(Sort.Direction.ASC,"author"));
    }

    private List<Post> findAllOrderByAuthorDesc(){
        return postRepository.findAll(Sort.by(Sort.Direction.DESC,"author"));
    }

    private List<Post> findAllOrderByTitleAsc(){
        return postRepository.findAll(Sort.by(Sort.Direction.ASC,"postTitle"));
    }

    public List<Post> findAllByParams(Map<String,String> params){

        List<Post> posts;
        // Handles sortField param
        String sortField = params.getOrDefault("sortField",null);
        String sortOrder = params.getOrDefault("order",null);
        if(sortField != null){
            posts = findSortedPostByField(sortField, sortOrder);
        }else{
            posts = findAll();
        }
        return posts;
    }

    private List<Post> findSortedPostByField(String sortField, String sortOrder){
        List<Post> post;
        if(sortField.equals("publishedAt")){
            // Sort by time
            if(sortOrder.equals("asc")){
                post = findAllOrderByPublishTimeAsc();
            }else{
                post = findAllOrderByPublishTimeDesc();
            }
        }else if(sortField.equals("author")){
            //Sort by author
            if(sortOrder.equals("asc")){
                post = findAllOrderByAuthorAsc();
            }else{
                post = findAllOrderByAuthorDesc();
            }
        }else{
            //title sorted
            post = findAllOrderByTitleAsc();
        }
        return post;
    }
}
