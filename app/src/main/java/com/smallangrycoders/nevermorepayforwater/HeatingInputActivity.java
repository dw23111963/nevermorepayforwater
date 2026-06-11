package com.smallangrycoders.nevermorepayforwater;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HeatingInputActivity extends AppCompatActivity {

    private EditText etRadiatorTemp, etWaterTemp, etVolume;
    private Button btnCalculate, btnSave;
    private TextView tvResult;
    private long cityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heating_input);

        cityId = getIntent().getLongExtra("city_id", -1);

        etRadiatorTemp = findViewById(R.id.et_radiator_temp);
        etWaterTemp = findViewById(R.id.et_water_temp);
        etVolume = findViewById(R.id.et_volume);
        btnCalculate = findViewById(R.id.btn_calculate);
        btnSave = findViewById(R.id.btn_save);
        tvResult = findViewById(R.id.tv_result);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateHeat();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveHeatData();
            }
        });
    }

    private void calculateHeat() {
        try {
            double radiatorTemp = Double.parseDouble(etRadiatorTemp.getText().toString());
            double waterTemp = Double.parseDouble(etWaterTemp.getText().toString());
            double volume = Double.parseDouble(etVolume.getText().toString());

            double heatLoss = (radiatorTemp - waterTemp) * (volume * 1000);
            tvResult.setText(String.format("Потеряно тепла: %.0f ккал", heatLoss));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Введите все числа корректно", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveHeatData() {
        Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
        finish();
    }
}