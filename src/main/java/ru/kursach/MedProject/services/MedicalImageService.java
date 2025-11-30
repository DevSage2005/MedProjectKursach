package ru.kursach.MedProject.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.kursach.MedProject.enums.ImageStatus;
import ru.kursach.MedProject.models.MedicalImage;
import ru.kursach.MedProject.models.MedicalRecord;
import ru.kursach.MedProject.models.User;
import ru.kursach.MedProject.repositories.MedicalImageRepository;
import ru.kursach.MedProject.util.MedicalImageProcess;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class MedicalImageService {

    private final MedicalImageRepository medicalImageRepository;
    private final ObjectMapper objectMapper;

    @Value("${medical.images.storage.path:./medical-images}")
    private String storageBasePath;

    public MedicalImageService(MedicalImageRepository medicalImageRepository, ObjectMapper objectMapper) {
        this.medicalImageRepository = medicalImageRepository;
        this.objectMapper = objectMapper;
    }

    public MedicalImage saveMedicalImage(MultipartFile file, MedicalRecord medicalRecord) throws IOException {
        User patient = medicalRecord.getMedicalCard().getUser();
        String patientFolder = createPatientFolder(patient);

        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        String storedFileName = generateUniqueFileName(fileExtension);

        // Сохраняем файл
        Path filePath = Paths.get(patientFolder, storedFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        MedicalImage medicalImage = new MedicalImage();
        medicalImage.setMedicalRecord(medicalRecord);
        medicalImage.setOriginalFileName(originalFileName);
        medicalImage.setFilePath(filePath.toString());
        medicalImage.setFileSize(file.getSize());

        return medicalImageRepository.save(medicalImage);
    }

    @Transactional
    public void saveProcessedMedicalImage(MedicalImageProcess medicalImageProcess) throws IOException {
        MedicalImage medicalImage = medicalImageRepository.findById(medicalImageProcess.getId())
                .orElseThrow(() -> new RuntimeException("MedicalImage not found with id: " + medicalImageProcess.getId()));

        // Сохраняем обработанное изображение
        String folderName = "processedImage";
        Path processedImagePath = Paths.get(Paths.get(medicalImage.getFilePath()).getParent().toString(), folderName);
        if (!Files.exists(processedImagePath)) {
            Files.createDirectories(processedImagePath);
        }

        String processedImageName = "processed_image." + medicalImageProcess.getExtension();
        Path filePath = Paths.get(processedImagePath.toString(), processedImageName);
        String stringFilePath = filePath.toString();

        ByteArrayInputStream byte_image = new ByteArrayInputStream(Base64.getDecoder().decode(medicalImageProcess.getEncodedProcessedImage()));
        BufferedImage image = ImageIO.read(byte_image);
        byte_image.close();

        File file = new File(stringFilePath);
        if (file.exists()) {
            file.delete();
        }

        ImageIO.write(image, medicalImageProcess.getExtension(), file);

        // Обновляем медицинское изображение
        medicalImage.setProcessedFilePath(stringFilePath);
        medicalImage.setProcessedAt(LocalDateTime.now());

        // Обновляем статус
        if (medicalImageProcess.getAnalysis() != null) {
            medicalImage.setStatus(ImageStatus.PROCESSED_AND_ANALYZED);
            saveAnalysisResults(medicalImage, medicalImageProcess.getAnalysis());
        } else {
            medicalImage.setStatus(ImageStatus.PROCESSED);
        }

        medicalImageRepository.save(medicalImage);
    }

    // Новый метод для сохранения результатов AI анализа
    @Transactional
    public void saveAnalysisResults(Long imageId, MedicalImageProcess.MedicalAnalysis analysis) {
        MedicalImage medicalImage = medicalImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("MedicalImage not found with id: " + imageId));

        saveAnalysisResults(medicalImage, analysis);
    }

    // Внутренний метод для сохранения анализа
    private void saveAnalysisResults(MedicalImage medicalImage, MedicalImageProcess.MedicalAnalysis analysis) {
        try {
            String analysisJson = objectMapper.writeValueAsString(analysis);
            medicalImage.setAnalysisResults(analysisJson);
            medicalImage.setProcessedAt(LocalDateTime.now());

            // Обновляем статус
            if (medicalImage.getProcessedFilePath() != null) {
                medicalImage.setStatus(ImageStatus.PROCESSED_AND_ANALYZED);
            } else {
                medicalImage.setStatus(ImageStatus.ANALYZED);
            }

            medicalImageRepository.save(medicalImage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error saving analysis results", e);
        }
    }

    // Метод для получения результатов анализа
    public MedicalImageProcess.MedicalAnalysis getAnalysisResults(Long imageId) {
        MedicalImage medicalImage = medicalImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("MedicalImage not found with id: " + imageId));

        return convertJsonToAnalysis(medicalImage.getAnalysisResults());
    }

    // Метод для проверки наличия анализа
    public boolean hasAnalysisResults(Long imageId) {
        MedicalImage medicalImage = medicalImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("MedicalImage not found with id: " + imageId));

        return medicalImage.getAnalysisResults() != null && !medicalImage.getAnalysisResults().trim().isEmpty();
    }

    // Метод для получения статуса анализа
    public ImageStatus getImageStatus(Long imageId) {
        MedicalImage medicalImage = medicalImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("MedicalImage not found with id: " + imageId));

        return medicalImage.getStatus();
    }

    // Метод для получения сводки по анализу (для быстрого отображения)
    public String getAnalysisSummary(Long imageId) {
        MedicalImageProcess.MedicalAnalysis analysis = getAnalysisResults(imageId);

        if (analysis == null || !analysis.isSuccess()) {
            return "Анализ не выполнен";
        }

        int highRiskCount = analysis.getHighRisk() != null ? analysis.getHighRisk().size() : 0;
        int mediumRiskCount = analysis.getMediumRisk() != null ? analysis.getMediumRisk().size() : 0;
        int totalFindings = analysis.getTotalFindings() != null ? analysis.getTotalFindings() : 0;

        if (totalFindings == 0) {
            return "Патологий не обнаружено";
        }

        StringBuilder summary = new StringBuilder();
        summary.append("Обнаружено патологий: ").append(totalFindings);

        if (highRiskCount > 0) {
            summary.append(" (🚨 ").append(highRiskCount).append(" критических)");
        } else if (mediumRiskCount > 0) {
            summary.append(" (⚠️ ").append(mediumRiskCount).append(" средних)");
        }

        // Добавляем топ-1 находку
        if (highRiskCount > 0 && analysis.getHighRisk() != null && !analysis.getHighRisk().isEmpty()) {
            String topFinding = analysis.getHighRisk().get(0).getPathology();
            summary.append(" | Основная: ").append(topFinding);
        }

        return summary.toString();
    }

    // Метод для получения краткой статистики анализа (для карточек)
    public AnalysisSummary getAnalysisSummaryObject(Long imageId) {
        MedicalImageProcess.MedicalAnalysis analysis = getAnalysisResults(imageId);

        if (analysis == null || !analysis.isSuccess()) {
            return new AnalysisSummary("Не анализировано", "secondary", 0, 0, 0);
        }

        int highRiskCount = analysis.getHighRisk() != null ? analysis.getHighRisk().size() : 0;
        int mediumRiskCount = analysis.getMediumRisk() != null ? analysis.getMediumRisk().size() : 0;
        int totalFindings = analysis.getTotalFindings() != null ? analysis.getTotalFindings() : 0;

        String statusText;
        String badgeColor;

        if (highRiskCount > 0) {
            statusText = "Критические находки";
            badgeColor = "danger";
        } else if (mediumRiskCount > 0) {
            statusText = "Обнаружены патологии";
            badgeColor = "warning";
        } else if (totalFindings > 0) {
            statusText = "Незначительные находки";
            badgeColor = "info";
        } else {
            statusText = "Без патологий";
            badgeColor = "success";
        }

        return new AnalysisSummary(statusText, badgeColor, totalFindings, highRiskCount, mediumRiskCount);
    }

    // Вспомогательный класс для сводки анализа
    public static class AnalysisSummary {
        private String statusText;
        private String badgeColor; // для Bootstrap классов
        private int totalFindings;
        private int highRiskFindings;
        private int mediumRiskFindings;

        public AnalysisSummary(String statusText, String badgeColor, int totalFindings, int highRiskFindings, int mediumRiskFindings) {
            this.statusText = statusText;
            this.badgeColor = badgeColor;
            this.totalFindings = totalFindings;
            this.highRiskFindings = highRiskFindings;
            this.mediumRiskFindings = mediumRiskFindings;
        }

        // геттеры
        public String getStatusText() { return statusText; }
        public String getBadgeColor() { return badgeColor; }
        public int getTotalFindings() { return totalFindings; }
        public int getHighRiskFindings() { return highRiskFindings; }
        public int getMediumRiskFindings() { return mediumRiskFindings; }
    }

    // Конвертация JSON в объект анализа
    private MedicalImageProcess.MedicalAnalysis convertJsonToAnalysis(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        try {
            return objectMapper.readValue(json, MedicalImageProcess.MedicalAnalysis.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error reading analysis results", e);
        }
    }

    private String createPatientFolder(User patient) throws IOException {
        String folderName = "patient_" + patient.getId();
        Path patientPath = Paths.get(storageBasePath, folderName);
        if (!Files.exists(patientPath)) {
            Files.createDirectories(patientPath);
        }
        return patientPath.toString();
    }

    private String generateUniqueFileName(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }

    public String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "dat";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}