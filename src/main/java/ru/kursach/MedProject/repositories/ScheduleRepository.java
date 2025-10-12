package ru.kursach.MedProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kursach.MedProject.models.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
}
