package com.example.utm.config;

import com.example.utm.repository.PasswordRepository;
import com.example.utm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class InitDbState {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRepository passwordRepository;

    @PostConstruct
    public void init() {
        userRepository.createTable();
        passwordRepository.createTable();
    }

}
