package ru.kursach.MedProject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kursach.MedProject.enums.BookStatus;
import ru.kursach.MedProject.enums.MedicalRecordStatus;
import ru.kursach.MedProject.enums.Roles;
import ru.kursach.MedProject.models.*;
import ru.kursach.MedProject.repositories.AppointmentRepository;
import ru.kursach.MedProject.repositories.MedicalCardRepository;
import ru.kursach.MedProject.repositories.MedicalRecordRepository;
import ru.kursach.MedProject.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/doctorPage")
public class DoctorController {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final User user;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalCardRepository medicalCardRepository;

    @Autowired
    public DoctorController(AppointmentRepository appointmentRepository, UserRepository userRepository, User user, MedicalRecordRepository medicalRecordRepository, MedicalCardRepository medicalCardRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.user = user;
        this.medicalRecordRepository = medicalRecordRepository;
        this.medicalCardRepository = medicalCardRepository;
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
        Appointment appointment = appointmentRepository.findById(id).get();


        MedicalCardAndRecord form = new MedicalCardAndRecord();
        User user = appointment.getUser();
        User doctor = appointment.getDoctor();
        form.setMedicalCard(user.getMedicalCard());
        form.setMedicalRecord(new MedicalRecord());
        form.getMedicalRecord().setDoctor(doctor);
        form.getMedicalCard().setUser(user);
        form.getMedicalRecord().setAppointment(appointment);
        List<User> radiologists = userRepository.findByRole(Roles.ROLE_RADIOLOGIST);
        model.addAttribute("radiologists", radiologists);
        model.addAttribute("form", form);
        return "doctor/createRecord";
    }

    @PostMapping("/saveMedicalRecord")
    public String sendRecord(@ModelAttribute("form") MedicalCardAndRecord form){

        User doctor = form.getMedicalRecord().getDoctor();
        User patient = form.getMedicalCard().getUser();
        MedicalCard card = form.getMedicalCard();
        MedicalRecord record = form.getMedicalRecord();
        card.addRecord(record);
        record.setMedicalCard(card);
        record.setRecordDate(LocalDateTime.now());


        if (form.getMedicalRecord().getStatus() == MedicalRecordStatus.COMPLETED) {
            record.setCompletionDate(LocalDateTime.now());
        } else if (form.getMedicalRecord().isImagingRequired()) {
            record.setStatus(MedicalRecordStatus.IMAGING_REQUESTED);
        } else {
            record.setStatus(MedicalRecordStatus.IN_PROGRESS);
        }

        medicalRecordRepository.save(record);
        medicalCardRepository.save(card);

        return "redirect:/doctorPage/appointments";
    }



}
