package ru.kursach.MedProject.enums;

public enum ImageStatus {
    UPLOADED("Загружено"), PROCESSED("В обработке"), ANALYZED("Проанализировать");

    String translation;
    ImageStatus(String translation) {
        this.translation = translation;
    }

    public String getTranslation() {
        return translation;
    }
}
