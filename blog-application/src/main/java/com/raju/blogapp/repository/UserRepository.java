package com.raju.blogapp.repository;

import com.raju.blogapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByName(String name);

    @Query("select user from User user where user.role = 'USER'")
    List<User> findAllUsers();
}
