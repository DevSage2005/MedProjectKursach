package ru.kursach.MedProject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kursach.MedProject.enums.Roles;
import ru.kursach.MedProject.models.User;
import ru.kursach.MedProject.repositories.UserRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@Service
public class AdminService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final User user;

    @Autowired
    public AdminService(UserRepository userRepository, UserService userService, User user) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.user = user;
    }

    public Set<User> getUsers(){
        return new HashSet<>(userRepository.findAll());
    }

    public void setRoleForUser(int id, String role){
        User user = userService.getUserById(id);
        Roles newRole = Roles.valueOf(role);
        user.addRole(newRole);
        userRepository.save(user);
    }

    public void deleteRoleFromUser(int id, String deleteRole) {
        User user = userService.getUserById(id);
        Roles role = Roles.valueOf(deleteRole);
        user.deleteRole(role);
        userRepository.save(user);
    }
}
