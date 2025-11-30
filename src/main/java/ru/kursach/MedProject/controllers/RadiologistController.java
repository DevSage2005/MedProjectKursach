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
import ru.kursach.MedProject.util.MedicalImageProcess;

import java.io.IOException;
import java.time.LocalDateTime;
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

    @GetMapping("/image/{imageId}")
    public String viewImage(@PathVariable Long imageId, Model model) {
        MedicalImage medicalImage = medicalImageRepository.findById(imageId).orElseThrow();
        MedicalRecord medicalRecord = medicalImage.getMedicalRecord();

        model.addAttribute("medicalImage", medicalImage);
        model.addAttribute("medicalRecord", medicalRecord);

        // Всегда проверяем и добавляем результаты анализа
        if (medicalImageService.hasAnalysisResults(imageId)) {
            MedicalImageProcess.MedicalAnalysis analysis = medicalImageService.getAnalysisResults(imageId);
            model.addAttribute("analysisResults", analysis);
            model.addAttribute("analysisSummary", medicalImageService.getAnalysisSummaryObject(imageId));

            // Добавляем время анализа (если нужно)
            model.addAttribute("analysisPerformedAt", medicalImage.getProcessedAt());
        } else {
            // Если анализа нет, добавляем пустые объекты чтобы не было ошибок в шаблоне
            model.addAttribute("analysisResults", null);
            model.addAttribute("analysisSummary", null);
        }
        return "radiologist/imageProcessing";
    }


    @PostMapping("/saveAnalizResult/{id}")
    public String saveResult(@ModelAttribute("medicalRecord") MedicalRecord medicalRecord){
        MedicalRecord existingRecord = medicalRecordRepository.findById(medicalRecord.getId())
                .orElseThrow(() -> new RuntimeException("Medical record not found"));
        existingRecord.setImagingResults(medicalRecord.getImagingResults());
        existingRecord.setStatus(MedicalRecordStatus.IMAGING_COMPLETED);
        medicalRecordRepository.save(existingRecord);
        return "redirect:/radiologistPage/analysis";
    }






}
