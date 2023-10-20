package me.rhythmvarshney.blogapplication.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import me.rhythmvarshney.blogapplication.entity.Comment;
import me.rhythmvarshney.blogapplication.entity.Post;
import me.rhythmvarshney.blogapplication.entity.Tag;
import me.rhythmvarshney.blogapplication.repositories.PostRepository;
import me.rhythmvarshney.blogapplication.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
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
        Faker faker = new Faker();

        for(int i = 1; i <=30; i++){
            Post post = new Post();
            post.setPostTitle(faker.lorem().sentence(1));
            post.setExcerpt(faker.lorem().sentence(20));
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            post.setPostContent(faker.lorem().paragraph(120));
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            post.setAuthor(faker.name().fullName());
            post.setPublishTime(faker.date().between(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(365)), new Date()));
            post.setPostCreateTime(faker.date().between(new Date(1695081600L), new Date()));
            if(i%3==0){
                post.setPublished(false);
            }else{
                post.setPublished(true);
            }
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            Tag tag = new Tag();
            String tagName = faker.lorem().fixedString(5);
            tag.setName(faker.lorem().fixedString(5));
            System.out.println("Tag accumulated is: " + tagName);
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            Tag tag1 = new Tag();
            tag1.setName(faker.lorem().fixedString(5));
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            Tag tag2 = new Tag();
            tag2.setName(faker.lorem().fixedString(5));
            post.addTag(tag);
            post.addTag(tag1);
            post.addTag(tag2);
            postRepository.save(post);
        }
    }


    @GetMapping("/sample")
    public String test1(Model model){
        Post post = postRepository.findById(18).get();
        model.addAttribute("single_post", post);
        model.addAttribute("post_comments",post.getComments());
        return "sample";
    }
}