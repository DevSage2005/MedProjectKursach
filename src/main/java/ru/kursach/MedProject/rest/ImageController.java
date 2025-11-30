package ru.kursach.MedProject.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                                         @RequestParam(defaultValue = "1.0", value = "contrast", required = false) double contrast,
                                         @RequestParam(defaultValue = "0", value = "brightness", required = false) int brightness,
                                         @RequestParam(defaultValue = "0.5", value = "sharpness", required = false) double sharpness,
                                         @RequestParam(defaultValue = "false", value = "grayscale", required = false) boolean grayscale,
                                         @RequestParam(defaultValue = "false", required = false) boolean enhance,
                                         @RequestParam(defaultValue = "false", value = "analyze", required = false) boolean analyze) throws IOException {

        MedicalImage medicalImage = medicalImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));

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
        jsonSend.setAnalyze(analyze); // Устанавливаем флаг анализа

        HttpEntity<MedicalImageProcess> request = new HttpEntity<>(jsonSend);

        MedicalImageProcess response = rest.postForObject(url, request, MedicalImageProcess.class);

        // Сохраняем обработанное изображение и результаты анализа
        medicalImageService.saveProcessedMedicalImage(response);


        return "redirect:/radiologistPage/image/" + medicalImage.getId();
    }


    @PostMapping("/analyze/{imageId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> analyzeImage(@PathVariable("imageId") long imageId) throws IOException {
        MedicalImage medicalImage = medicalImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        // Проверяем, есть ли уже анализ
        if (medicalImageService.hasAnalysisResults(imageId)) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Анализ уже выполнен");
            response.put("analysis", medicalImageService.getAnalysisResults(imageId));
            return ResponseEntity.ok(response);
        }

        // Выполняем анализ через Flask сервер
        Path imagePath = Paths.get(medicalImage.getFilePath());
        byte[] imageBytes = Files.readAllBytes(imagePath);
        String encodedOriginalImage = Base64.getEncoder().encodeToString(imageBytes);

        RestTemplate rest = new RestTemplate();
        String url = "http://127.0.0.1:5000/api/analyze";

        // Создаем правильный JSON для Flask
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("encodedImage", encodedOriginalImage);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody);

        try {
            // Получаем ответ как Map
            Map<String, Object> analysisResponse = rest.postForObject(url, request, Map.class);

            if (analysisResponse != null && Boolean.TRUE.equals(analysisResponse.get("success"))) {
                // Конвертируем Map в MedicalAnalysis
                MedicalImageProcess.MedicalAnalysis medicalAnalysis = convertMapToMedicalAnalysis(analysisResponse);

                // Сохраняем результаты
                medicalImageService.saveAnalysisResults(imageId, medicalAnalysis);

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Анализ успешно выполнен");
                response.put("analysis", medicalAnalysis);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("error", analysisResponse != null ? analysisResponse.get("error") : "Ошибка при выполнении анализа");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Ошибка соединения с сервисом анализа: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Метод для конвертации Map в MedicalAnalysis
    private MedicalImageProcess.MedicalAnalysis convertMapToMedicalAnalysis(Map<String, Object> map) {
        MedicalImageProcess.MedicalAnalysis analysis = new MedicalImageProcess.MedicalAnalysis();

        analysis.setSuccess((Boolean) map.get("success"));
        analysis.setTotalFindings(((Number) map.get("totalFindings")).intValue());
        analysis.setCriticalFindings(((Number) map.get("criticalFindings")).intValue());

        // Конвертируем списки находок
        if (map.get("highRisk") != null) {
            List<Map<String, Object>> highRiskList = (List<Map<String, Object>>) map.get("highRisk");
            analysis.setHighRisk(convertFindingsList(highRiskList));
        }

        if (map.get("mediumRisk") != null) {
            List<Map<String, Object>> mediumRiskList = (List<Map<String, Object>>) map.get("mediumRisk");
            analysis.setMediumRisk(convertFindingsList(mediumRiskList));
        }

        if (map.get("lowRisk") != null) {
            List<Map<String, Object>> lowRiskList = (List<Map<String, Object>>) map.get("lowRisk");
            analysis.setLowRisk(convertFindingsList(lowRiskList));
        }

        return analysis;
    }

    private List<MedicalImageProcess.PathologyFinding> convertFindingsList(List<Map<String, Object>> findingsList) {
        return findingsList.stream()
                .map(this::convertToPathologyFinding)
                .collect(Collectors.toList());
    }

    private MedicalImageProcess.PathologyFinding convertToPathologyFinding(Map<String, Object> findingMap) {
        MedicalImageProcess.PathologyFinding finding = new MedicalImageProcess.PathologyFinding();

        finding.setPathology((String) findingMap.get("pathology"));
        finding.setProbability(((Number) findingMap.get("probability")).doubleValue());
        finding.setConfidence((String) findingMap.get("confidence"));
        finding.setRiskLevel((String) findingMap.get("riskLevel"));

        return finding;
    }


    @GetMapping("/analysis/{imageId}")
    @ResponseBody
    public ResponseEntity<MedicalImageProcess.MedicalAnalysis> getAnalysisResults(@PathVariable("imageId") long imageId) {
        MedicalImageProcess.MedicalAnalysis analysis = medicalImageService.getAnalysisResults(imageId);
        return ResponseEntity.ok(analysis);
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