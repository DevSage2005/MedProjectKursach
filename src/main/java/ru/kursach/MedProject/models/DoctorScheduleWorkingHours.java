package ru.kursach.MedProject.models;

import java.util.ArrayList;
import java.util.List;

public class DoctorScheduleWorkingHours {
    private User user;
    private Schedule schedule;
    private List<WorkingHours> workingHours ;




    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public List<WorkingHours> getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(List<WorkingHours> workingHours) {
        this.workingHours = workingHours;
    }
}
