package ru.kursach.MedProject.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kursach.MedProject.enums.Roles;
import ru.kursach.MedProject.enums.Specialization;
import ru.kursach.MedProject.models.User;
import ru.kursach.MedProject.repositories.UserRepository;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        return userRepository.findByEmail(name);
    }

    public boolean confirmPassword(User user,String confirm){
        return confirm == user.getPassword();
    }

    @Transactional
    public Set<User> getAllDoctors(){
        return userRepository.findByRole(Roles.ROLE_DOCTOR);
    }

    public Map<Specialization, Set<User>> getDoctorsGroupedBySpecialization(){
        Set<User> doctors = userRepository.findByRole(Roles.ROLE_DOCTOR);
        return doctors.stream().filter(user -> user.getSpecialization()!=null).collect(Collectors.groupingBy(User::getSpecialization, Collectors.toSet()));
    }


    @Transactional
    public User getUserById(int id){
        return userRepository.findUserById(id);
    }



}
