package ru.kursach.MedProject.models;

import jakarta.persistence.*;
import ru.kursach.MedProject.enums.ImageStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_image")
public class MedicalImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medical_record_id")
    private MedicalRecord medicalRecord;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "file_path")
    private String filePath; // путь к оригинальному снимку

    @Column(name = "processed_file_path")
    private String processedFilePath; // путь к обработанному снимку

    @Column(name = "file_size")
    private Long fileSize;

    // Статус
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ImageStatus status; // "UPLOADED", "PROCESSED", "ANALYZED"

    // Даты
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    // Конструкторы
    public MedicalImage() {
        this.uploadedAt = LocalDateTime.now();
        this.status = ImageStatus.UPLOADED;
    }

    public MedicalImage(MedicalRecord medicalRecord,
                        String originalFileName, String filePath, Long fileSize) {
        this();
        this.medicalRecord = medicalRecord;
        this.originalFileName = originalFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MedicalRecord getMedicalRecord() { return medicalRecord; }
    public void setMedicalRecord(MedicalRecord medicalRecord) { this.medicalRecord = medicalRecord; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getProcessedFilePath() { return processedFilePath; }
    public void setProcessedFilePath(String processedFilePath) { this.processedFilePath = processedFilePath; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public ImageStatus getStatus() { return status; }
    public void setStatus(ImageStatus status) { this.status = status; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

}