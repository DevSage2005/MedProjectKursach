package ru.kursach.MedProject.enums;

public enum Gender {
    MALE("Мужчина"), FEMALE("Женщина");

    String translation;

    Gender(String translation) {
        this.translation = translation;
    }

    public String getTranslation() {
        return translation;
    }
}
