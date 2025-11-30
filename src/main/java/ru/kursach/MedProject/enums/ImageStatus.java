package ru.kursach.MedProject.enums;

public enum ImageStatus {
    UPLOADED("Загружено"), PROCESSED("В обработке"), ANALYZED("Проанализировать"), PROCESSED_AND_ANALYZED("Обработано и проанализировано");

    String translation;
    ImageStatus(String translation) {
        this.translation = translation;
    }

    public String getTranslation() {
        return translation;
    }
}
