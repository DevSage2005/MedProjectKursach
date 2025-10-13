package ru.kursach.MedProject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kursach.MedProject.enums.BookStatus;
import ru.kursach.MedProject.enums.DayOfWeek;
import ru.kursach.MedProject.enums.Roles;
import ru.kursach.MedProject.enums.Specialization;
import ru.kursach.MedProject.models.*;
import ru.kursach.MedProject.repositories.AppointmentRepository;
import ru.kursach.MedProject.repositories.ScheduleRepository;
import ru.kursach.MedProject.repositories.UserRepository;
import ru.kursach.MedProject.services.UserService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserService userService;
    private final AppointmentRepository appointmentRepository;



    @Autowired
    public UserController(UserRepository userRepository, ScheduleRepository scheduleRepository, UserService userService, AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.userService = userService;
        this.appointmentRepository=appointmentRepository;
    }

    @GetMapping("/searchDoctors")
    public String getDoctors(){
        return "user/appointment";
    }

    @GetMapping("/bookDoctor/{doctor_id}")
    public String bookDoctor(@PathVariable("doctor_id") int doctor_id, Model model){
        model.addAttribute("selectedDoctor",userRepository.findUserById(doctor_id));
        return "user/bookDoctor";
    }

    @PostMapping("/searchDoctors")
    public String showDoctors(@RequestParam("specialization") String spec, Model model){

        Specialization specialization = Specialization.valueOf(spec);
        model.addAttribute("doctors", userRepository.findUserByRoleAndSpecialization(Roles.ROLE_DOCTOR, specialization));
        model.addAttribute("selectedSpecialization", specialization);
        return "user/appointment";
    }

    @PostMapping("/bookDoctor/{doctor_id}")
    public String bookedDoctor(@PathVariable("doctor_id") int doctor_id, @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, Model model){
        User doctor =  userRepository.findUserById(doctor_id);
        Date utilDate = java.sql.Date.valueOf(date);
        model.addAttribute("slots", userService.getSlots(utilDate, doctor));
        model.addAttribute("selectedDoctor", doctor);
        model.addAttribute("selectedDate", date);
        return "user/bookDoctor";
    }

    @PostMapping("/showConfirmation")
    public String confirmation(@RequestParam("doctorId") int doctor_id, @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, @RequestParam("selectedTime") LocalTime time) throws ParseException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PesonDetails pesonDetails = (PesonDetails) authentication.getPrincipal();
        Date sqlDate = java.sql.Date.valueOf(date);
        User patient = pesonDetails.getUser();
        Appointment appointment = new Appointment();
        appointment.setDate(sqlDate);
        appointment.setUser(patient);
        appointment.setDoctor(userRepository.findUserById(doctor_id));
        appointment.setSlot(time);
        appointment.setStatus(BookStatus.SCHEDULED);
        appointmentRepository.save(appointment);
        return "redirect:/main/index";
    }


}
