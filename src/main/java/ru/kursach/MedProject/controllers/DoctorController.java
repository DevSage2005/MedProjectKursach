package ru.kursach.MedProject.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kursach.MedProject.enums.BookStatus;
import ru.kursach.MedProject.enums.Roles;
import ru.kursach.MedProject.models.*;
import ru.kursach.MedProject.repositories.AppointmentRepository;
import ru.kursach.MedProject.repositories.UserRepository;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/doctorPage")
public class DoctorController {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final User user;

    public DoctorController(AppointmentRepository appointmentRepository, UserRepository userRepository, User user) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.user = user;
    }

    @GetMapping("/appointments")
    public String getAppointments(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PesonDetails pesonDetails = (PesonDetails) authentication.getPrincipal();


        User doctor = pesonDetails.getUser();
        List<Appointment> doctorAppointments = appointmentRepository.findAllByDoctorAndDate(doctor, new Date()).stream()
                .sorted(Comparator.comparing(Appointment::getSlot))
                .toList();
        List<Appointment> scheduledAppointments = doctorAppointments.stream().filter(dApp -> dApp.getStatus() == BookStatus.SCHEDULED).toList();
        List<Appointment> completedAppointments = doctorAppointments.stream().filter(dApp -> dApp.getStatus() == BookStatus.COMPLETED).toList();
        List<Appointment> cancelledAppointments = doctorAppointments.stream().filter(dApp -> dApp.getStatus() == BookStatus.CANCELLED).toList();
        model.addAttribute("scheduledAppointments", scheduledAppointments);
        model.addAttribute("completedAppointments", completedAppointments);
        model.addAttribute("cancelledAppointments", cancelledAppointments);
        model.addAttribute("doctorAppointments", doctorAppointments);
        model.addAttribute("currentDate", new Date());
        return "doctor/doctorAppointments";
    }

    @GetMapping("/createMedicalRecord/{id}")
    public String createRecord(@PathVariable("id") int id, Model model){
        MedicalCardAndRecord form = new MedicalCardAndRecord();
        User user = userRepository.findUserById(id);
        form.setMedicalCard(user.getMedicalCard());
        form.setMedicalRecord(new MedicalRecord());
        List<User> radiologists = userRepository.findByRole(Roles.ROLE_RADIOLOGIST);
        model.addAttribute("radiologists", radiologists);
        model.addAttribute("form", form);
        return "doctor/createRecord";
    }



}
