package com.raju.blogapp.securityconfiguration;

import com.raju.blogapp.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration{

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    private static final String[] WHITE_LIST_URLS = {
            "/","/login","/register","/registerUser","/post{Id}","/post{Id}/addComment","/page/{pageNumber}/filters"
            ,"/page/{pageNumber}"
    };

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());

        AuthenticationManager authenticationManager
                = authenticationManagerBuilder. build();
        http
                .authenticationManager(authenticationManager)
                .authorizeHttpRequests()
                .antMatchers(WHITE_LIST_URLS).permitAll()
//                .anyRequest().authenticated()
                .and()
                .formLogin()
                .defaultSuccessUrl("/");

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
}

