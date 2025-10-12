package ru.kursach.MedProject.enums;

public enum DayOfWeek {
    MONDAY("Понедельник"), TUESDAY("Вторник"), WEDNESDAY("Среда"), THURSDAY("Четверг"), FRIDAY("Пятница"), SATURDAY("Суббота"), SUNDAY("Воскресенье");

    private String translation;
    DayOfWeek(String translation) {
        this.translation = translation;
    }

    public String getTranslation() {
        return translation;
    }
}