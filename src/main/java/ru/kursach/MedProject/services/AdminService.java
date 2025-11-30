package ru.kursach.MedProject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kursach.MedProject.enums.Roles;
import ru.kursach.MedProject.models.Schedule;
import ru.kursach.MedProject.models.User;
import ru.kursach.MedProject.models.WorkingHours;
import ru.kursach.MedProject.repositories.ScheduleRepository;
import ru.kursach.MedProject.repositories.UserRepository;
import ru.kursach.MedProject.repositories.WorkingHoursRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class AdminService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final User user;
    private final ScheduleRepository scheduleRepository;
    private final WorkingHoursRepository workingHoursRepository;

    @Autowired
    public AdminService(UserRepository userRepository, UserService userService, User user, ScheduleRepository scheduleRepository, WorkingHoursRepository workingHoursRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.user = user;
        this.scheduleRepository = scheduleRepository;
        this.workingHoursRepository = workingHoursRepository;
    }

    public Set<User> getUsers(){
        return new HashSet<>(userRepository.findAll());
    }

    public void setRoleForUser(int id, String role){
        User user = userService.getUserById(id);
        Roles newRole = Roles.valueOf(role.toUpperCase());
        user.setRole(newRole);
        userRepository.save(user);
    }

    public void saveSchedule(Schedule schedule){
        scheduleRepository.save(schedule);
    }


    public void saveWorkingHours(List<WorkingHours> workingHours) {
        workingHoursRepository.saveAll(workingHours);
    }
}
