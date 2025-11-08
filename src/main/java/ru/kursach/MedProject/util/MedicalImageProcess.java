package ru.kursach.MedProject.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalImageProcess {
    private Long id;
    private String encodedOriginalImage;
    private String encodedProcessedImage;
    private double contrast;
    private int brightness;
    private double sharpness;
    private boolean grayscale;
    private boolean enhance;
    private String extension;
    }
