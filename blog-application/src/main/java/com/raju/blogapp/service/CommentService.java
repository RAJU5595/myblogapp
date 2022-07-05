package com.raju.blogapp.service;

import com.raju.blogapp.entity.Comment;
import com.raju.blogapp.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public void saveComment(Comment comment){
        commentRepository.save(comment);
    }

    public Comment getComment(Integer id){
        Optional<Comment> result = commentRepository.findById(id);
        Comment comment;
        if(result.isPresent()){
            comment = result.get();
        }
        else{
            throw new RuntimeException("Did not find comment with :"+id);
        }
        return comment;
    }

    public List getComments(){
        List <Comment> comments = commentRepository.findAll();
        return comments;
    }

    public void deleteComment(int id){
        commentRepository.deleteById(id);
    }
}
