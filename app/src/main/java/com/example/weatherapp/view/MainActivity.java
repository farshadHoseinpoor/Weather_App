package com.example.weatherapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherapp.ApiService;
import com.example.weatherapp.R;
import com.example.weatherapp.model.WeatherInfo;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements ApiService.WeatherInfoReceived {
    private ApiService apiService;
    private TextView txtWeatherName;
    private TextView txtDescription;
    private TextView txtTemperature;
    private TextView txtMinTemperature;
    private TextView txtMaxTemperature;
    private TextView txtPressure;
    private TextView txtHumidity;
    private TextView txtWindSpeed;
    private TextView txtWindDegree;
    private TextView txtCountry;
    private TextView txtCity;
    private ProgressBar progressBar;
    private ConnectivityListener connectivityListener;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiService = new ApiService(this);
        initViews();
        Button btnSendRequest = findViewById(R.id.btn_sendRequest);
        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiService.getTehranWeather(MainActivity.this);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectivityListener = new ConnectivityListener();
        registerReceiver(connectivityListener, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(connectivityListener);
    }

    private void initViews() {
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        progressBar = findViewById(R.id.progressBar);
        txtWeatherName = findViewById(R.id.txt_weatherName);
        txtDescription = findViewById(R.id.txt_description);
        txtTemperature = findViewById(R.id.txt_temperature);
        txtMinTemperature = findViewById(R.id.txt_minTemperature);
        txtMaxTemperature = findViewById(R.id.txt_maxTemperature);
        txtPressure = findViewById(R.id.txt_pressure);
        txtHumidity = findViewById(R.id.txt_humidity);
        txtWindSpeed = findViewById(R.id.txt_windSpeed);
        txtWindDegree = findViewById(R.id.txt_windDegree);
        txtCountry = findViewById(R.id.txt_country);
        txtCity = findViewById(R.id.txt_city);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onReceived(WeatherInfo weatherInfo) {
        if (weatherInfo != null) {
            //show information to user
            progressBar.setVisibility(View.INVISIBLE);
            txtWeatherName.setText("آب و هوای فعلی: " + weatherInfo.getName());
            txtDescription.setText("توضیحات: " + weatherInfo.getDescription());
            txtTemperature.setText("درجه حرارت فعلی: " + weatherInfo.getTemperature());
            txtMinTemperature.setText("حداقل دما: " + weatherInfo.getMinTemperature());
            txtMaxTemperature.setText("حداکثر دما: " + weatherInfo.getMaxTemperature());
            txtPressure.setText("فشار هوا: " + weatherInfo.getPressure());
            txtHumidity.setText("رطوبت هوا: " + weatherInfo.getHumidity());
            txtWindDegree.setText("درجه باد: " + weatherInfo.getWindDegree());
            txtWindSpeed.setText("سرعت باد: " + weatherInfo.getWindSpeed());
            txtCity.setText("شهر: " + weatherInfo.getCity());
            txtCountry.setText("کشور: " + weatherInfo.getCountry());
        } else {
            Toast.makeText(this, "خطا در دریافت اطلاعات", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

    class ConnectivityListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = networkInfo != null && networkInfo.isConnected();
            if (!isConnected) {
                snackbar = Snackbar.make(coordinatorLayout, "لطفا اتصال به اینترنت دستگاه را روشن نمایید", Snackbar.LENGTH_INDEFINITE);
                snackbar.show();

            } else {
                if (snackbar != null)
                    snackbar.dismiss();
            }
        }
    }
}
