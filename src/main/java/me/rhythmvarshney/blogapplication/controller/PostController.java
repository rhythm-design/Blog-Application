package me.rhythmvarshney.blogapplication.controller;

import me.rhythmvarshney.blogapplication.entity.Comment;
import me.rhythmvarshney.blogapplication.entity.Post;
import me.rhythmvarshney.blogapplication.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/posts")
public class PostController {

    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{postId}")
    public String singlePostView(@PathVariable int postId, Model model){
        Post post = postService.findById(postId);
        model.addAttribute("single_post",post);
        model.addAttribute("post_comments",post.getComments());
        return "single-post";
    }

    @GetMapping("/delete/{postId}")
    public String deletePostById(@PathVariable int postId){
        postService.deleteById(postId);
        return "redirect:/";
    }

    @GetMapping("/edit/{postId}")
    public String editPostById(@PathVariable int postId){
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
