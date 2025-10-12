package ru.kursach.MedProject.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.kursach.MedProject.models.DoctorScheduleWorkingHours;
import ru.kursach.MedProject.services.UserService;

import java.util.Objects;

@Component
public class DoctorScheduleWorkingHoursValidator implements Validator {

    private final UserService userService;
    private String confirmPassword;

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public void reset() {
        this.confirmPassword = null;
    }

    @Autowired
    public DoctorScheduleWorkingHoursValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return DoctorScheduleWorkingHours.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        DoctorScheduleWorkingHours form = (DoctorScheduleWorkingHours) target;

        if(userService.getUserByName(form.getUser().getEmail()).isPresent()){
            errors.rejectValue("user.email", "","User with this email already exist!");
        }

        if(confirmPassword != null && !form.getUser().getPassword().equals(confirmPassword)){
            errors.rejectValue("user.password", "mismatch", "Passwords do not match!");
        } else if (Objects.equals(confirmPassword, "")) {
            errors.rejectValue("user.password", "empty", "Password confirmation is required");
        }


    }
}
