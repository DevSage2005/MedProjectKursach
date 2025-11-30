package ru.kursach.MedProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kursach.MedProject.models.MedicalCard;
import ru.kursach.MedProject.models.User;

public interface MedicalCardRepository extends JpaRepository<MedicalCard, Integer> {
    MedicalCard findByUser(User user);
}
