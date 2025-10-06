package ru.kursach.MedProject.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kursach.MedProject.models.PesonDetails;
import ru.kursach.MedProject.models.User;
import ru.kursach.MedProject.repositories.UserRepository;

import java.util.Optional;

@Service
public class PersonService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public PersonService(UserRepository userRepository){
        this.userRepository=userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user= userRepository.findByName(username);

        if(user.isEmpty()){
            throw new UsernameNotFoundException("User not found!");
        }
        return new PesonDetails(user.get());
    }
}
