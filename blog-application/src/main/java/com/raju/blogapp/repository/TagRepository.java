package com.raju.blogapp.repository;

import com.raju.blogapp.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag,Integer> {
    Tag findTagByName(String name);
}
