package ru.kursach.MedProject.controllers;


import org.hibernate.internal.build.AllowPrintStacktrace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kursach.MedProject.enums.Roles;
import ru.kursach.MedProject.models.User;
import ru.kursach.MedProject.services.AdminService;
import ru.kursach.MedProject.services.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/adminPage")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    @Autowired
    public AdminController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
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
}
