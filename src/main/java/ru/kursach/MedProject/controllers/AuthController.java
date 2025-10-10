package ru.kursach.MedProject.controllers;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kursach.MedProject.models.User;
import ru.kursach.MedProject.services.UserService;
import ru.kursach.MedProject.validators.UserValidator;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private UserService userService;
    private UserValidator userValidator;

    @Autowired
    public AuthController(UserService userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @GetMapping("/login")
    public String login(){
        return "/auth/login";
    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute("user") User user){
        return "/auth/registration";
    }

    @PostMapping("/registration")
    public String registrationSubmit(@RequestParam("confirmPassword") String confirmPassword, @Valid @ModelAttribute("user") User user, BindingResult bindingResult){

        userValidator.setConfirmPassword(confirmPassword);
        userValidator.validate(user, bindingResult);
        userValidator.reset();

        if(bindingResult.hasErrors()){
            return "/auth/registration";
        }
        user.setCreatedAt(LocalDateTime.now());
        userService.save(user);
        return "redirect:/auth/login";
    }
}
