package com.smallangrycoders.nevermorepayforwater;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class DBCities {
    private static final String DATABASE_NAME = "cities.db";
    private static final int DATABASE_VERSION = 2; // 👈 УВЕЛИЧЕН ВЕРСИЯ (для обновления)
    private static final String TABLE_NAME = "cities";
    private static final String TABLE_HEAT_HISTORY = "heat_history"; // 👈 НОВАЯ ТАБЛИЦА

    // Колонки для таблицы cities
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TEMPR = "tempr";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_LON = "lon";
    private static final String COLUMN_FLAG2 = "flag2";
    private static final String COLUMN_SYNCDATE = "syncdate";

    // 👇 НОВЫЕ КОЛОНКИ ДЛЯ ОТОПЛЕНИЯ (добавим в cities)
    private static final String COLUMN_DAILY_WATER_VOLUME = "daily_water_volume";
    private static final String COLUMN_RADIATOR_TEMP = "radiator_temp";
    private static final String COLUMN_WATER_SOURCE_TEMP = "water_source_temp";

    // Колонки для таблицы heat_history
    private static final String COLUMN_HISTORY_ID = "id";
    private static final String COLUMN_CITY_ID = "city_id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_RADIATOR_TEMP_HIST = "radiator_temp";
    private static final String COLUMN_WATER_SOURCE_TEMP_HIST = "water_source_temp";
    private static final String COLUMN_HEAT_LOSS_KCAL = "heat_loss_kcal";

    private SQLiteDatabase stcDataBase;

    public DBCities(Context context) {
        OpenHelper mOpenHelper = new OpenHelper(context);
        stcDataBase = mOpenHelper.getWritableDatabase();
    }

    // Существующие методы
    public void insert(String name, String temp, String lat, String lon, int flag, LocalDateTime syncDate) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_TEMPR, temp);
        cv.put(COLUMN_LAT, lat);
        cv.put(COLUMN_LON, lon);
        cv.put(COLUMN_FLAG2, flag);
        cv.put(COLUMN_SYNCDATE, String.valueOf(syncDate));
        cv.put(COLUMN_DAILY_WATER_VOLUME, 0.0);  // дефолт
        cv.put(COLUMN_RADIATOR_TEMP, 20.0);      // дефолт
        cv.put(COLUMN_WATER_SOURCE_TEMP, 5.0);   // дефолт
        stcDataBase.insert(TABLE_NAME, null, cv);
    }

    public int update(StCity stc) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, stc.getName());
        cv.put(COLUMN_TEMPR, stc.getTemp());
        cv.put(COLUMN_LAT, stc.getStrLat());
        cv.put(COLUMN_LON, stc.getStrLon());
        cv.put(COLUMN_FLAG2, stc.getFlagResource());
        cv.put(COLUMN_SYNCDATE, stc.getSyncDate().toString());
        cv.put(COLUMN_DAILY_WATER_VOLUME, stc.getDailyWaterVolume());
        cv.put(COLUMN_RADIATOR_TEMP, stc.getRadiatorTemp());
        cv.put(COLUMN_WATER_SOURCE_TEMP, stc.getWaterSourceTemp());

        return stcDataBase.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(stc.getId())});
    }

    public void deleteAll() {
        stcDataBase.delete(TABLE_NAME, null, null);
        stcDataBase.delete(TABLE_HEAT_HISTORY, null, null);
    }

    public ArrayList<StCity> selectAll() {
        Cursor mCursor = stcDataBase.query(TABLE_NAME, null, null, null, null, null, null);
        ArrayList<StCity> arr = new ArrayList<>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(mCursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_NAME));
                String temp = mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_TEMPR));
                String lat = mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_LAT));
                String lon = mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_LON));
                int flag = mCursor.getInt(mCursor.getColumnIndexOrThrow(COLUMN_FLAG2));
                LocalDateTime syncDate = null;
                String syncDateStr = mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_SYNCDATE));
                if (!Objects.equals(syncDateStr, "null")) {
                    syncDate = LocalDateTime.parse(syncDateStr);
                }

                StCity city = new StCity(id, name, temp, lat, lon, flag, syncDate);

                // Загружаем данные отопления
                int waterVolumeCol = mCursor.getColumnIndex(COLUMN_DAILY_WATER_VOLUME);
                int radiatorTempCol = mCursor.getColumnIndex(COLUMN_RADIATOR_TEMP);
                int waterSourceTempCol = mCursor.getColumnIndex(COLUMN_WATER_SOURCE_TEMP);

                if (waterVolumeCol != -1) {
                    city.setDailyWaterVolume(mCursor.getDouble(waterVolumeCol));
                }
                if (radiatorTempCol != -1) {
                    city.setRadiatorTemp(mCursor.getDouble(radiatorTempCol));
                }
                if (waterSourceTempCol != -1) {
                    city.setWaterSourceTemp(mCursor.getDouble(waterSourceTempCol));
                }

                // Загружаем историю отопления
                city.setHeatHistory(getHeatHistory(id));

                arr.add(city);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return arr;
    }

    // 👇 НОВЫЙ МЕТОД: поиск города по ID
    public StCity selectById(long id) {
        Cursor mCursor = stcDataBase.query(TABLE_NAME, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (mCursor != null && mCursor.moveToFirst()) {
            String name = mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_NAME));
            String temp = mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_TEMPR));
            String lat = mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_LAT));
            String lon = mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_LON));
            int flag = mCursor.getInt(mCursor.getColumnIndexOrThrow(COLUMN_FLAG2));
            LocalDateTime syncDate = null;
            String syncDateStr = mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_SYNCDATE));
            if (!Objects.equals(syncDateStr, "null")) {
                syncDate = LocalDateTime.parse(syncDateStr);
            }

            StCity city = new StCity(id, name, temp, lat, lon, flag, syncDate);

            int waterVolumeCol = mCursor.getColumnIndex(COLUMN_DAILY_WATER_VOLUME);
            int radiatorTempCol = mCursor.getColumnIndex(COLUMN_RADIATOR_TEMP);
            int waterSourceTempCol = mCursor.getColumnIndex(COLUMN_WATER_SOURCE_TEMP);

            if (waterVolumeCol != -1) {
                city.setDailyWaterVolume(mCursor.getDouble(waterVolumeCol));
            }
            if (radiatorTempCol != -1) {
                city.setRadiatorTemp(mCursor.getDouble(radiatorTempCol));
            }
            if (waterSourceTempCol != -1) {
                city.setWaterSourceTemp(mCursor.getDouble(waterSourceTempCol));
            }

            city.setHeatHistory(getHeatHistory(id));
            mCursor.close();
            return city;
        }
        return null;
    }

    // 👇 НОВЫЙ МЕТОД: обновление объема воды
    public void updateWaterVolume(long id, double volume) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DAILY_WATER_VOLUME, volume);
        stcDataBase.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    // 👇 НОВЫЙ МЕТОД: обновление температуры батареи
    public void updateRadiatorTemp(long id, double temp) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RADIATOR_TEMP, temp);
        stcDataBase.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    // 👇 НОВЫЙ МЕТОД: сохранение записи об отоплении
    public void saveHeatRecord(long cityId, DailyHeatData record) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CITY_ID, cityId);
        cv.put(COLUMN_DATE, record.getDate().toString());
        cv.put(COLUMN_RADIATOR_TEMP_HIST, record.getRadiatorTemp());
        cv.put(COLUMN_WATER_SOURCE_TEMP_HIST, record.getWaterSourceTemp());
        cv.put(COLUMN_HEAT_LOSS_KCAL, record.getHeatLossKcal());
        stcDataBase.insert(TABLE_HEAT_HISTORY, null, cv);
    }

    // 👇 НОВЫЙ МЕТОД: получение истории отопления
    public ArrayList<DailyHeatData> getHeatHistory(long cityId) {
        ArrayList<DailyHeatData> history = new ArrayList<>();
        Cursor mCursor = stcDataBase.query(TABLE_HEAT_HISTORY, null,
                COLUMN_CITY_ID + " = ?", new String[]{String.valueOf(cityId)},
                null, null, COLUMN_DATE + " DESC");

        if (mCursor != null && mCursor.moveToFirst()) {
            do {
                LocalDate date = LocalDate.parse(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_DATE)));
                double radiatorTemp = mCursor.getDouble(mCursor.getColumnIndexOrThrow(COLUMN_RADIATOR_TEMP_HIST));
                double waterSourceTemp = mCursor.getDouble(mCursor.getColumnIndexOrThrow(COLUMN_WATER_SOURCE_TEMP_HIST));
                double heatLossKcal = mCursor.getDouble(mCursor.getColumnIndexOrThrow(COLUMN_HEAT_LOSS_KCAL));

                history.add(new DailyHeatData(date, radiatorTemp, waterSourceTemp, heatLossKcal));
            } while (mCursor.moveToNext());
            mCursor.close();
        }
        return history;
    }

    // 👇 НОВЫЙ МЕТОД: получение суммы теплопотерь за месяц
    public double getMonthlyHeatTotal(long cityId, int year, int month) {
        String datePrefix = year + "-" + String.format("%02d", month);
        String query = "SELECT SUM(" + COLUMN_HEAT_LOSS_KCAL + ") FROM " + TABLE_HEAT_HISTORY +
                " WHERE " + COLUMN_CITY_ID + " = ? AND " + COLUMN_DATE + " LIKE ?";

        Cursor cursor = stcDataBase.rawQuery(query, new String[]{String.valueOf(cityId), datePrefix + "%"});
        double total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getDouble(0);
            cursor.close();
        }
        return total;
    }

    // 👇 ОБНОВЛЕННЫЙ OpenHelper
    private class OpenHelper extends SQLiteOpenHelper {
        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // Таблица городов с новыми колонками
            String query = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_TEMPR + " TEXT, " +
                    COLUMN_LAT + " TEXT, " +
                    COLUMN_LON + " TEXT, " +
                    COLUMN_FLAG2 + " INT, " +
                    COLUMN_SYNCDATE + " TEXT, " +
                    COLUMN_DAILY_WATER_VOLUME + " REAL DEFAULT 0, " +
                    COLUMN_RADIATOR_TEMP + " REAL DEFAULT 20, " +
                    COLUMN_WATER_SOURCE_TEMP + " REAL DEFAULT 5);";
            db.execSQL(query);

            // Таблица истории отопления
            String heatHistoryQuery = "CREATE TABLE " + TABLE_HEAT_HISTORY + " (" +
                    COLUMN_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CITY_ID + " INTEGER, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_RADIATOR_TEMP_HIST + " REAL, " +
                    COLUMN_WATER_SOURCE_TEMP_HIST + " REAL, " +
                    COLUMN_HEAT_LOSS_KCAL + " REAL, " +
                    "FOREIGN KEY(" + COLUMN_CITY_ID + ") REFERENCES " + TABLE_NAME + "(" + COLUMN_ID + "));";
            db.execSQL(heatHistoryQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEAT_HISTORY);
            onCreate(db);
        }
    }
}