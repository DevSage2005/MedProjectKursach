package ru.kursach.MedProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kursach.MedProject.enums.Roles;
import ru.kursach.MedProject.enums.Specialization;
import ru.kursach.MedProject.models.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String name);
    User findUserById(int id);
    List<User> findByRole(Roles role);
    void deleteUserByRole(Roles role);
    List<User> findUserByRoleAndSpecialization(Roles role, Specialization specialization);
}
