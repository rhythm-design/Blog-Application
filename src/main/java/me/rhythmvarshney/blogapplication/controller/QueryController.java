package me.rhythmvarshney.blogapplication.controller;

import me.rhythmvarshney.blogapplication.entity.Post;
import me.rhythmvarshney.blogapplication.entity.Tag;
import me.rhythmvarshney.blogapplication.service.FilteringService;
import me.rhythmvarshney.blogapplication.service.MergeFilterService;
import me.rhythmvarshney.blogapplication.service.PostService;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
public class QueryController {

    private MergeFilterService<Post,Tag> mergeFilterService;

    @Autowired
    public QueryController(MergeFilterService<Post,Tag> mergeFilterService) {
        this.mergeFilterService = mergeFilterService;
    }


    @GetMapping("/filter")
    public String filtering(@RequestParam Map<String,String> map, Model model, @RequestParam(required = false) String tags){
        Post t = new Post();
//        Class c =t.getClass();
//        Pair<>
//        System.out.println(Arrays.toString(c.getDeclaredFields()));
//        Post post = new Post();
//        System.out.println(post.getDe);
        filteringService.setT(t);
        Set<String> t1 = new HashSet<>();
        Specification<Post> postSpecification = mergeFilterService
                .searchInAllFields("",t.getClass().getDeclaredFields(),"representing","","");
//        Specification<Post> postSpecification = filteringService.allThis("Rhythm", t1,"");
        PageRequest pageRequest = PageRequest.of(0,6);
        Page<Post> post = postService.findAll(postSpecification, pageRequest);
//        for(Post post1: post.getContent()){
//            for(Tag tji: post1.getTags()){
//                System.out.println(tji.toString());
//            }
//        }
//        System.out.println("Content: " + post1.getContent());
        model.addAttribute("posts_list",post.getContent());
        return "sample";
    }
}
