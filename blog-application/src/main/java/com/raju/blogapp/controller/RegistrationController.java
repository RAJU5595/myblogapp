package com.raju.blogapp.controller;

import com.raju.blogapp.entity.User;
import com.raju.blogapp.repository.UserRepository;
import com.raju.blogapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
public class RegistrationController {

    @Autowired
    private User user;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        model.addAttribute("user",user);
        return "registration-form";
    }

    @PostMapping("/registerUser")
    public String registerUser(@ModelAttribute("user") User user, Model model){
        User newUser = new User();
        newUser.setName(user.getName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setRole("USER");
        Optional<User> validUser = userRepository.findByName(user.getName());
        if(validUser.isPresent()){
            model.addAttribute("userPresent","User with this username Already Present, Please Try with Other username");
            return "registration-form";
        }
        else {
            userService.saveUser(newUser);
            model.addAttribute("message", "User Created Successfully");
            return "registration-form";
        }
    }
}
