package me.rhythmvarshney.blogapplication.service;

import jakarta.persistence.criteria.Predicate;
import me.rhythmvarshney.blogapplication.entity.Post;
import me.rhythmvarshney.blogapplication.entity.User;
import me.rhythmvarshney.blogapplication.repositories.PostRepository;
import me.rhythmvarshney.blogapplication.repositories.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostService {

    private PostRepository postRepository;

    private UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
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

    public Post editPost(String title, String content, int postId){
        Post post = findById(postId);
        post.setPostTitle(title);
        post.setTags(new HashSet<>());
        post.setPostContent(content);
        return post;
    }


    public Specification<Post> collectionContain(List<String> collectionList) {
        return (root, query, criteriaBuilder) -> {
            if (collectionList == null || collectionList.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            for(String email: collectionList){
                User user = userRepository.findByEmail(email);
                Predicate equal = criteriaBuilder.equal(root.get("author"),user);
                predicates.add(equal);
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public Page<Post> findAllPublishedPosts(Specification<Post> specification,PageRequest pageRequest){
        Specification<Post> spec = isPublished();
        Specification<Post> finalSpecification = Specification.where(spec).and(specification);
        return postRepository.findAll(finalSpecification, pageRequest);
    }

    public Page<Post> findAllPostsByAuthorEmail(String email, PageRequest pageRequest){
        return postRepository.findAllPostsByUserEmail(email, pageRequest);
    }

    public Page<Post> findAllDraftPostsByEmail(String email, PageRequest pageRequest){
        return postRepository.findAllDraftPostsByUserEmail(email,pageRequest);
    }

    private static Specification<Post> isPublished() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("isPublished"));
    }
}
