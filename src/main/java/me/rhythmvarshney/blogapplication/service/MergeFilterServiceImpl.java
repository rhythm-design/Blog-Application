package me.rhythmvarshney.blogapplication.service;

import me.rhythmvarshney.blogapplication.entity.Post;
import me.rhythmvarshney.blogapplication.entity.Tag;
import org.springframework.stereotype.Service;

@Service
public class MergeFilterServiceImpl implements MergeFilterService<Post, Tag> {
    @Override
    public String returnFieldNameForCollectionSearch() {
        return "name";
    }
}
