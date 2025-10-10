package ru.kursach.MedProject.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kursach.MedProject.enums.Roles;
import ru.kursach.MedProject.models.User;
import ru.kursach.MedProject.repositories.UserRepository;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService {


    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void save(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.addRole(Roles.ROLE_USER);
        userRepository.save(user);
    }

    @Transactional
    public Optional<User> getUserByName(String name){
        return userRepository.findByName(name);
    }


    @Transactional
    public User getUserById(int id){
        return userRepository.findUserById(id);
    }



}
