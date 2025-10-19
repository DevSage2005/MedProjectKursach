package ru.kursach.MedProject.controllers;


import jakarta.validation.Valid;
import org.hibernate.internal.build.AllowPrintStacktrace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kursach.MedProject.enums.DayOfWeek;
import ru.kursach.MedProject.enums.Roles;
import ru.kursach.MedProject.enums.Specialization;
import ru.kursach.MedProject.models.DoctorScheduleWorkingHours;
import ru.kursach.MedProject.models.Schedule;
import ru.kursach.MedProject.models.User;
import ru.kursach.MedProject.models.WorkingHours;
import ru.kursach.MedProject.repositories.UserRepository;
import ru.kursach.MedProject.services.AdminService;
import ru.kursach.MedProject.services.UserService;
import ru.kursach.MedProject.validators.DoctorScheduleWorkingHoursValidator;
import ru.kursach.MedProject.validators.UserValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;

@Controller
@RequestMapping("/adminPage")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final DoctorScheduleWorkingHoursValidator validator;
    private final UserRepository userRepository;
    private final User user;

    @Autowired
    public AdminController(AdminService adminService, UserService userService, DoctorScheduleWorkingHoursValidator validator, UserRepository userRepository, User user) {
        this.adminService = adminService;
        this.userService = userService;
        this.validator = validator;
        this.userRepository = userRepository;
        this.user = user;
    }

    @GetMapping()
    public String getPage(Model model) {
        List<User> users = new ArrayList<>(adminService.getUsers());
        List<Roles> roles = new ArrayList<>(List.of(Roles.ROLE_ADMIN, Roles.ROLE_USER, Roles.ROLE_DOCTOR));
        model.addAttribute("userRoles", roles);
        model.addAttribute("users", users);

        long doctors = users.stream().filter(user -> user.getRole() == Roles.ROLE_DOCTOR).count();
        long simpleUsers = users.stream().filter(user -> user.getRole() == Roles.ROLE_USER).count();
        long admins = users.stream().filter(user -> user.getRole() == Roles.ROLE_ADMIN).count();
        long radiologistCount = users.stream().filter(user -> user.getRole() == Roles.ROLE_RADIOLOGIST).count();


        model.addAttribute("adminCount", admins);
        model.addAttribute("doctorCount", doctors);
        model.addAttribute("userCount", simpleUsers);
        model.addAttribute("radiologistCount", radiologistCount);

        return "user/admin";
    }


    @PostMapping("/setRole/{id}")
    public String setUserRole(@PathVariable("id") int id, @RequestParam("newRole") String role) {
        adminService.setRoleForUser(id, role);
        return "redirect:/adminPage";

    }


    @GetMapping("/addDoctor")
    public String addDoctor(Model model) {
        DoctorScheduleWorkingHours form = new DoctorScheduleWorkingHours();
        User user = new User();
        Schedule schedule = new Schedule();
        List<WorkingHours> workingHours = new ArrayList<>(7);


        for (DayOfWeek d : DayOfWeek.values()) {
            WorkingHours workHour = new WorkingHours();
            workHour.setDayOfWeek(d);
            workingHours.add(workHour);
        }

        form.setUser(user);
        form.setSchedule(schedule);
        form.setWorkingHours(workingHours);
        model.addAttribute("form", form);
        return "user/addDoctor";
    }

    @PostMapping("/addDoctor")
    public String saveDoctor(@RequestParam("confirmPassword") String confirmPassword, @Valid @ModelAttribute("form") DoctorScheduleWorkingHours form, BindingResult bindingResult) {


        validator.setConfirmPassword(confirmPassword);
        validator.validate(form, bindingResult);
        validator.reset();

        if (bindingResult.hasErrors()) {
            return "user/addDoctor";
        }
        form.getUser().setCreatedAt(LocalDateTime.now());
        userService.save(form.getUser());
        form.getSchedule().setDoctor(form.getUser());
        adminService.saveSchedule(form.getSchedule());
        for (WorkingHours wh : form.getWorkingHours()) {
            wh.setSchedule(form.getSchedule());
        }
        adminService.saveWorkingHours(form.getWorkingHours());
        return "redirect:/adminPage";
    }


    @GetMapping("/searchForEdit")
    public String searchDoctors(@RequestParam(value = "filterSpecialization", required = false) String spec, Model model) {

        List<User> doctors;
        if (spec != null && !spec.equals("all") && !spec.isEmpty()) {
            try {
                Specialization specialization = Specialization.valueOf(spec);
                doctors = userRepository.findUserByRoleAndSpecialization(Roles.ROLE_DOCTOR, specialization);
                model.addAttribute("selectedSpecialization", specialization);
            } catch (IllegalArgumentException e) {
                doctors = userRepository.findByRole(Roles.ROLE_DOCTOR);
            }
        } else {
            doctors = userRepository.findByRole(Roles.ROLE_DOCTOR);
        }
        model.addAttribute("doctors", doctors);
        return "user/searchForEdit";
    }

    @GetMapping("/editDoctor/{id}")
    public String getPageEdit(@PathVariable("id") int id, Model model) {
        DoctorScheduleWorkingHours form = new DoctorScheduleWorkingHours();
        User doctor = userRepository.findUserById(id);
        form.setUser(doctor);
        if (form.getUser().getSchedule() == null) {
            form.setSchedule(new Schedule());
        }
        else{
            form.setSchedule(form.getUser().getSchedule());
        }
        if (form.getUser().getSchedule()==null) {
            List<WorkingHours> workingHours = new ArrayList<>(7);
            for (DayOfWeek d : DayOfWeek.values()) {
                WorkingHours workHour = new WorkingHours();
                workHour.setDayOfWeek(d);
                workingHours.add(workHour);
            }
            form.setWorkingHours(workingHours);
        }
        else{
            form.setWorkingHours(form.getUser().getSchedule().getWorkingHours());
        }
        model.addAttribute("form", form);
        return "user/editDoctor";
    }

    @PostMapping("/editDoctor/{id}")
    public String confirmEdit(@PathVariable("id") int id, @ModelAttribute("form") DoctorScheduleWorkingHours form) {
        User doctor = form.getUser();
        Schedule schedule = form.getSchedule();
        List<WorkingHours> workingHours = form.getWorkingHours();
        doctor.setSchedule(schedule);
        schedule.setDoctor(doctor);
        schedule.setWorkingHours(workingHours);
        for (WorkingHours wh : workingHours) {
            wh.setSchedule(schedule);
        }
        userRepository.save(doctor);
        return "redirect:/adminPage";
    }
}
