package ru.kursach.MedProject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kursach.MedProject.enums.BookStatus;
import ru.kursach.MedProject.enums.Roles;
import ru.kursach.MedProject.enums.Specialization;
import ru.kursach.MedProject.models.*;
import ru.kursach.MedProject.repositories.AppointmentRepository;
import ru.kursach.MedProject.repositories.ScheduleRepository;
import ru.kursach.MedProject.repositories.UserRepository;
import ru.kursach.MedProject.services.UserService;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

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
    public String getDoctors(@RequestParam(value = "specialization", required = false) String spec, Model model){
        List<User> doctors;
        if(spec != null && !spec.equals("all") && !spec.isEmpty()) {
            try {
                Specialization specialization = Specialization.valueOf(spec);
                doctors = userRepository.findUserByRoleAndSpecialization(Roles.ROLE_DOCTOR, specialization);
                model.addAttribute("selectedSpecialization", specialization);
            } catch (IllegalArgumentException e) {
                doctors = userRepository.findByRole(Roles.ROLE_DOCTOR);
            }
        } else {
            doctors = userRepository.findByRole(Roles.ROLE_DOCTOR);
        }
        model.addAttribute("doctors", doctors);
        return "user/searchDoctor";
    }

    @GetMapping("/bookDoctor/{doctor_id}")
    public String bookDoctor(@PathVariable("doctor_id") int doctor_id, @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, Model model){
        User doctor = userRepository.findUserById(doctor_id);
        if(date!=null) {
            Date utilDate = java.sql.Date.valueOf(date);
            model.addAttribute("slots", userService.getSlots(utilDate, doctor));
            model.addAttribute("selectedDate", date);
        }

        model.addAttribute("selectedDoctor",doctor);
        return "user/bookDoctor";
    }

    @PostMapping("/confirmation")
    public String confirmation(@RequestParam("doctorId") int doctor_id, @RequestParam(value = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, @RequestParam("selectedTime") LocalTime time) throws ParseException {
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

    @GetMapping("/appointments")
    public String getAppointments(@RequestParam(value = "filter", defaultValue = "all") String filter,Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PesonDetails pesonDetails = (PesonDetails) authentication.getPrincipal();
        User user = pesonDetails.getUser();
        List<Appointment> appointments = appointmentRepository.findAllByUser(user);
        user.setAppointment(appointments);

        List<Appointment> filteredAppointments;
        switch (filter) {
            case "upcoming":
                filteredAppointments = user.getAppointmentsByStatus(BookStatus.SCHEDULED);
                break;
            case "completed":
                filteredAppointments = user.getAppointmentsByStatus(BookStatus.COMPLETED);
                break;
            case "cancelled":
                filteredAppointments = user.getAppointmentsByStatus(BookStatus.CANCELLED);
                break;
            default:
                filteredAppointments = appointments;
                break;
        }

        model.addAttribute("appointments", filteredAppointments);
        model.addAttribute("allAppointments", appointments);
        model.addAttribute("user", user);
        model.addAttribute("upcomingCount", user.getAppointmentsByStatus(BookStatus.SCHEDULED));
        model.addAttribute("completedCount", user.getAppointmentsByStatus(BookStatus.COMPLETED));
        model.addAttribute("cancelledCount", user.getAppointmentsByStatus(BookStatus.CANCELLED));
        model.addAttribute("currentFilter", filter);
        return "user/appointments";
    }


    @GetMapping("/aboutDoctor/{id}")
    public String showDoctor(@PathVariable("id") int id, Model model){
        model.addAttribute("doctor",userRepository.findUserById(id));
        return "user/aboutDoctor";
    }



    @PostMapping("/appointments/cancel/{id}")
    public String cancelAppointment(@PathVariable("id") int id) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment != null) {
            appointment.setStatus(BookStatus.CANCELLED);
            appointmentRepository.save(appointment);
        }
        return "redirect:/user/appointments";
    }






}
