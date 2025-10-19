package ru.kursach.MedProject.models;

import jakarta.persistence.*;
import ru.kursach.MedProject.enums.ImagingType;
import ru.kursach.MedProject.enums.MedicalRecordStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_record")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_card_id")
    private MedicalCard medicalCard;

    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
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

    @ManyToOne(fetch = FetchType.LAZY)
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

    public MedicalRecord() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MedicalCard getMedicalCard() {
        return medicalCard;
    }

    public void setMedicalCard(MedicalCard medicalCard) {
        this.medicalCard = medicalCard;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getExaminationNotes() {
        return examinationNotes;
    }

    public void setExaminationNotes(String examinationNotes) {
        this.examinationNotes = examinationNotes;
    }

    public String getPreliminaryDiagnosis() {
        return preliminaryDiagnosis;
    }

    public void setPreliminaryDiagnosis(String preliminaryDiagnosis) {
        this.preliminaryDiagnosis = preliminaryDiagnosis;
    }

    public boolean isImagingRequired() {
        return imagingRequired;
    }

    public void setImagingRequired(boolean imagingRequired) {
        this.imagingRequired = imagingRequired;
    }

    public ImagingType getImagingType() {
        return imagingType;
    }

    public void setImagingType(ImagingType imagingType) {
        this.imagingType = imagingType;
    }

    public User getAssignedRadiologist() {
        return assignedRadiologist;
    }

    public void setAssignedRadiologist(User assignedRadiologist) {
        this.assignedRadiologist = assignedRadiologist;
    }

    public String getClinicalQuestion() {
        return clinicalQuestion;
    }

    public void setClinicalQuestion(String clinicalQuestion) {
        this.clinicalQuestion = clinicalQuestion;
    }

    public String getImagingResults() {
        return imagingResults;
    }

    public void setImagingResults(String imagingResults) {
        this.imagingResults = imagingResults;
    }

    public String getFinalDiagnosis() {
        return finalDiagnosis;
    }

    public void setFinalDiagnosis(String finalDiagnosis) {
        this.finalDiagnosis = finalDiagnosis;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public String getPrescribedMedication() {
        return prescribedMedication;
    }

    public void setPrescribedMedication(String prescribedMedication) {
        this.prescribedMedication = prescribedMedication;
    }

    public LocalDateTime getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDateTime recordDate) {
        this.recordDate = recordDate;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public MedicalRecordStatus getStatus() {
        return status;
    }

    public void setStatus(MedicalRecordStatus status) {
        this.status = status;
    }
}