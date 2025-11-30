package ru.kursach.MedProject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public String getAppointments(Model model,
                                  @RequestParam(value = "firstName", required = false) String firstName,
                                  @RequestParam(value = "lastName", required = false) String lastName,
                                  @RequestParam(value = "middleName", required = false) String middleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PesonDetails pesonDetails = (PesonDetails) authentication.getPrincipal();

        // Получаем все завершенные анализы для этого доктора
        User doctor = pesonDetails.getUser();
        List<MedicalRecord> medicalRecordsImageCompleted = medicalRecordRepository
                .findByStatusAndDoctor(MedicalRecordStatus.IMAGING_COMPLETED, doctor);

        MedicalRecord searchRecord = null;
        if (firstName != null && !firstName.isEmpty() &&
                lastName != null && !lastName.isEmpty() &&
                middleName != null && !middleName.isEmpty()) {

            searchRecord = medicalRecordsImageCompleted.stream()
                    .filter(record -> {
                        User patient = record.getMedicalCard().getUser();
                        return patient.getFirstName().equalsIgnoreCase(firstName) &&
                                patient.getLastName().equalsIgnoreCase(lastName) &&
                                patient.getMiddleName().equalsIgnoreCase(middleName);
                    })
                    .findFirst()
                    .orElse(null);
        }

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
        model.addAttribute("medicalRecordsImageCompleted", medicalRecordsImageCompleted);
        model.addAttribute("searchRecord", searchRecord);
        model.addAttribute("currentDate", new Date());

        return "doctor/doctorAppointments";
    }


    @GetMapping("/showRecordImageCompleted/{id}")
    public String showRecord(@PathVariable("id") int recordId, Model model){
        MedicalRecord medicalRecord = medicalRecordRepository.findById(recordId).get();
        MedicalCardAndRecord form = new MedicalCardAndRecord();
        form.setMedicalCard(medicalRecord.getMedicalCard());
        form.setMedicalRecord(medicalRecord);

        List<MedicalImage> medicalImages = medicalRecord.getMedicalImage();
        model.addAttribute("form", form);
        model.addAttribute("medicalImages",medicalImages);
        return "doctor/showRecordImageCompleted";
    }

    @GetMapping("/showMedicalCard/{id}")
    public String showCard(@PathVariable("id") int medCardId,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "5") int size,
                           Model model) {

        MedicalCard medicalCard = medicalCardRepository.findById(medCardId)
                .orElseThrow(() -> new RuntimeException("Medical card not found"));

        // Получаем записи отсортированные по дате окончания (новые сверху)
        Pageable pageable = PageRequest.of(page, size, Sort.by("completionDate").descending());
        Page<MedicalRecord> medicalRecordsPage = medicalRecordRepository
                .findByMedicalCardOrderByCompletionDateDesc(medicalCard, pageable);

        model.addAttribute("medicalCard", medicalCard);
        model.addAttribute("medicalRecords", medicalRecordsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", medicalRecordsPage.getTotalPages());
        model.addAttribute("totalRecords", medicalRecordsPage.getTotalElements());
        model.addAttribute("pageSize", size);

        return "doctor/patientMedCard";
    }


    @GetMapping("/createMedicalRecord/{id}")
    public String createRecord(@PathVariable("id") int id, Model model) {
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
        model.addAttribute("appointment", appointment);
        return "doctor/createRecord";
    }

    @PostMapping("/saveMedicalRecord")
    public String sendRecord(@ModelAttribute("form") MedicalCardAndRecord form) {

        User doctor = form.getMedicalRecord().getDoctor();
        User patient = form.getMedicalCard().getUser();
        MedicalCard card = form.getMedicalCard();
        MedicalRecord record = form.getMedicalRecord();
        card.addRecord(record);
        record.setMedicalCard(card);
        record.setRecordDate(LocalDateTime.now());


        if (form.getMedicalRecord().isImagingRequired()) {
            record.setStatus(MedicalRecordStatus.IMAGING_REQUESTED);
        } else{
            record.setStatus(MedicalRecordStatus.COMPLETED);
        }

        if (form.getMedicalRecord().getStatus() == MedicalRecordStatus.COMPLETED) {
            record.setCompletionDate(LocalDateTime.now());
        }

        Appointment existAppointment = record.getAppointment();
        existAppointment.setStatus(BookStatus.COMPLETED);

        appointmentRepository.save(existAppointment);
        medicalRecordRepository.save(record);
        medicalCardRepository.save(card);

        return "redirect:/doctorPage/appointments";
    }


}
