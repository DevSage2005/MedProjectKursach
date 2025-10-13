package ru.kursach.MedProject.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kursach.MedProject.enums.DayOfWeek;
import ru.kursach.MedProject.enums.Roles;
import ru.kursach.MedProject.enums.Specialization;
import ru.kursach.MedProject.models.User;
import ru.kursach.MedProject.models.WorkingHours;
import ru.kursach.MedProject.repositories.UserRepository;

import java.time.LocalTime;
import java.util.*;
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
        user.setRole(Roles.ROLE_USER);
        userRepository.save(user);
    }

    @Transactional
    public Optional<User> getUserByName(String name){
        return userRepository.findByEmail(name);
    }

    public boolean confirmPassword(User user,String confirm){
        return confirm == user.getPassword();
    }


    public List<LocalTime> getSlots(Date date, User doctor){
        List<LocalTime> slots = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        DayOfWeek day = DayOfWeek.values()[calendar.get(Calendar.DAY_OF_WEEK)-1];
        Optional<WorkingHours> wh = doctor.getSchedule().getWorkingHours().stream().filter(doc-> doc.getDayOfWeek()==day).findFirst();

        if(wh.isPresent()){
            LocalTime l = wh.get().getStartTime();
            while(l.isBefore(wh.get().getEndTime())){
                if(l.equals(wh.get().getBreakStart()))
                {
                    l=wh.get().getBreakEnd();
                }
                slots.add(l);
                l = l.plusMinutes(doctor.getSchedule().getSlotDuration());
            }
        }
        return slots;
    }

    @Transactional
    public List<User> getAllDoctors(){
        return userRepository.findByRole(Roles.ROLE_DOCTOR);
    }

    public Map<Specialization, Set<User>> getDoctorsGroupedBySpecialization(){
        List<User> doctors = userRepository.findByRole(Roles.ROLE_DOCTOR);
        return doctors.stream().filter(user -> user.getSpecialization()!=null).collect(Collectors.groupingBy(User::getSpecialization, Collectors.toSet()));
    }


    @Transactional
    public User getUserById(int id){
        return userRepository.findUserById(id);
    }



}
