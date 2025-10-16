package ru.kursach.MedProject.models;

import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;
import ru.kursach.MedProject.enums.DayOfWeek;

import java.time.LocalTime;

@Entity
@Table(name = "working_hours")
public class WorkingHours {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinColumn(name = "schedule_id", referencedColumnName = "id")
    private Schedule schedule;

    @Enumerated(EnumType.STRING)
    @Column(name="day_of_week")
    private DayOfWeek dayOfWeek;
    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "end_time")// 09:00
    private LocalTime endTime;
    @Column(name = "break_start")// 18:00
    private LocalTime breakStart;
    @Column(name = "break_end")// 13:00
    private LocalTime breakEnd;


    public WorkingHours(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, LocalTime breakStart, LocalTime breakEnd) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.breakStart = breakStart;
        this.breakEnd = breakEnd;
    }

    public WorkingHours() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalTime getBreakStart() {
        return breakStart;
    }

    public void setBreakStart(LocalTime breakStart) {
        this.breakStart = breakStart;
    }

    public LocalTime getBreakEnd() {
        return breakEnd;
    }



    public void setBreakEnd(LocalTime breakEnd) {
        this.breakEnd = breakEnd;
    }
}
