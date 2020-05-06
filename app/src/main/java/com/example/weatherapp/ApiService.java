package com.example.weatherapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherapp.model.WeatherInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ApiService {
    private static final String TAG = "ApiService";
    private Context context;

    public ApiService(Context context) {
        this.context = context;

    }

    public void getTehranWeather(final WeatherInfoReceived weatherInfoReceived) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                "http://api.openweathermap.org/data/2.5/weather?q=Tehran&appid=a61fc4764db63c1c6f5f822e20569d92"
                , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "onResponse: " + response.toString());
                weatherInfoReceived.onReceived(parseResponseToWeatherInfo(response));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error.toString());
                weatherInfoReceived.onReceived(null);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }

    private WeatherInfo parseResponseToWeatherInfo(JSONObject response) {
        WeatherInfo weatherInfo = new WeatherInfo();
        try {
            JSONArray weatherJsonArray = response.getJSONArray("weather");
            JSONObject weatherJsonObject = weatherJsonArray.getJSONObject(0);
            weatherInfo.setName(weatherJsonObject.getString("main"));
            weatherInfo.setDescription(weatherJsonObject.getString("description"));
            JSONObject mainJsonObject = response.getJSONObject("main");
            weatherInfo.setTemperature(mainJsonObject.getDouble("temp"));
            weatherInfo.setMinTemperature(mainJsonObject.getDouble("temp_min"));
            weatherInfo.setMaxTemperature(mainJsonObject.getDouble("temp_max"));
            weatherInfo.setPressure(mainJsonObject.getInt("pressure"));
            weatherInfo.setHumidity(mainJsonObject.getInt("humidity"));
            JSONObject windJsonObject = response.getJSONObject("wind");
            weatherInfo.setWindSpeed(windJsonObject.getDouble("speed"));
            weatherInfo.setWindDegree(windJsonObject.getDouble("deg"));
            JSONObject sysJsonObject = response.getJSONObject("sys");
            weatherInfo.setCountry(sysJsonObject.getString("country"));
            weatherInfo.setCity(response.getString("name"));
            return weatherInfo;


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface WeatherInfoReceived {
        void onReceived(WeatherInfo weatherInfo);
    }
}
