package me.rhythmvarshney.blogapplication.service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import me.rhythmvarshney.blogapplication.entity.Post;
import me.rhythmvarshney.blogapplication.entity.Tag;
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

    private MergeFilterService<User,Post> userPostMergeFilterService;

    public PostService(PostRepository postRepository, UserRepository userRepository, MergeFilterService<User,Post> userPostMergeFilterService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.userPostMergeFilterService = userPostMergeFilterService;
    }

    public Post findById(int id){
        Optional<Post> postOptional = postRepository.findById(id);

        if(postOptional.isEmpty()){
            throw new RuntimeException("Post not found by id: " + id);
        }
        return postOptional.get();
    }

    public Page<Post> findAll(Specification<Post> specification, Pageable page){
        return postRepository.findAll(specification,page);
    }

    public void deleteById(int id){
        postRepository.deleteById(id);
        System.out.println("Successflully deleted post with id: " + id);
    }

    public void save(Post post){
        postRepository.save(post);
        System.out.println("Secessfully saved the post");
    }

    private Page<Post> findAllOrderByPublishTimeAsc(){
        PageRequest pageRequest = PageRequest.of(0,6,Sort.Direction.DESC,"publishTime");
        Page post = postRepository.findAll(pageRequest);
        return post;
    }

    private Page<Post> findAllOrderByPublishTimeDesc(){
        PageRequest pageRequest = PageRequest.of(0,6,Sort.Direction.DESC,"publishTime");
        Page post = postRepository.findAll(pageRequest);
        return post;
    }

    public Page<Post> findAll(int pageNumber, int pageSize){
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        return postRepository.findAll(pageRequest);
    }

    private Page<Post> findAllOrderByAuthorAsc(int pageSize){
        PageRequest pageRequest = PageRequest.of(0,6,Sort.Direction.ASC,"author");
        Page post = postRepository.findAll(pageRequest);
        return post;
    }

    private Page<Post> findAllOrderByAuthorDesc(){
        PageRequest pageRequest = PageRequest.of(0,6,Sort.Direction.DESC,"author");
        Page post = postRepository.findAll(pageRequest);
        return post;
    }

    private Page<Post> findAllOrderByTitleAsc(){
        PageRequest pageRequest = PageRequest.of(0,6,Sort.Direction.DESC,"postTitle");
        Page post = postRepository.findAll(pageRequest);
        return post;
    }

    public Page<Post> findAllByParams(Map<String,String> params){

        Page<Post> posts;
        // Handles sortField param Exception handling
        String sortField = params.getOrDefault("sortField",null);
        String sortOrder = params.getOrDefault("order",null);
        String pageNumber = params.getOrDefault("start","0");
        String countLimit = params.getOrDefault("limit","6");

        // Handles Search Input for keywords
        String searchKeywords = params.getOrDefault("search",null);
        if(searchKeywords !=null){
            posts = searchKeywordsInPost(searchKeywords, Integer.parseInt(pageNumber), Integer.parseInt(countLimit));
        }else if(sortField != null){
            posts = findSortedPostByField(sortField, sortOrder, countLimit);
        }else{
            posts = findAll(Integer.parseInt(pageNumber), Integer.parseInt(countLimit));
        }
        return posts;
    }

    private Page<Post> findSortedPostByField(String sortField, String sortOrder, String countLimit){
        Page<Post> post;
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
                post = findAllOrderByAuthorAsc(Integer.parseInt(countLimit));
            }else{
                post = findAllOrderByAuthorDesc();
            }
        }else{
            //title sorted
            post = findAllOrderByTitleAsc();
        }
        return post;
    }

    private Page<Post> searchKeywordsInPost(String keywords, int pageNumber, int limit) {
        String[] keywordsList = keywords.split(" ");
        Set<Post> searchSet = new TreeSet<>((a,b)->b.getId() - a.getId());

        for (String keyword : keywordsList) {
            Set<Post> single = postRepository.findByKeyword(keyword);
            searchSet.addAll(single);
        }

        int start = pageNumber * limit;
        int end = Math.min(start + limit, searchSet.size());

        List<Post> pagedData = new ArrayList<>(searchSet).subList(start, end);

        return new PageImpl<>(pagedData, PageRequest.of(pageNumber, limit), searchSet.size());
    }

    public Post editPost(String title, String content, int postId){
        Post post = findById(postId);
        post.setPostTitle(title);
        post.setTags(new HashSet<>());
        post.setPostContent(content);
        return post;
    }

    public List<String> getAllAuthors(){
        return postRepository.findAllAuthors();
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
        return postRepository.findPublishedPosts(specification, pageRequest);
    }

    public Page<Post> findAllPostsByAuthorEmail(String email, PageRequest pageRequest){
        return postRepository.findAllPostsByUserEmail(email, pageRequest);
    }

    public Page<Post> findAllDraftPostsByEmail(String email, PageRequest pageRequest){
        return postRepository.findAllDraftPostsByUserEmail(email,pageRequest);
    }
}
