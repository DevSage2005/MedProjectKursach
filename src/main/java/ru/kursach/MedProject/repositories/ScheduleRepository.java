package ru.kursach.MedProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kursach.MedProject.models.Schedule;
import ru.kursach.MedProject.models.User;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    Schedule findByDoctor(User doctor);
}
