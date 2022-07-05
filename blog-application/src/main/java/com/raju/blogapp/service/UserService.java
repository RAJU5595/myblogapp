package com.raju.blogapp.service;

import com.raju.blogapp.entity.User;
import com.raju.blogapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void saveUser(User user){
        userRepository.save(user);
    }

    public Optional<User> findUser(String username){
        Optional<User> user = userRepository.findByName(username);
        return user;
    }

    public List<User> findAllUsers(){
        return userRepository.findAllUsers();
    }

    public Optional<User> findUserById(Integer id){
        return userRepository.findById(id);
    }
}
