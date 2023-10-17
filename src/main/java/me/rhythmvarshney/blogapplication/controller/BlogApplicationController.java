package me.rhythmvarshney.blogapplication.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.rhythmvarshney.blogapplication.entity.Post;
import me.rhythmvarshney.blogapplication.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

@Controller
public class BlogApplicationController {

    @Autowired
    PostRepository postRepository;

    @PutMapping("/fillData")
    public void test() throws IOException {
        for(int i = 1; i <= 10; i++){
            URL url = new URL("https://api.api-ninjas.com/v1/loremipsum?paragraphs=8");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("x-api-key","k1/P1qg94t/wUz/eCD4QIA==w0Fb9cfeQp0NA8Iu");
            InputStream responseStream = connection.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseStream);
            Post post = new Post("title " + i,"excerpt " + i, root.path("text").asText(),"author" + i,new Date(),true,new Date(),new Date());
            postRepository.save(post);
            System.out.println("Saved Post");
        }
    }

    @GetMapping("/")
    public String homepage(Model model){
        model.addAttribute("posts_list", postRepository.findAll());
        return "homepage";
    }
}
