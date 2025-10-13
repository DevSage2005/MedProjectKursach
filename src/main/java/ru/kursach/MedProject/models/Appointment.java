package ru.kursach.MedProject.models;

import jakarta.persistence.*;
import ru.kursach.MedProject.enums.BookStatus;

import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name="appointment")
public class Appointment {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Temporal(TemporalType.DATE)
    @Column(name="date")
    private Date date;

    @Column(name="slot")
    private LocalTime slot;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private BookStatus status=BookStatus.SCHEDULED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    private User doctor;


    public Appointment(Date date, LocalTime slot, BookStatus status, User user, User doctor) {
        this.date = date;
        this.slot = slot;
        this.status = status;
        this.user = user;
        this.doctor = doctor;
    }

    public Appointment() {

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public LocalTime getSlot() {
        return slot;
    }

    public void setSlot(LocalTime slot) {
        this.slot = slot;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }
}
