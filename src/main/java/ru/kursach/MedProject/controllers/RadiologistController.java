package ru.kursach.MedProject.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kursach.MedProject.enums.MedicalRecordStatus;
import ru.kursach.MedProject.models.MedicalImage;
import ru.kursach.MedProject.models.MedicalRecord;
import ru.kursach.MedProject.models.PesonDetails;
import ru.kursach.MedProject.models.User;
import ru.kursach.MedProject.repositories.MedicalImageRepository;
import ru.kursach.MedProject.repositories.MedicalRecordRepository;
import ru.kursach.MedProject.services.MedicalImageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/radiologistPage")
public class RadiologistController {


    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalImageService medicalImageService;
    private final MedicalImageRepository medicalImageRepository;

    @Autowired
    public RadiologistController(MedicalRecordRepository medicalRecordRepository, MedicalImageService medicalImageService, MedicalImageRepository medicalImageRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.medicalImageService = medicalImageService;
        this.medicalImageRepository = medicalImageRepository;
    }

    @GetMapping("/analysis")
    public String startAnalysis(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PesonDetails pesonDetails = (PesonDetails) authentication.getPrincipal();
        User radiologist = pesonDetails.getUser();
        List<MedicalRecord> medicalRecords = medicalRecordRepository.findAllByStatusAndAssignedRadiologist(MedicalRecordStatus.IMAGING_REQUESTED, radiologist);
        model.addAttribute("imageAnaliseRecords", medicalRecords);
        return "radiologist/showAnalises";
    }

    @GetMapping("/uploadImage/{recordId}")
    public String uploadImage(@PathVariable("recordId") int id, Model model){
        MedicalRecord medicalRecord = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical record not found"));
        model.addAttribute("medicalRecord", medicalRecord);
        model.addAttribute("medicalImage", new MedicalImage());
        return "radiologist/uploadImage";
    }

    @PostMapping("uploadImage/{recordId}")
    public String saveImage(@PathVariable("recordId") int id, @RequestParam("imageFile") MultipartFile file) throws IOException {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical record not found"));
        MedicalImage medicalImage = medicalImageService.saveMedicalImage(file, medicalRecord);
        medicalRecord.addImage(medicalImage);
        medicalImageRepository.save(medicalImage);
        medicalRecordRepository.save(medicalRecord);
        return "redirect:/radiologistPage/image/" + medicalImage.getId();
    }

    @GetMapping("/image/{id}")
    public String analisPage(@PathVariable("id") long id, Model model){
        MedicalImage medicalImage = medicalImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        model.addAttribute("medicalImage", medicalImage);
        model.addAttribute("medicalRecord", medicalImage.getMedicalRecord());

        return "radiologist/imageProcessing";

    }





}
