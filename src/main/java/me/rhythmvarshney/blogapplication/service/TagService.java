package me.rhythmvarshney.blogapplication.service;

import me.rhythmvarshney.blogapplication.entity.Tag;
import me.rhythmvarshney.blogapplication.repositories.TagRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TagService {

    private TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag findTagByName(String name){
        Tag tag = tagRepository.findByName(name);

        if(tag == null){
            tag = new Tag();
            tag.setName(name);
            tag.setCreatedAt(new Date());
        }
        tag.setUpdatedAt(new Date());
        return tag;
    }

    public List<Tag> findAll(){
        return tagRepository.findAll();
    }

}
