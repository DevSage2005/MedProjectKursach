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
import ru.kursach.MedProject.models.DoctorScheduleWorkingHours;
import ru.kursach.MedProject.models.Schedule;
import ru.kursach.MedProject.models.User;
import ru.kursach.MedProject.models.WorkingHours;
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

    @Autowired
    public AdminController(AdminService adminService, UserService userService, DoctorScheduleWorkingHoursValidator validator) {
        this.adminService = adminService;
        this.userService = userService;
        this.validator = validator;
    }

    @GetMapping()
    public String getPage(Model model){
        List<User> users = new ArrayList<>(adminService.getUsers());
        List<Roles> roles = new ArrayList<>(List.of(Roles.ROLE_ADMIN,Roles.ROLE_USER,Roles.ROLE_DOCTOR));
        model.addAttribute("userRoles", roles);
        model.addAttribute("users", users);
        return "user/admin";
    }


    @PostMapping("/setRole/{id}")
    public String setUserRole(@PathVariable("id") int id, @RequestParam("newRole") String role){
        adminService.setRoleForUser(id,role);
        return "redirect:/adminPage";

    }


    @GetMapping("/addDoctor")
    public String addDoctor(Model model){
        DoctorScheduleWorkingHours form =  new DoctorScheduleWorkingHours();
        User user = new User();
        Schedule schedule = new Schedule();
        List<WorkingHours> workingHours = new ArrayList<>(7);


        for(DayOfWeek d : DayOfWeek.values()){
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
    public String saveDoctor(@RequestParam("confirmPassword") String confirmPassword, @Valid @ModelAttribute("form") DoctorScheduleWorkingHours form, BindingResult bindingResult){


        validator.setConfirmPassword(confirmPassword);
        validator.validate(form, bindingResult);
        validator.reset();

        if(bindingResult.hasErrors()){
            return "user/addDoctor";
        }





        form.getUser().setRole(Roles.ROLE_DOCTOR);
        form.getUser().setCreatedAt(LocalDateTime.now());
        userService.save(form.getUser());
        form.getSchedule().setDoctor(form.getUser());
        adminService.saveSchedule(form.getSchedule());
        for(WorkingHours wh: form.getWorkingHours())
        {
            wh.setSchedule(form.getSchedule());
        }
        adminService.saveWorkingHours(form.getWorkingHours());
        return "redirect:/adminPage";
    }


}
