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

    private MergeFilterService<Post,Tag> mergeFilterService;

    @Autowired
    public PostController(PostService postService, TagService tagService, MergeFilterService<Post,Tag> mergeFilterService, UserService userService) {
        this.postService = postService;
        this.tagService = tagService;
        this.mergeFilterService = mergeFilterService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String homepage(Model model,
                            @RequestParam(value = "tag", required = false) List<String> passedTagList,
                            @RequestParam(value = "author", required = false) List<String> authorList,
                            @RequestParam(value = "order", required = false) String sortOrder,
                            @RequestParam(value = "search", required = false) String searchedText,
                            @RequestParam(value = "start", required = false) String start,
                            @RequestParam(value = "limit", required = false) String limit,
                           @RequestParam(value = "user", required = false) List<String> userEmails,
                           HttpServletRequest request
    ){
        List<User> users = userService.findAll();
        System.out.println(userEmails);
        List<Tag> availableTags = tagService.findAll();
        List<String> availableAuthorList = postService.getAllAuthors();

        String passedUrl = request.getQueryString();

        String url = request.getQueryString() != null ?
                passedUrl.replaceAll("&start=\\d+", "").replaceAll("\\&+", "&")
                        .replaceAll("&limit=\\d+", "").replaceAll("\\&+", "&")
                :"";

        if(url != null){
            String updatedUrl = url.replaceAll("&start=\\d+", "").replaceAll("\\&+", "&");
//            String updatedUrl = url.replaceAll("start=\\d+", "");
//            System.out.println("Start replacer: " + updatedUrl);
//            System.out.println("Start Replacer: " + url.replaceAll("start.?&",""));
        }else{
            url = "";
        }

//        String url=request.getQueryString()!=null?
//                "&"+request.getQueryString().replaceAll("limit.?&","").replaceAll("start.?&",""):
//                "";

        Specification<Post> postSpecification = mergeFilterService.searchInAllFields(
                passedTagList,
                Post.class.getDeclaredFields(),
                searchedText,
                "",
                ""
        );


        int startPage = start == null ? Integer.parseInt("0"): Integer.parseInt(start);
        int blogCount = limit == null ? Integer.parseInt("6"): Integer.parseInt(limit);
        PageRequest pageRequest = PageRequest.of(startPage,blogCount);

        Page<Post> posts = postService.findAll(postSpecification,pageRequest);
        model.addAttribute("posts_list", posts.getContent());
        model.addAttribute("tags_list",availableTags);
        model.addAttribute("authors_list",availableAuthorList);
        model.addAttribute("previous_page",posts.getNumber() - 1);
        model.addAttribute("next_page", posts.getNumber() + 1);
        model.addAttribute("total_pages",posts.getTotalPages());
        model.addAttribute("limit",posts.getSize());
        model.addAttribute("user_list", users);

        model.addAttribute("url_string", url);
        return "homepage";
    }



//    @GetMapping("/")
//    public String homepage(Model model,
//                           @RequestParam(value = "tag", required = false) List<String> passedTagList,
//                           @RequestParam(value = "author", required = false) List<String> authorList,
//                           @RequestParam(value = "order", required = false) String sortOrder,
//                           @RequestParam(value = "search", required = false) String searchedText,
//                           @RequestParam(value = "start", required = false) String start,
//                           @RequestParam(value = "limit", required = false) String limit,
//                           HttpServletRequest request
//    ){
//        List<User> users = userService.findAll();
//        List<Tag> tags = tagService.findAll();
////        Specification<Post> postSpecification = mergeFilterService.searchInAllFields()
//        return "homepage";
//    }



    @GetMapping("/createPost")
    public String createPost(){
        return "post-form";
    }
    @PostMapping("/createPost")
    public String createPost(@RequestParam("title") String title,
                             @RequestParam("excerpt") String excerpt,
                             @RequestParam("content") String content,
                             @RequestParam("isPublished") boolean isPublished,
                             @RequestParam("tags") String tags
                             ){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // get the email name of the User
        User user = userService.findByEmail(email);
        Post post = new Post(title, excerpt, content, "rhythm", new Date(), isPublished, new Date());
        post.setUser(user);
        String[] tagList = tags.split(",");
        for(String tag: tagList){
            Tag tempTag = tagService.findTagByName(tag);
            post.addTag(tempTag);
        }
        postService.save(post);
//        tagService.test("java");
        return "redirect:/";
    }


    @GetMapping("/post/{postId}")
    public String singlePostView(@PathVariable int postId, Model model){
        Post post = postService.findById(postId);
        model.addAttribute("single_post",post);
        model.addAttribute("post_comments",post.getComments());
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

        return "redirect:/" + postId;
    }

    @GetMapping("/delete/{postId}")
    public String deletePostById(@PathVariable int postId){
        postService.deleteById(postId);
        return "redirect:/";
    }



    @PostMapping("/addcomment/{postId}")
    public String addCommentByPostId(@PathVariable int postId,
                                     @RequestParam("user_name") String name,
                                     @RequestParam("user_email") String email,
                                     @RequestParam("user_comment") String comment
    ){
        // Find post by postId
        Post post = postService.findById(postId);

        //Construct Comment
        Comment comments = new Comment(name,email, comment);

        // Save the comment to post
        post.addComment(comments);

        // Save the post again in DB
        postService.save(post);

        //redirect to post again
        return "redirect:/" + postId;
    }

}
