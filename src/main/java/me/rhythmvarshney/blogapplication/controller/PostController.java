package me.rhythmvarshney.blogapplication.controller;

import me.rhythmvarshney.blogapplication.entity.Comment;
import me.rhythmvarshney.blogapplication.entity.Post;
import me.rhythmvarshney.blogapplication.entity.Tag;
import me.rhythmvarshney.blogapplication.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;


@Controller
@RequestMapping("/posts")
public class PostController {

    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

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
        String[] tagsList = tags.split(",");
        Post post = new Post(title, excerpt, content, "rhythm", new Date(), isPublished, new Date());
        System.out.println("tags: " +tags);
        Tag tag = new Tag();
        tag.setName(tagsList[0]);
        Tag tag1= new Tag();
        tag1.setName(tagsList[1]);
        post.addTag(tag);
        post.addTag(tag1);
        postService.save(post);
        return "redirect:/";
    }


    @GetMapping("/{postId}")
    public String singlePostView(@PathVariable int postId, Model model){
        Post post = postService.findById(postId);
        model.addAttribute("single_post",post);
        model.addAttribute("post_comments",post.getComments());
        return "single-post";
    }

    @GetMapping("/edit/{postId}")
    public String editForm(@PathVariable int postId, Model model){
//        postService.deleteById(postId);
        Post post = postService.findById(postId);
        model.addAttribute("post_data",post);
        return "edit-post";
    }

    @PostMapping("/edit")
    public String editPostById(
            @RequestParam("title") String title,
            @RequestParam("excerpt") String excerpt,
            @RequestParam("content") String content,
            @RequestParam("postId") int postId
    ){
        Post post = postService.findById(postId);
        post.setPostTitle(title);
        post.setExcerpt(excerpt);
        post.setPostContent(content);

        postService.save(post);

        return "redirect:/posts/" + postId;
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
        return "redirect:/posts/" + postId;
    }

}
