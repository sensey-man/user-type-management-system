package com.example.utm.service.db;

import com.example.utm.dto.dao.Passwords;
import com.example.utm.dto.dao.User;
import com.example.utm.dto.enums.UserType;
import com.example.utm.repository.PasswordRepository;
import com.example.utm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class DbService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRepository passwordRepository;

    @Transactional
    public List<User> list() {
        return userRepository.list();
    }

    @Transactional
    public Boolean addUser(User user) {
        var p = new Passwords();
        p.setUserId(user.getId());

        return addUserWithPassword(user, p);
    }

    @Transactional
    public Boolean addUserWithPassword(User user, Passwords p) {
        var okUser = userRepository.insert(user);
        var okPass = passwordRepository.insert(p);

        return okUser && okPass;
    }

    @Transactional
    public User getUserById(UUID id) {
        return userRepository.getUserById(id);
    }

    @Transactional
    public Passwords getUserPasswords(UUID id) {
        return passwordRepository.getUserPasswords(id);
    }

    @Transactional
    public Boolean changeUserType(UUID userId, UserType type, Boolean enable, Passwords passwords) {
        var okUser = userRepository.changeUserType(userId, type, enable);
        var okPass = passwordRepository.update(passwords);
        return okPass > 0 && okUser > 0;
    }


}