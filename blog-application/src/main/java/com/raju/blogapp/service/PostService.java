package com.raju.blogapp.service;

import com.raju.blogapp.entity.Tag;
import com.raju.blogapp.repository.PostRepository;
import com.raju.blogapp.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagService tagService;

    public List<Post> findAllPosts(){
        return postRepository.findAll();
    }

    public void savePost(Post post){
        postRepository.save(post);
    }

    public Post getPost(int id){
        Optional<Post> result = postRepository.findById(id);
        Post post;
        if(result.isPresent()){
            post = result.get();
        }
        else{
            throw new RuntimeException("Did not find post with :"+id);
        }
        return post;
    }
    public Date formatDate(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        try {
            date = formatter.parse(dateString);
            return date;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Page<Post> findPostsWithPagination(int pageNumber,int pageSize,String sortField,String sortOrder,String searchField){
        Sort sort = sortOrder.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Page<Post> posts = postRepository.findPostsWithSearch(PageRequest.of(pageNumber,pageSize,sort),searchField);
        return posts;
    }

    public void deleteThePost(Integer postId){
        postRepository.deleteById(postId);
    }

    public List<Post> findAllAuthorPosts(String authorName,String searchField){
        return postRepository.findAllAuthorPosts(authorName,searchField);
    }
}
