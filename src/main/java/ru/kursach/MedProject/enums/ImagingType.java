package ru.kursach.MedProject.enums;

public enum ImagingType {
    CHEST_XRAY("Рентген грудной клетки");

    private final String description;

    ImagingType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}