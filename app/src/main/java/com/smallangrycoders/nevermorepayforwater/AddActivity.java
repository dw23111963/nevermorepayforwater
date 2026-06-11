package com.smallangrycoders.nevermorepayforwater;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddActivity extends Activity {
    private static final String TAG = "AddActivity";
    private Button btSave, btCancel, btFindCoords;
    private EditText etLoc, etLat, etLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        btSave = findViewById(R.id.butSave);
        btCancel = findViewById(R.id.butCancel);
        btFindCoords = findViewById(R.id.butFindCoords);
        etLoc = findViewById(R.id.City);
        etLat = findViewById(R.id.etLat);
        etLon = findViewById(R.id.etLon);

        btSave.setOnClickListener(v -> {
            if (etLoc.getText().toString().isEmpty()) {
                Toast.makeText(AddActivity.this, "Введите название города", Toast.LENGTH_SHORT).show();
                return;
            }

            StCity stcity = new StCity(-1,
                    etLoc.getText().toString(),
                    "0",
                    etLat.getText().toString(),
                    etLon.getText().toString(),
                    1,
                    LocalDateTime.now());

            Intent intent = getIntent();
            intent.putExtra("StCity", stcity);
            setResult(RESULT_OK, intent);
            finish();
        });

        btCancel.setOnClickListener(v -> finish());

        btFindCoords.setOnClickListener(v -> {
            String cityName = etLoc.getText().toString().trim();
            if (!cityName.isEmpty()) {
                Toast.makeText(AddActivity.this, "Поиск...", Toast.LENGTH_SHORT).show();
                getCoordinatesFromName(cityName);
            } else {
                Toast.makeText(AddActivity.this, "Введите название города", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCoordinatesFromName(String cityName) {
        try {
            // Кодируем русские буквы
            String encodedCityName = URLEncoder.encode(cityName, "UTF-8");

            // API с поддержкой русского языка
            String geocodeUrl = "https://nominatim.openstreetmap.org/search?q=" +
                    encodedCityName +
                    "&format=json&limit=1&accept-language=ru";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(geocodeUrl)
                    .header("User-Agent", "NevermorePayForWater/1.0")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();

                    if (response.isSuccessful()) {
                        try {
                            JSONArray arr = new JSONArray(json);

                            if (arr.length() > 0) {
                                JSONObject obj = arr.getJSONObject(0);
                                String lat = obj.getString("lat");
                                String lon = obj.getString("lon");
                                String displayName = obj.getString("display_name");

                                runOnUiThread(() -> {
                                    etLat.setText(lat);
                                    etLon.setText(lon);
                                    Toast.makeText(AddActivity.this,
                                            "Найдено: " + displayName, Toast.LENGTH_LONG).show();
                                });
                            } else {
                                runOnUiThread(() ->
                                        Toast.makeText(AddActivity.this,
                                                "Город не найден. Попробуйте на английском",
                                                Toast.LENGTH_SHORT).show());
                            }
                        } catch (JSONException e) {
                            runOnUiThread(() ->
                                    Toast.makeText(AddActivity.this, "Ошибка", Toast.LENGTH_SHORT).show());
                        }
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(AddActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show());
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}