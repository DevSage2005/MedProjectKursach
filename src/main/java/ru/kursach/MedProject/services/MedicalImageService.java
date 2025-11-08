package ru.kursach.MedProject.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
import java.util.Base64;
import java.util.UUID;

@Service
public class MedicalImageService {

    private final MedicalImageRepository medicalImageRepository;

    @Value("${medical.images.storage.path:./medical-images}")
    private String storageBasePath;

    public MedicalImageService(MedicalImageRepository medicalImageRepository) {
        this.medicalImageRepository = medicalImageRepository;
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
        return medicalImage;
    }

    @Transactional
    public void saveProcessedMedicalImage(MedicalImageProcess medicalImageProcess) throws IOException {
        MedicalImage medicalImage = medicalImageRepository.findById(medicalImageProcess.getId()).get();

        String folderName = "processedImage";
        Path processedImagePath = Paths.get(Paths.get(medicalImage.getFilePath()).getParent().toString(), folderName);
        if (!Files.exists(processedImagePath)) {
            Files.createDirectories(processedImagePath);
        }

        // Используем фиксированное имя файла вместо уникального
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

        medicalImage.setProcessedFilePath(stringFilePath);
        medicalImageRepository.save(medicalImage);
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