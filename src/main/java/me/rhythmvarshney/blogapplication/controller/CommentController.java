package me.rhythmvarshney.blogapplication.controller;

import me.rhythmvarshney.blogapplication.entity.Comment;
import me.rhythmvarshney.blogapplication.entity.Post;
import me.rhythmvarshney.blogapplication.service.CommentService;
import me.rhythmvarshney.blogapplication.service.PostService;
import me.rhythmvarshney.blogapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comment")
public class CommentController {

    private CommentService commentService;

    private PostService postService;

    private UserService userService;

    @Autowired
    public CommentController(CommentService commentService, PostService postService, UserService userService) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping("/update-comment/{commentId}/{postId}")
    public String editComment(Model model, @PathVariable int commentId, @PathVariable int postId){
        model.addAttribute("comment_data", commentService.findById(commentId));
        model.addAttribute("comment_post_id", postId);

        return "edit-comment";
    }

    @PostMapping("/update-comment/{postId}")
    public String updateComment(@PathVariable int postId,
                                @RequestParam("commentId") int commentId,
                                @RequestParam("updatedComment") String comment
                                ){
        Comment oldComment = commentService.findById(commentId);
        oldComment.setComment(comment);
        commentService.save(oldComment);
        return "redirect:/post/" + postId;
    }

    @GetMapping("/delete-comment/{commentId}/{postId}")
    public String deleteComment(@PathVariable int commentId, @PathVariable int postId){
        commentService.deleteById(commentId);
        return "redirect:/post/" + postId;
    }

    @PostMapping("/addcomment/{postId}")
    public String addCommentByPostId(@PathVariable int postId, @RequestParam("user_comment") String comment){

        Post post = postService.findById(postId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        String name = userService.findByEmail(email).getName();
        Comment comments = new Comment(name,email, comment);
        post.addComment(comments);

        postService.save(post);
        return "redirect:/post/" + postId;
    }

}
