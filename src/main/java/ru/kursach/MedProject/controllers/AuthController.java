package ru.kursach.MedProject.controllers;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kursach.MedProject.models.User;
import ru.kursach.MedProject.services.UserService;
import ru.kursach.MedProject.validators.UserValidator;

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
    public String registrationSubmit(@ModelAttribute("user") @Valid User user, BindingResult bindingResult){

        userValidator.validate(user, bindingResult);
        if(bindingResult.hasErrors()){
            return "/auth/registration";
        }
        userService.save(user);
        return "redirect:/auth/login";
    }
}
