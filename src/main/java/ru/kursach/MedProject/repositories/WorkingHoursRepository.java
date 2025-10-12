package ru.kursach.MedProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kursach.MedProject.models.WorkingHours;

public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Integer> {
}
