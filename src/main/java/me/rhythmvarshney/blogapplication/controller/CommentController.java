package me.rhythmvarshney.blogapplication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @GetMapping("/update-comment")
    public String updateComment(){
        return "";
    }

    @GetMapping("/edit-comment")
    public String editComment(){
        return "";
    }

    @GetMapping("/delete-comment")
    public String deleteComment(){
        return "";
    }

}
