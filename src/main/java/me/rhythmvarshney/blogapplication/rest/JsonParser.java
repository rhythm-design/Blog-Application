package me.rhythmvarshney.blogapplication.rest;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class JsonParser {

    private String email;

    private String password;

    private String postTitle;

    private String postContent;

    private String excerpt;

    private Set<String> tags;

    private boolean isPublished;

    private int postId;
}


