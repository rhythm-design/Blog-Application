package me.rhythmvarshney.blogapplication.controller;

import jakarta.servlet.http.HttpServletRequest;
import me.rhythmvarshney.blogapplication.entity.Comment;
import me.rhythmvarshney.blogapplication.entity.Post;
import me.rhythmvarshney.blogapplication.entity.Tag;
import me.rhythmvarshney.blogapplication.entity.User;
import me.rhythmvarshney.blogapplication.service.MergeFilterService;
import me.rhythmvarshney.blogapplication.service.PostService;
import me.rhythmvarshney.blogapplication.service.TagService;
import me.rhythmvarshney.blogapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
public class PostController {

    private PostService postService;

    private TagService tagService;

    private UserService userService;

    private MergeFilterService<Post,Tag> postTagMergeFilterService;

    private MergeFilterService<User,Post> userPostMergeFilterService;

    @Autowired
    public PostController(PostService postService, TagService tagService, MergeFilterService<Post,Tag> postTagMergeFilterService, UserService userService, MergeFilterService<User,Post> userPostMergeFilterService) {
        this.postService = postService;
        this.tagService = tagService;
        this.postTagMergeFilterService = postTagMergeFilterService;
        this.userService = userService;
        this.userPostMergeFilterService = userPostMergeFilterService;
    }

    @GetMapping("/")
    public String homepage(Model model,
                            @RequestParam(value = "tag", required = false) List<String> passedTagList,
                            @RequestParam(value = "order", required = false) String sortOrder,
                            @RequestParam(value = "search", required = false) String searchedText,
                            @RequestParam(value = "start", required = false) String start,
                            @RequestParam(value = "limit", required = false) String limit,
                           @RequestParam(value = "user", required = false) List<String> userEmails,
                           HttpServletRequest request
    ){

        String passedUrl = request.getQueryString();

        String url = request.getQueryString() != null ?
                passedUrl.replaceAll("&start=\\d+", "").replaceAll("\\&+", "&")
                        .replaceAll("&limit=\\d+", "").replaceAll("\\&+", "&")
                :"";

        List<User> users = userService.findAll();
        List<Tag> availableTags = tagService.findAll();

        Specification<Post> authorSpecification = postService.collectionContain(userEmails);

        Specification<Post> postSpecification = postTagMergeFilterService.searchInAllFields(passedTagList, Post.class.getDeclaredFields(), searchedText);

        Specification<Post> finalSpecification = Specification
                                                    .where(postSpecification)
                                                    .and(authorSpecification);

        int startPage = start == null ? Integer.parseInt("0"): Integer.parseInt(start);
        int blogCount = limit == null ? Integer.parseInt("6"): Integer.parseInt(limit);

        PageRequest pageRequest = sortBy(startPage,blogCount,sortOrder);
        Page<Post> posts = postService.findAllPublishedPosts(finalSpecification, pageRequest);

        model.addAttribute("posts_list", posts.getContent());
        model.addAttribute("tags_list",availableTags);
        model.addAttribute("previous_page",posts.getNumber() - 1);
        model.addAttribute("next_page", posts.getNumber() + 1);
        model.addAttribute("total_pages",posts.getTotalPages());
        model.addAttribute("limit",posts.getSize());
        model.addAttribute("user_list", users);
        model.addAttribute("url_string", url);
        return "homepage";
    }

    @GetMapping("/createPost")
    public String createPost(){
        return "post-form";
    }
    @PostMapping("/createPost")
    public String savePost(@RequestParam("title") String title,
                             @RequestParam("excerpt") String excerpt,
                             @RequestParam("content") String content,
                             @RequestParam("tags") String tags,
                             @RequestParam(value = "isDraft", required = false) String isDraft
                             ){

        System.out.println("Status is: " + isDraft);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // get the email name of the User
        User user = userService.findByEmail(email);
        Post post = new Post(title, excerpt, content, new Date(), isDraft == null, new Date());
        post.setAuthor(user);
        String[] tagList = tags.split(",");
        for(String tag: tagList){
            Tag tempTag = tagService.findTagByName(tag);
            post.addTag(tempTag);
        }
        postService.save(post);
        return "redirect:/";
    }


    @GetMapping("/post/{postId}")
    public String singlePostView(@PathVariable int postId, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        System.out.println("vbnj " + authentication.getName());
        Post post = postService.findById(postId);
        model.addAttribute("single_post",post);
        model.addAttribute("post_comments",post.getComments());
        model.addAttribute("current_logged_in_user", authentication.getName());
        return "single-post";
    }

    @GetMapping("/edit/{postId}")
    public String editForm(@PathVariable int postId, Model model){
        Post post = postService.findById(postId);
        model.addAttribute("post_data",post);
        return "edit-post";
    }

    @PostMapping("/edit")
    public String editPostById(
            @RequestParam("title") String title,
            @RequestParam("tags") String tags,
            @RequestParam("content") String content,
            @RequestParam("postId") int postId
    ){
        Post post = postService.editPost(title,content,postId);
        String[] tagList = tags.split(",");
        for(String tag: tagList){
            Tag tagName = tagService.findTagByName(tag);
            post.addTag(tagName);
        }
        postService.save(post);

        return "redirect:/post/" + postId;
    }

    @GetMapping("/delete/{postId}")
    public String deletePostById(@PathVariable int postId){
        postService.deleteById(postId);
        return "redirect:/";
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

    @GetMapping("/myposts")
    public String myposts(Model model,
                          @RequestParam(value = "tag", required = false) List<String> passedTagList,
                          @RequestParam(value = "order", required = false) String sortOrder,
                          @RequestParam(value = "search", required = false) String searchedText,
                          @RequestParam(value = "start", required = false) String start,
                          @RequestParam(value = "limit", required = false) String limit,
                          @RequestParam(value = "user", required = false) List<String> userEmails,
                          HttpServletRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String passedUrl = request.getQueryString();

        String url = request.getQueryString() != null ?
                passedUrl.replaceAll("&start=\\d+", "").replaceAll("\\&+", "&")
                        .replaceAll("&limit=\\d+", "").replaceAll("\\&+", "&")
                :"";

        List<User> users = userService.findAll();
        List<Tag> availableTags = tagService.findAll();

        Specification<Post> authorSpecification = postService.collectionContain(userEmails);

        Specification<Post> postSpecification = postTagMergeFilterService.searchInAllFields(passedTagList, Post.class.getDeclaredFields(), searchedText);

        Specification<Post> finalSpecification = Specification
                .where(postSpecification)
                .and(authorSpecification);

        int startPage = start == null ? Integer.parseInt("0"): Integer.parseInt(start);
        int blogCount = limit == null ? Integer.parseInt("6"): Integer.parseInt(limit);

        PageRequest pageRequest = sortBy(startPage,blogCount,sortOrder);
        Page<Post> posts = postService.findAllPostsByAuthorEmail(authentication.getName(), pageRequest);

        model.addAttribute("posts_list", posts.getContent());
        model.addAttribute("tags_list",availableTags);
        model.addAttribute("previous_page",posts.getNumber() - 1);
        model.addAttribute("next_page", posts.getNumber() + 1);
        model.addAttribute("total_pages",posts.getTotalPages());
        model.addAttribute("limit",posts.getSize());
        model.addAttribute("user_list", users);
        model.addAttribute("url_string", url);
        return "homepage";
    }

    @GetMapping("/mydrafts")
    public String mydrafts(Model model,
                          @RequestParam(value = "tag", required = false) List<String> passedTagList,
                          @RequestParam(value = "order", required = false) String sortOrder,
                          @RequestParam(value = "search", required = false) String searchedText,
                          @RequestParam(value = "start", required = false) String start,
                          @RequestParam(value = "limit", required = false) String limit,
                          @RequestParam(value = "user", required = false) List<String> userEmails,
                          HttpServletRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String passedUrl = request.getQueryString();

        String url = request.getQueryString() != null ?
                passedUrl.replaceAll("&start=\\d+", "").replaceAll("\\&+", "&")
                        .replaceAll("&limit=\\d+", "").replaceAll("\\&+", "&")
                :"";

        List<User> users = userService.findAll();
        List<Tag> availableTags = tagService.findAll();

        Specification<Post> authorSpecification = postService.collectionContain(userEmails);

        Specification<Post> postSpecification = postTagMergeFilterService.searchInAllFields(passedTagList, Post.class.getDeclaredFields(), searchedText);

        Specification<Post> finalSpecification = Specification
                .where(postSpecification)
                .and(authorSpecification);

        int startPage = start == null ? Integer.parseInt("0"): Integer.parseInt(start);
        int blogCount = limit == null ? Integer.parseInt("6"): Integer.parseInt(limit);

        PageRequest pageRequest = sortBy(startPage,blogCount,sortOrder);
        Page<Post> posts = postService.findAllDraftPostsByEmail(authentication.getName(), pageRequest);

        model.addAttribute("posts_list", posts.getContent());
        model.addAttribute("tags_list",availableTags);
        model.addAttribute("previous_page",posts.getNumber() - 1);
        model.addAttribute("next_page", posts.getNumber() + 1);
        model.addAttribute("total_pages",posts.getTotalPages());
        model.addAttribute("limit",posts.getSize());
        model.addAttribute("user_list", users);
        model.addAttribute("url_string", url);
        return "homepage";
    }

}
