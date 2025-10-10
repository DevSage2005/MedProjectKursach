package ru.kursach.MedProject.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kursach.MedProject.models.PesonDetails;
import ru.kursach.MedProject.models.User;
import ru.kursach.MedProject.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserDetailsService(UserRepository userRepository){
        this.userRepository=userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user= userRepository.findByEmail(username);
                if(user.isEmpty()){
            throw new UsernameNotFoundException("User not found!");
        }

        user.get().setLastLoginAt(LocalDateTime.now());
        userRepository.save(user.get());
        return new PesonDetails(user.get());
    }

}
