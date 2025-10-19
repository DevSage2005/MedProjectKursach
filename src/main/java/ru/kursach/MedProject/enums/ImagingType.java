package ru.kursach.MedProject.enums;

public enum ImagingType {
    CHEST_XRAY("Рентген грудной клетки"),
    ABDOMINAL_XRAY("Рентген брюшной полости"),
    EXTREMITY_XRAY("Рентген конечностей"),
    DENTAL_XRAY("Стоматологический рентген"),
    OTHER("Другое");

    private final String description;

    ImagingType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}