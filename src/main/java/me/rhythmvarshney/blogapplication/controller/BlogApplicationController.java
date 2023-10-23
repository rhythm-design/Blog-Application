package me.rhythmvarshney.blogapplication.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.rhythmvarshney.blogapplication.entity.Comment;
import me.rhythmvarshney.blogapplication.entity.Post;
import me.rhythmvarshney.blogapplication.entity.Tag;
import me.rhythmvarshney.blogapplication.repositories.PostRepository;
import me.rhythmvarshney.blogapplication.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class BlogApplicationController {

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostService postService;
    @PutMapping("/fillData")
    public void test() throws IOException, InterruptedException {
        for(int i = 1; i <= 30; i++){
            URL url = new URL("https://api.api-ninjas.com/v1/loremipsum?paragraphs=6");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("x-api-key","k1/P1qg94t/wUz/eCD4QIA==w0Fb9cfeQp0NA8Iu");
            InputStream responseStream = connection.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseStream);
            Post post = new Post();
            post.setPostTitle("Title is Best" + i);
            post.setExcerpt("This is excerpt" + i%5);
            post.setPostContent(root.path("text").asText());
            post.setAuthor("Rhythm");
            post.setPublishTime(new Date());
            post.setPostCreateTime(new Date());
            if(i%5==0){
                post.setPublished(false);
            }else{
                post.setPublished(true);
            }

            Tag tag = new Tag();
            tag.setName("label" + i%5);
            Tag tag1 = new Tag();
            tag1.setName("label" + i%3);
            Tag tag2 = new Tag();
            tag2.setName("label" + i%7);
            post.addTag(tag);
            post.addTag(tag1);
            post.addTag(tag2);
            postRepository.save(post);
            System.out.println("Saved Post");
        }
    }


    @GetMapping("/sample2")
    public String test1(){
        return "login-page";
    }
}