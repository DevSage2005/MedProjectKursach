package ru.kursach.MedProject.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import ru.kursach.MedProject.enums.Gender;
import ru.kursach.MedProject.enums.Roles;
import ru.kursach.MedProject.enums.Specialization;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name="user")
@Component
public class User {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\-\\s']{2,50}$", message = "ФИО должно содержать только буквы, дефисы и пробелы (2-50 символов)")
    @Column(name="first_name")
    private String firstName; //Имя
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\-\\s']{2,50}$", message = "ФИО должно содержать только буквы, дефисы и пробелы (2-50 символов)")
    @Column(name="last_name")
    private String lastName; //Фамилия
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\-\\s']{2,50}$", message = "ФИО должно содержать только буквы, дефисы и пробелы (2-50 символов)")
    @Column(name="middle_name")
    private String middleName; //Отчество
    @Column(name="date_of_birth")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    @Pattern(regexp = "^\\+375 \\((17|25|29|33|44)\\) [0-9]{3}-[0-9]{2}-[0-9]{2}$",
            message = "Номер телефона должен быть в формате: +375 (33) 650-96-05")
    @Column(name="phone")
    private String phone;
    @Email(message = "Некорректный формат email")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email должен быть в формате example@domain.com")
    @Column(name="email")
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(name="specialization")
    private Specialization specialization;
    @Column(name="experience_years")
    private Integer experienceYears;
    @Size(max = 1000, message = "Превышено допутимое количество символов")
    @Column(name="bio", length = 1000)
    private String bio;
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё0-9\\s\\-\\.,/№]{5,200}$",message = "Адрес должен содержать буквы, цифры, пробелы и основные символы (5-200 символов)")
    @Column(name="address")
    private String address;
    @Column(name="education")
    private String education;
    @Column(name="is_active")
    private boolean isActive=true;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="creation_time")
    private LocalDateTime createdAt;
    @Pattern(regexp = "^[A-Za-z0-9\\-\\s]{5,20}$", message = "Номер лицензии должен содержать буквы, цифры и дефисы (5-20 символов)")
    @Column(name="medical_license_number")
    private String medicalLicenseNumber;
    @Enumerated(EnumType.STRING)
    @Column(name="gender")
    private Gender gender;
    @Column(name="last_login_at")
    private LocalDateTime lastLoginAt;
    @Pattern(regexp = "^.{6,}$",
            message = "Пароль должен содержать минимум 6 символов")
    @Column(name="password")
    private String password;

    @OneToOne(mappedBy = "doctor", cascade = CascadeType.ALL)
    private Schedule schedule;

    @ElementCollection(targetClass = Roles.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name="role_name")
    private Set<Roles> role = new LinkedHashSet<>();


    public Set<Roles> getRole() {
        return role;
    }


    public void addRole(Roles role){
        this.getRole().add(role);
    }

    public void deleteRole(Roles role){
        this.getRole().removeIf(r -> r.name().equals(role.name()));
    }

    public boolean isAdmin(){
        for(Roles r : role){
            if(r==Roles.ROLE_ADMIN)
                return true;
        }
        return false;
    }

    public boolean isSimpleUser(){
        for(Roles r : role){
            if(r==Roles.ROLE_USER)
                return true;
        }
        return false;
    }

    public boolean isDoctor(){
        for(Roles r : role){
            if(r==Roles.ROLE_DOCTOR)
                return true;
        }
        return false;
    }


    public String getAllRoles(){
        StringBuilder roles = new StringBuilder();
        for(Roles role:this.getRole()){

            roles.append(", ").append(role.getTranslation());
        }
        roles.delete(0,1);
        return roles.toString();
    }

    public void setRole(Set<Roles> role) {
        this.role = role;
    }



    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMedicalLicenseNumber() {
        return medicalLicenseNumber;
    }

    public void setMedicalLicenseNumber(String medicalLicenseNumber) {
        this.medicalLicenseNumber = medicalLicenseNumber;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && isActive == user.isActive && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(middleName, user.middleName) && Objects.equals(dateOfBirth, user.dateOfBirth) && Objects.equals(phone, user.phone) && Objects.equals(email, user.email) && specialization == user.specialization && Objects.equals(experienceYears, user.experienceYears) && Objects.equals(bio, user.bio) && Objects.equals(address, user.address) && Objects.equals(education, user.education) && Objects.equals(createdAt, user.createdAt) && Objects.equals(medicalLicenseNumber, user.medicalLicenseNumber) && gender == user.gender && Objects.equals(lastLoginAt, user.lastLoginAt) && Objects.equals(password, user.password) && Objects.equals(role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, middleName, dateOfBirth, phone, email, specialization, experienceYears, bio, address, education, isActive, createdAt, medicalLicenseNumber, gender, lastLoginAt, password, role);
    }
}
