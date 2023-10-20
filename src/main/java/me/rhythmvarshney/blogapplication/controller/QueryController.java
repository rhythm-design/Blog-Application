package me.rhythmvarshney.blogapplication.controller;

import me.rhythmvarshney.blogapplication.entity.Post;
import me.rhythmvarshney.blogapplication.service.FilteringService;
import me.rhythmvarshney.blogapplication.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class QueryController {

    private PostService postService;

    private FilteringService<Post> filteringService;

    @Autowired
    public QueryController(PostService postService, FilteringService<Post> filteringService) {
        this.postService = postService;
        this.filteringService = filteringService;
    }


    @GetMapping("/filter")
    public String filtering(@RequestParam Map<String,String> map, Model model, @RequestParam(required = false) String tags){

        Specification<Post> postSpecification = filteringService.searchByMultipleCriteria(map);
        PageRequest pageRequest = PageRequest.of(0,6);
        Page<Post> post = postService.findAll(postSpecification, pageRequest);
//        System.out.println("Content: " + post1.getContent());
        model.addAttribute("posts_list",post.getContent());
        return "homepage";
    }
}
