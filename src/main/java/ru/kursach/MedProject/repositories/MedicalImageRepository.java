package ru.kursach.MedProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kursach.MedProject.models.MedicalImage;

public interface MedicalImageRepository extends JpaRepository<MedicalImage, Long> {

}
