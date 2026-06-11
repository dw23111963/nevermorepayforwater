package com.smallangrycoders.nevermorepayforwater;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;  // 👈 ДОБАВИТЬ ЭТОТ ИМПОРТ

public class StCity implements Serializable {
    private long id;
    private String name;
    private String temp;
    private int flagResource;
    private String lat;
    private String lon;
    private LocalDateTime syncDate;

    // 👇 НОВЫЕ ПОЛЯ ДЛЯ ОТОПЛЕНИЯ
    private double dailyWaterVolume;   // объем воды за день (м³)
    private double radiatorTemp;        // температура батареи
    private double waterSourceTemp;     // температура водоема (из API погоды)
    private ArrayList<DailyHeatData> heatHistory; // история отопления

    // Getters существующие
    public String getName() {
        return this.name;
    }

    public String getTemp() {
        return this.temp;
    }

    public String getStrLat() {
        return String.valueOf(this.lat);
    }

    public String getStrLon() {
        return String.valueOf(this.lon);
    }

    public int getFlagResource() {
        return this.flagResource;
    }

    public LocalDateTime getSyncDate() {
        return this.syncDate;
    }

    // Setters существующие
    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setStrLat(String strLat) {
        this.lat = strLat;
    }

    public void setStrLon(String strLon) {
        this.lon = strLon;
    }

    public void setFlagResource(int flagResource) {
        this.flagResource = flagResource;
    }

    public void setSyncDate(LocalDateTime syncDate) {
        this.syncDate = syncDate;
    }

    // Существующий конструктор
    public StCity(long id, String name, String temp, String lat, String lon, int flag, LocalDateTime syncDate) {
        setId(id);
        setName(name);
        setTemp(temp);
        setFlagResource(flag);
        setSyncDate(syncDate);
        setStrLat(lat);
        setStrLon(lon);

        // 👇 ИНИЦИАЛИЗАЦИЯ НОВЫХ ПОЛЕЙ
        this.dailyWaterVolume = 0.0;
        this.radiatorTemp = 20.0;
        this.waterSourceTemp = 5.0;
        this.heatHistory = new ArrayList<>();
    }

    public long getId() {
        return this.id;
    }

    // 👇 НОВЫЕ ГЕТТЕРЫ И СЕТТЕРЫ ДЛЯ ОТОПЛЕНИЯ

    public double getDailyWaterVolume() {
        return dailyWaterVolume;
    }

    public void setDailyWaterVolume(double volume) {
        this.dailyWaterVolume = volume;
    }

    public double getRadiatorTemp() {
        return radiatorTemp;
    }

    public void setRadiatorTemp(double temp) {
        this.radiatorTemp = temp;
    }

    public double getWaterSourceTemp() {
        return waterSourceTemp;
    }

    public void setWaterSourceTemp(double temp) {
        this.waterSourceTemp = temp;
    }

    public ArrayList<DailyHeatData> getHeatHistory() {
        return heatHistory;
    }

    public void setHeatHistory(ArrayList<DailyHeatData> history) {
        this.heatHistory = history;
    }

    public void addHeatRecord(DailyHeatData record) {
        if (this.heatHistory == null) {
            this.heatHistory = new ArrayList<>();
        }
        this.heatHistory.add(record);
    }
}