package me.rhythmvarshney.blogapplication.controller;

import me.rhythmvarshney.blogapplication.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class QueryController {

    private PostService postService;

    @Autowired
    public QueryController(PostService postService) {
        this.postService = postService;
    }


}
