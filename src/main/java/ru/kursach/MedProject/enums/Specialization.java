package ru.kursach.MedProject.enums;

public enum Specialization {
    PULMONOLOGY("Пульмонология", "Диагностика заболеваний легких"),
    CARDIOLOGY("Кардиология", "Диагностика заболеваний сердца");

    private final String name;
    private final String description;

    Specialization(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
