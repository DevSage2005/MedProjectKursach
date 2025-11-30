package ru.kursach.MedProject.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kursach.MedProject.enums.ImageStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
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

    @Column(name = "analysis_results") // Для хранения JSON
    private String analysisResults;


    public MedicalImage() {
        this.uploadedAt = LocalDateTime.now();
        this.status = ImageStatus.UPLOADED;
    }




}