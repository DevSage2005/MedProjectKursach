package ru.kursach.MedProject.enums;

public enum Roles {
    ROLE_ADMIN("Администратор"), ROLE_USER("Пользователь"), ROLE_DOCTOR("Доктор"), ROLE_RADIOLOGIST("Радиологист");

    final String translation;
    Roles(String translation) {
        this.translation=translation;
    }

    public String getTranslation() {
        return translation;
    }
}
