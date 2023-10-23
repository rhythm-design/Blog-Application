package me.rhythmvarshney.blogapplication.service;

import me.rhythmvarshney.blogapplication.entity.Post;
import me.rhythmvarshney.blogapplication.entity.Tag;
import me.rhythmvarshney.blogapplication.entity.User;
import org.springframework.stereotype.Service;


@Service
public class UserAuthorFilterImpl implements MergeFilterService<User, Post>{

    @Override
    public String returnFieldNameForCollectionSearch() {
        return null;
    }
}
