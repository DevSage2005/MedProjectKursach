package ru.kursach.MedProject.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medical_card")
public class MedicalCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "blood_type")
    private String bloodType; // Группа крови

    @Column(name = "allergies", length = 1000)
    private String allergies; // Аллергии

    @Column(name = "chronic_diseases", length = 1000)
    private String chronicDiseases;

    @Column(name = "medical_history", length = 2000)
    private String medicalHistory;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @OneToMany(mappedBy = "medicalCard", cascade = CascadeType.ALL)
    private List<MedicalRecord> medicalRecords = new ArrayList<>();

    public MedicalCard(User user, String bloodType, String allergies, String chronicDiseases, String currentMedications, String medicalHistory, LocalDateTime createdDate, LocalDateTime lastUpdated, List<MedicalRecord> medicalRecords) {
        this.user = user;
        this.bloodType = bloodType;
        this.allergies = allergies;
        this.chronicDiseases = chronicDiseases;
        this.medicalHistory = medicalHistory;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
        this.medicalRecords = medicalRecords;
    }

    public void addRecord(MedicalRecord record){
        medicalRecords.add(record);
    }

    public MedicalCard() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getChronicDiseases() {
        return chronicDiseases;
    }

    public void setChronicDiseases(String chronicDiseases) {
        this.chronicDiseases = chronicDiseases;
    }


    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public void setMedicalRecords(List<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

}
