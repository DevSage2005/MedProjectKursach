package ru.kursach.MedProject.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kursach.MedProject.enums.ImagingType;
import ru.kursach.MedProject.enums.MedicalRecordStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medical_record")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medical_card_id")
    private MedicalCard medicalCard;

    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    private User doctor;

    // Жалобы и осмотр
    @Column(name = "symptoms", length = 2000)
    private String symptoms;

    @Column(name = "examination_notes", length = 2000)
    private String examinationNotes;

    @Column(name = "preliminary_diagnosis", length = 1000)
    private String preliminaryDiagnosis;

    @Column(name = "imaging_required")
    private boolean imagingRequired;

    @Enumerated(EnumType.STRING)
    @Column(name = "imaging_type")
    private ImagingType imagingType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_radiologist_id")
    private User assignedRadiologist;

    @Column(name = "clinical_question", length = 1000)
    private String clinicalQuestion;

    @Column(name = "imaging_results", length = 2000)
    private String imagingResults;

    @Column(name = "final_diagnosis", length = 1000)
    private String finalDiagnosis;

    @Column(name = "treatment_plan", length = 2000)
    private String treatmentPlan;

    @Column(name = "prescribed_medication")
    private String prescribedMedication;

    @Column(name = "record_date")
    private LocalDateTime recordDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MedicalRecordStatus status;

    @OneToMany(mappedBy = "medicalRecord", fetch = FetchType.EAGER)
    private List<MedicalImage> medicalImage = new ArrayList<>();

    public void addImage(MedicalImage medicalImage){
        this.medicalImage.add(medicalImage);
    }

}