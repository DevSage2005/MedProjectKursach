package ru.kursach.MedProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kursach.MedProject.enums.MedicalRecordStatus;
import ru.kursach.MedProject.models.MedicalCard;
import ru.kursach.MedProject.models.MedicalRecord;
import ru.kursach.MedProject.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {
    List<MedicalRecord> findAllByStatusAndAssignedRadiologist(MedicalRecordStatus medicalRecordStatus, User radiologist);
    List<MedicalRecord> findByStatusAndDoctor(MedicalRecordStatus medicalRecordStatus, User doctor);
    List<MedicalRecord> findAllByMedicalCard(MedicalCard medicalCard);
    Page<MedicalRecord> findByMedicalCardOrderByCompletionDateDesc(MedicalCard medicalCard, Pageable pageable);

}
