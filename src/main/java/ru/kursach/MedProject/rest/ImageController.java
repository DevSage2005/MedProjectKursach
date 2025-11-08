package ru.kursach.MedProject.rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.kursach.MedProject.models.MedicalImage;
import ru.kursach.MedProject.repositories.MedicalImageRepository;
import ru.kursach.MedProject.services.MedicalImageService;
import ru.kursach.MedProject.util.MedicalImageProcess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Controller
@RequestMapping("/imageData")
public class ImageController {

    private final MedicalImageRepository medicalImageRepository;
    private final MedicalImageService medicalImageService;

    @Autowired
    public ImageController(MedicalImageRepository medicalImageRepository, MedicalImageService medicalImageService) {
        this.medicalImageRepository = medicalImageRepository;
        this.medicalImageService = medicalImageService;
    }


    @PostMapping("/getProcessedImage/{imageId}")
    private String getProcessedImageData(@PathVariable("imageId") long imageId,
                                                         @RequestParam(defaultValue = "1.0",value = "contrast", required = false) double contrast,
                                                         @RequestParam(defaultValue = "0",value = "brightness", required = false) int brightness,
                                                         @RequestParam(defaultValue = "0.5",value = "sharpness", required = false) double sharpness,
                                                         @RequestParam(defaultValue = "false",value = "grayscale", required = false) boolean grayscale,
                                                         @RequestParam(defaultValue = "false", required = false) boolean enhance) throws IOException {
        MedicalImage medicalImage = medicalImageRepository.findById(imageId).get();

        Path imagePath = Paths.get(medicalImage.getFilePath());
        byte[] imageBytes = Files.readAllBytes(imagePath);
        String encodedOriginalImage = Base64.getEncoder().encodeToString(imageBytes);

        RestTemplate rest = new RestTemplate();
        String url = "http://127.0.0.1:5000/api/process";

        MedicalImageProcess jsonSend = new MedicalImageProcess();
        jsonSend.setId(medicalImage.getId());
        jsonSend.setEncodedOriginalImage(encodedOriginalImage);
        jsonSend.setContrast(contrast);
        jsonSend.setBrightness(brightness);
        jsonSend.setSharpness(sharpness);
        jsonSend.setGrayscale(grayscale);
        jsonSend.setEnhance(enhance);
        jsonSend.setExtension(medicalImageService.getFileExtension(medicalImage.getFilePath()));

        HttpEntity<MedicalImageProcess> request = new HttpEntity<>(jsonSend);

        MedicalImageProcess response = rest.postForObject(url, request, MedicalImageProcess.class);
        medicalImageService.saveProcessedMedicalImage(response);
        return "redirect:/radiologistPage/image/"+medicalImage.getId();
    }


    @ResponseBody
    @GetMapping("/{imageId}")
    public ResponseEntity<byte[]> getImageData(@PathVariable("imageId") long imageId) throws IOException {
        MedicalImage medicalImage = medicalImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        Path imagePath = Paths.get(medicalImage.getFilePath());
        byte[] imageData = Files.readAllBytes(imagePath);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageData);
    }

    @ResponseBody
    @GetMapping("/processedImage/{imageId}")
    public ResponseEntity<byte[]> getProcessedImageData(@PathVariable("imageId") long imageId) throws IOException {
        MedicalImage medicalImage = medicalImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        Path imagePath = Paths.get(medicalImage.getProcessedFilePath());
        byte[] imageData = Files.readAllBytes(imagePath);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageData);
    }
}
