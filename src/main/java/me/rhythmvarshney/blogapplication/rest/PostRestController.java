package me.rhythmvarshney.blogapplication.rest;


import jakarta.servlet.http.HttpServletRequest;
import me.rhythmvarshney.blogapplication.entity.Post;
import me.rhythmvarshney.blogapplication.entity.Tag;
import me.rhythmvarshney.blogapplication.entity.User;
import me.rhythmvarshney.blogapplication.rest.auth.AuthenticationHandler;
import me.rhythmvarshney.blogapplication.service.MergeFilterService;
import me.rhythmvarshney.blogapplication.service.PostService;
import me.rhythmvarshney.blogapplication.service.TagService;
import me.rhythmvarshney.blogapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {

    private PostService postService;

    private TagService tagService;

    private UserService userService;

    private MergeFilterService<Post,Tag> postTagMergeFilterService;

    private AuthenticationHandler authenticationHandler;

    @Autowired
    public PostRestController(PostService postService, TagService tagService, MergeFilterService<Post,Tag> postTagMergeFilterService, UserService userService, AuthenticationHandler authenticationHandler) {
        this.postService = postService;
        this.tagService = tagService;
        this.postTagMergeFilterService = postTagMergeFilterService;
        this.userService = userService;
        this.authenticationHandler = authenticationHandler;
    }


    @GetMapping
    public Page<Post> homepage(@RequestParam(value = "tag", required = false) List<String> passedTagList,
                               @RequestParam(value = "order", required = false) String sortOrder,
                               @RequestParam(value = "search", required = false) String searchedText,
                               @RequestParam(value = "start", required = false) String start,
                               @RequestParam(value = "limit", required = false) String limit,
                               @RequestParam(value = "user", required = false) List<String> userEmails
    ){

        Specification<Post> authorSpecification = postService.collectionContain(userEmails);

        Specification<Post> postSpecification = postTagMergeFilterService.searchInAllFields(passedTagList, Post.class.getDeclaredFields(), searchedText);

        Specification<Post> finalSpecification = Specification
                .where(postSpecification)
                .and(authorSpecification);

        int startPage = start == null ? Integer.parseInt("0"): Integer.parseInt(start);
        int blogCount = limit == null ? Integer.parseInt("6"): Integer.parseInt(limit);

        PageRequest pageRequest = sortBy(startPage,blogCount,sortOrder);
        Page<Post> posts = postService.findAllPublishedPosts(finalSpecification, pageRequest);
        return posts;
    }


    @GetMapping("/posts/{postId}")
    public Post singlePostView(@PathVariable int postId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Post post = postService.findById(postId);
        return post;
    }

    @PostMapping("/createPost")
    @ExceptionHandler
    public String savePost(@RequestBody JsonParser jsonParser){

        if(!authenticationHandler.authenticate(jsonParser.getEmail(), jsonParser.getPassword())){
            throw new UsernameNotFoundException("Invalid Email or Password");
        }

        User user = userService.findByEmail(jsonParser.getEmail());
        Post post = new Post(jsonParser.getPostTitle(), jsonParser.getExcerpt(), jsonParser.getPostContent(), new Date(), !jsonParser.isPublished(), new Date());
        post.setAuthor(user);
        for(String tag: jsonParser.getTags()){
            Tag tempTag = tagService.findTagByName(tag);
            post.addTag(tempTag);
        }
        postService.save(post);
        return "Post saved sucessfully :)";
    }

    @PutMapping("/updatepost")
    public String editForm(@RequestBody JsonParser jsonParser){
        if(!authenticationHandler.authenticate(jsonParser.getEmail(), jsonParser.getPassword())){
            throw new UsernameNotFoundException("Invalid Email or Password");
        }

        Post post = postService.editPost(jsonParser.getPostTitle(), jsonParser.getPostContent(), jsonParser.getPostId());
        if(!post.getAuthor().getEmail().equals(jsonParser.getEmail())){
            throw new RuntimeException("Access denied to this post");
        }

        for(String tag: jsonParser.getTags()){
            Tag tagName = tagService.findTagByName(tag);
            post.addTag(tagName);
        }
        postService.save(post);
        return "Post Edited Successfully";
    }

    @DeleteMapping("/deletePostId")
    public String deletePostById(@RequestBody JsonParser jsonParser){
        if(!authenticationHandler.authenticate(jsonParser.getEmail(), jsonParser.getPassword())){
            throw new UsernameNotFoundException("Invalid Email or Password");
        }

        Post post = postService.findById(jsonParser.getPostId());
        if(!post.getAuthor().getEmail().equals(jsonParser.getEmail())){
            throw new RuntimeException("Access denied to this post");
        }
        postService.deleteById(jsonParser.getPostId());
        return "Deleted Post Sucessfully";
    }

    private PageRequest sortBy(int start, int blogCount, String s){

        if(s == null || s.isEmpty()){
            return PageRequest.of(start,blogCount);
        }
        if(s.equals("date-asc")){
            return PageRequest.of(start,blogCount, Sort.Direction.ASC, "publishTime");
        }else if(s.equals("date-desc")){
            return PageRequest.of(start,blogCount, Sort.Direction.DESC, "publishTime");
        }else if(s.equals("title-asc")){
            return PageRequest.of(start,blogCount, Sort.Direction.ASC, "postTitle");
        }else if(s.equals("title-desc")){
            return PageRequest.of(start,blogCount, Sort.Direction.DESC, "postTitle");
        }else{
            return PageRequest.of(start,blogCount);
        }
    }
}
