package ru.kursach.MedProject.controllers;


import jakarta.validation.Valid;
import org.hibernate.internal.build.AllowPrintStacktrace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kursach.MedProject.enums.Roles;
import ru.kursach.MedProject.models.User;
import ru.kursach.MedProject.services.AdminService;
import ru.kursach.MedProject.services.UserService;
import ru.kursach.MedProject.validators.UserValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/adminPage")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final UserValidator userValidator;

    @Autowired
    public AdminController(AdminService adminService, UserService userService, UserValidator userValidator) {
        this.adminService = adminService;
        this.userService = userService;
        this.userValidator = userValidator;
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

    @PostMapping("/deleteRole/{id}")
    public String deleteUserRole(@PathVariable("id") int id, @RequestParam("deleteRole") String deleteRole){
        adminService.deleteRoleFromUser(id,deleteRole);
        return "redirect:/adminPage";
    }


    @GetMapping("/addDoctor")
    public String addDoctor(@ModelAttribute("newDoctor") User user){
        return "user/addDoctor";
    }

    @PostMapping("/addDoctor")
    public String saveDoctor(@RequestParam("confirmPassword") String confirmPassword, @Valid @ModelAttribute("newDoctor") User user, BindingResult bindingResult){

        userValidator.setConfirmPassword(confirmPassword);
        userValidator.validate(user, bindingResult);
        userValidator.reset();

        if(bindingResult.hasErrors()){
            return "user/addDoctor";
        }

        user.addRole(Roles.ROLE_DOCTOR);
        user.setCreatedAt(LocalDateTime.now());
        userService.save(user);
        return "redirect:/adminPage";
    }


}
