package ru.kursach.MedProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kursach.MedProject.enums.MedicalRecordStatus;
import ru.kursach.MedProject.models.MedicalRecord;
import ru.kursach.MedProject.models.User;

import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {
    List<MedicalRecord> findAllByStatusAndAssignedRadiologist(MedicalRecordStatus medicalRecordStatus, User radiologist);
}
