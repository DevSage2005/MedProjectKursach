package ru.kursach.MedProject.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    private boolean analyze; // Новое поле: запросить AI анализ
    private MedicalAnalysis analysis; // Новое поле: результаты AI анализа

    // Вложенный класс для результатов анализа
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicalAnalysis {
        private boolean success;
        private List<PathologyFinding> highRisk;
        private List<PathologyFinding> mediumRisk;
        private List<PathologyFinding> lowRisk;
        private Integer totalFindings;
        private Integer criticalFindings;

        public boolean hasHighRiskFindings() {
            return highRisk != null && !highRisk.isEmpty();
        }

        public boolean hasFindings() {
            return totalFindings != null && totalFindings > 0;
        }
    }

    // Класс для представления находки патологии
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PathologyFinding {
        private String pathology; // Название патологии на русском
        private Double probability; // Вероятность (0.0 - 1.0)
        private String confidence; // Доверительный интервал в процентах
        private String riskLevel; // Уровень риска: high, medium, low

        // Удобный конструктор
        public PathologyFinding(String pathology, Double probability, String riskLevel) {
            this.pathology = pathology;
            this.probability = probability;
            this.confidence = String.format("%.1f%%", probability * 100);
            this.riskLevel = riskLevel;
        }
    }
}