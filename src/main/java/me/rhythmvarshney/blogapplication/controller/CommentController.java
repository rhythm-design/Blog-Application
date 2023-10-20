package me.rhythmvarshney.blogapplication.controller;

import me.rhythmvarshney.blogapplication.entity.Comment;
import me.rhythmvarshney.blogapplication.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comment")
public class CommentController {

    private CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
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
        return "redirect:/" + postId;
    }

    @GetMapping("/delete-comment/{commentId}/{postId}")
    public String deleteComment(@PathVariable int commentId, @PathVariable int postId){
        commentService.deleteById(commentId);
        return "redirect:/" + postId;
    }

}
