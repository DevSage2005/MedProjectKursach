package ru.kursach.MedProject.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.kursach.MedProject.models.User;
import ru.kursach.MedProject.services.UserService;

import java.util.Objects;

@Component
public class UserValidator implements Validator {

    private final UserService userService;
    private String confirmPassword;

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public void reset() {
        this.confirmPassword = null;
    }

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

        if(userService.getUserByName(newUser.getEmail()).isPresent()){
            errors.rejectValue("email", "","User with this email already exist!");
        }

        if(confirmPassword != null && !newUser.getPassword().equals(confirmPassword)){
            errors.rejectValue("password", "mismatch", "Passwords do not match!");
        } else if (Objects.equals(confirmPassword, "")) {
            errors.rejectValue("password", "empty", "Password confirmation is required");
        }


    }
}
