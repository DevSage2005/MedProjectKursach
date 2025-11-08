package ru.kursach.MedProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kursach.MedProject.models.MedicalCard;

public interface MedicalCardRepository extends JpaRepository<MedicalCard, Integer> {
}
