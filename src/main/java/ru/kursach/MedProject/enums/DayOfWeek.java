package ru.kursach.MedProject.enums;

public enum DayOfWeek {
    SUNDAY("Воскресенье"), MONDAY("Понедельник"), TUESDAY("Вторник"), WEDNESDAY("Среда"), THURSDAY("Четверг"), FRIDAY("Пятница"), SATURDAY("Суббота");

    private String translation;
    DayOfWeek(String translation) {
        this.translation = translation;
    }

    public String getTranslation() {
        return translation;
    }
}