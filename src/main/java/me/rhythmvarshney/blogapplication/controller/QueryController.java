package me.rhythmvarshney.blogapplication.controller;

import jakarta.servlet.http.HttpServletRequest;
import me.rhythmvarshney.blogapplication.entity.Post;
import me.rhythmvarshney.blogapplication.entity.Tag;
import me.rhythmvarshney.blogapplication.service.MergeFilterService;
import me.rhythmvarshney.blogapplication.service.PostService;
import me.rhythmvarshney.blogapplication.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class QueryController {

    @Autowired
    private PostService postService;

    @Autowired
    private TagService tagService;
    private MergeFilterService<Post,Tag> mergeFilterService;

    @Autowired
    public QueryController(MergeFilterService<Post,Tag> mergeFilterService) {
        this.mergeFilterService = mergeFilterService;
    }


    @GetMapping("/filter")
    public String filtering(Model model,
                            @RequestParam(value = "tag", required = false) List<String> passedTagList,
                            @RequestParam(value = "author", required = false) List<String> authorList,
                            @RequestParam(value = "order", required = false) String sortOrder,
                            @RequestParam(value = "search", required = false) String searchedText,
                            @RequestParam(value = "start", required = false) String start,
                            @RequestParam(value = "limit", required = false) String limit,
                            HttpServletRequest request
                            ){

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
            System.out.println("Start replacer: " + updatedUrl);
            System.out.println("Start Replacer: " + url.replaceAll("start.?&",""));
        }else{
            url = "";
        }

//        String url=request.getQueryString()!=null?
//                "&"+request.getQueryString().replaceAll("limit.?&","").replaceAll("start.?&",""):
//                "";

        Specification<Post> postSpecification = mergeFilterService.searchInAllFields(
                passedTagList,
                Post.class.getDeclaredFields(),
                searchedText
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

        model.addAttribute("url_string", url);
//        System.out.println("Selected Cars: " + selectedCars);
//        Post t = new Post();
//        Set<String> t1 = new HashSet<>();
//        Specification<Post> postSpecification = mergeFilterService
//                .searchInAllFields(selectedCars,t.getClass().getDeclaredFields(),"","","");
//        PageRequest pageRequest = PageRequest.of(0,6);
//        Page<Post> post = postService.findAll(postSpecification, pageRequest);
//        model.addAttribute("posts_list",post.getContent());
//        return "sample";
        return "sample";
    }
}
