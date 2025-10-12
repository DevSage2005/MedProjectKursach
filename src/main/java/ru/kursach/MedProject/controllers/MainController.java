package ru.kursach.MedProject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kursach.MedProject.enums.Specialization;
import ru.kursach.MedProject.models.User;
import ru.kursach.MedProject.services.UserService;

import java.util.*;

@Controller
@RequestMapping("/main")
public class MainController {

    private final UserService userService;

    @Autowired
    public MainController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/index")
    public String index(Model model){

        Map<Specialization, Set<User>> specializationDoctors = userService.getDoctorsGroupedBySpecialization();
        model.addAttribute("doctors" ,specializationDoctors);
        return "main/index";
    }

    @GetMapping("/about")
    public String about(){
        return "main/about";
    }
}
