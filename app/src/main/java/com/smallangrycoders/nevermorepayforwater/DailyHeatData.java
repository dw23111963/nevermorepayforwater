package com.smallangrycoders.nevermorepayforwater;

import java.time.LocalDate;

public class DailyHeatData {
    private LocalDate date;
    private double radiatorTemp;
    private double waterSourceTemp;
    private double heatLossKcal;

    public DailyHeatData(LocalDate date, double radiatorTemp, double waterSourceTemp, double heatLossKcal) {
        this.date = date;
        this.radiatorTemp = radiatorTemp;
        this.waterSourceTemp = waterSourceTemp;
        this.heatLossKcal = heatLossKcal;
    }

    public LocalDate getDate() { return date; }
    public double getRadiatorTemp() { return radiatorTemp; }
    public double getWaterSourceTemp() { return waterSourceTemp; }
    public double getHeatLossKcal() { return heatLossKcal; }
}
