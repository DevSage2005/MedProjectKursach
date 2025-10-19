package ru.kursach.MedProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kursach.MedProject.models.Appointment;
import ru.kursach.MedProject.models.User;

import java.util.Date;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findAllByUser(User user);
    List<Appointment> findAllByDoctorAndDate(User user, Date date);

}
