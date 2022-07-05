package com.raju.blogapp.service;

import com.raju.blogapp.entity.Tag;
import com.raju.blogapp.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    public Tag getTag(int id){
        Optional<Tag> result = tagRepository.findById(id);
        Tag tag;
        if(result.isPresent()){
            tag = result.get();
        }
        else{
            throw new RuntimeException("Did not find post with :"+id);
        }
        return tag;
    }

    public List<Tag> findAllTags(){
        return tagRepository.findAll();
    }

    public void saveTag(Tag tag){
        tagRepository.save(tag);
    }
    public Tag getTag(String name){
        return tagRepository.findTagByName(name);
    }
}
