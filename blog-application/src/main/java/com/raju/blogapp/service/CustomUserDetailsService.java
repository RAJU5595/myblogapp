package com.raju.blogapp.service;

import com.raju.blogapp.entity.User;
import com.raju.blogapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> validUser = userRepository.findByName(username);
        if(validUser.isEmpty()){
            throw new UsernameNotFoundException("User doesn't exist");
        }
        User user = validUser.get();
        return new org.springframework.security.core.userdetails.User(
                user.getName(),
                user.getPassword(),
                true,
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}
