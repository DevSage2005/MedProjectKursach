package ru.kursach.MedProject.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.kursach.MedProject.models.User;
import ru.kursach.MedProject.services.UserService;

@Component
public class UserValidator implements Validator {

    private final UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User newUser = (User) target;

        if(userService.getUserByName(newUser.getName()).isPresent()){
            errors.rejectValue("name", "","User with this name already exist!");
        }

    }
}
