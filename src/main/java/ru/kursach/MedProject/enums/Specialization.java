package ru.kursach.MedProject.enums;

public enum Specialization {
    PULMONOLOGY("Пульмонология", "Диагностика заболеваний легких"),
    DERMATOLOGY("Дерматология", "Диагностика заболеваний кожи"),
    OPHTHALMOLOGY("Офтальмология", "Диагностика заболеваний глаз"),
    ONCOLOGY("Онкология", "Диагностика онкологических заболеваний"),
    CARDIOLOGY("Кардиология", "Диагностика заболеваний сердца"),
    NEUROLOGY("Неврология", "Диагностика заболеваний нервной системы");

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
