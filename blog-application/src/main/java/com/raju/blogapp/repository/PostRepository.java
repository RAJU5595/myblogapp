package com.raju.blogapp.repository;

import com.raju.blogapp.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Integer> {
    @Query("select post from Post post where lower(post.title) LIKE CONCAT('%',:searchField ,'%')" +
            "OR lower(post.content) LIKE CONCAT('%',:searchField ,'%')" +
            "OR lower(post.author) LIKE CONCAT('%',:searchField ,'%')")
    Page <Post> findPostsWithSearch(Pageable pageable,String searchField);

    @Query(value = "select * from posts where author=?1 AND (content ILIKE %?2% OR title ILIKE %?2%)",nativeQuery = true)
    List<Post> findAllAuthorPosts(String authorName,String searchField);
}
