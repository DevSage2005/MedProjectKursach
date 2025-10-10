package ru.kursach.MedProject.models;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import ru.kursach.MedProject.enums.Roles;

import java.util.*;

@Entity
@Table(name="user")
@Component
public class User {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="name")
    private String name;
    @Column(name="password")
    private String password;
    @Column(name="date_of_birth")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

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

    public User(String name, String password, Date dateOfBirth) {
        this.name = name;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return id == user.id && Objects.equals(name, user.name) && Objects.equals(password, user.password) && Objects.equals(dateOfBirth, user.dateOfBirth) && Objects.equals(role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, password, dateOfBirth, role);
    }
}
