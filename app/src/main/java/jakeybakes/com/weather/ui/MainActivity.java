package jakeybakes.com.weather.ui;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.transition.Fade;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.Contract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import jakeybakes.com.weather.location.MyLocation;
import jakeybakes.com.weather.R;
import jakeybakes.com.weather.databinding.ActivityMainBinding;
import jakeybakes.com.weather.weather.CurrentWeather;
import jakeybakes.com.weather.weather.Day;
import jakeybakes.com.weather.weather.Forecast;
import jakeybakes.com.weather.weather.Hour;
import jakeybakes.com.weather.weather.WeatherDictionary;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSION_LOCATION = 255; // int should be between 0 and 255

    private Forecast forecast;
    private ImageView iconImageView;
    private  ActivityMainBinding binding;
    private double latitude;
    private double longitude;

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        forecast = (Forecast)getApplicationContext();
        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        binding.txtvMainFetchingDataMessage.setVisibility(View.INVISIBLE);
        binding.txtvTimeholder.setVisibility(View.VISIBLE);

        if(!forecast.isAlreadySet()) {
            // first time the app is opened
            forecast.setAlreadySet(true); // switch flag
            // ask for permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            } else {
                // We have already permission to use the location
                getLocationNameAndCurrentWeather();
                binding.txtvTimeholder.setVisibility(View.INVISIBLE);
                binding.txtvMainFetchingDataMessage.setVisibility(View.VISIBLE);
            }
        } else {
            //Do stuff with forecast data here
            iconImageView = findViewById(R.id.imgv_icon);
            TextView darkSky = findViewById(R.id.txtv_attribution);
            darkSky.setMovementMethod(LinkMovementMethod.getInstance());
            final CurrentWeather displayWeather = forecast.getCurrentWeather();
            binding.setCurrent(displayWeather);
            Drawable drawable = ContextCompat.getDrawable(this,displayWeather.getIconId());
            iconImageView.setImageDrawable(drawable);
        }

        Fade fade = new Fade();
        fade.setDuration(300);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setReturnTransition(fade);
        getWindow().setExitTransition(fade);
        getWindow().setReenterTransition(fade);
    }

    // *******************************
    //  GET WEATHER FUNCTIONS
    // *******************************

    private void getCurrentWeather(double latitude, double longitude, String location) {
        final ActivityMainBinding binding = DataBindingUtil.setContentView(MainActivity.this,
                R.layout.activity_main);
        final String locationLabel = location;
        iconImageView = findViewById(R.id.imgv_icon);

        TextView darkSky = findViewById(R.id.txtv_attribution);
        darkSky.setMovementMethod(LinkMovementMethod.getInstance());
        // build url
        String apiKey = "0af36c450df6f67c5f2a955253a3d5eb";
        String forecastUrl = "https://api.darksky.net/forecast/" + apiKey + "/" + latitude + "," + longitude;
        // instantiate client, request and call objects from OkHttp
        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request
                    .Builder()
                    .url(forecastUrl)
                    .build();
            Call call = client.newCall(request);
            // put request in queue and pass in a callback object
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    try {
                        String jsonData = response.body().string();
                        if (response.isSuccessful()) {
                            runOnUiThread(() -> {
                                binding.txtvMainFetchingDataMessage.setVisibility(View.INVISIBLE);
                                binding.txtvTimeholder.setVisibility(View.VISIBLE);
                            });
                            parseJsonData(jsonData, locationLabel);
                            binding.setCurrent(forecast.getCurrentWeather());
                            final CurrentWeather displayWeather = forecast.getCurrentWeather();
                            runOnUiThread(() -> {
                                Drawable drawable = ResourcesCompat.getDrawable(getResources(), displayWeather.getIconId(), null);
                                iconImageView.setImageDrawable(drawable);
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    } catch (JSONException jsone) {
                        jsone.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Sorry, Network is unavailable", Toast.LENGTH_LONG).show();
        }
    }

    private void getLocationNameAndCurrentWeather() {
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                // Got the location!
                // @Override gotLocation()
                setLatitude(location.getLatitude());
                setLongitude(location.getLongitude());
                String loc = getAddress(latitude,longitude);
                forecast.setLocation(loc);
                getCurrentWeather(latitude, longitude, loc);
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(this, locationResult);
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            if(obj.getLocality() != null) {
                return obj.getLocality();
            }
            if(obj.getPostalCode() != null){
                return obj.getPostalCode();
            }
            if(obj.getSubAdminArea() != null){
                return obj.getSubAdminArea();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Wherever I am?";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission to use location granted
                getLocationNameAndCurrentWeather();
            } else{
                // permission denied so go to 'get location from map' activity
                Intent intent = new Intent(getBaseContext(), LocationActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                startActivity(intent, options.toBundle());
            }
        }
    }

    private void parseJsonData(String jsonData, String locationString) throws JSONException{

        forecast = (Forecast)getApplicationContext();
        forecast.setCurrentWeather(getCurrentDetails(jsonData, locationString));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));

    }

    private CurrentWeather getCurrentDetails(String jsonData, String locationLabel) throws JSONException {

        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject current = forecast.getJSONObject("currently");

        String location = locationLabel;
        String icon = current.getString("icon");
        long time = current.getLong("time");
        Double temp = current.getDouble("temperature");
        Double humid = current.getDouble("humidity");
        Double precipProb = current.getDouble("precipProbability");
        String summary = current.getString("summary");
        Double precipIntensity = current.getDouble("temperature");
        Double apparentTemperature = current.getDouble("precipIntensity");
        Double dewPoint = current.getDouble("dewPoint");
        Double pressure = current.getDouble("pressure");
        Double windSpeed = current.getDouble("windSpeed");
        Double windGust = current.getDouble("windGust");
        Double cloudCover = current.getDouble("cloudCover");
        Double visibility = current.getDouble("visibility");
        Double ozone = current.getDouble("ozone");
        int uvIndex = current.getInt("uvIndex");
        int windBearing = current.getInt("windBearing");

        CurrentWeather cw = new CurrentWeather(location, icon, time, temp, humid, precipProb, summary, timezone,
                apparentTemperature, precipIntensity, dewPoint, pressure,
                windSpeed, windGust, windBearing, cloudCover, uvIndex, visibility, ozone);


        return cw;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {

        JSONObject forecastObj = new JSONObject(jsonData);
        String timezone = forecastObj.getString("timezone");
        JSONObject hourly = forecastObj.getJSONObject("hourly");
        forecast.setHourlySummary(hourly.getString("summary"));
        JSONArray hourlyData = hourly.getJSONArray("data");
        int hourNum = hourlyData.length();
        Hour[] hours = new Hour[hourNum];
        for(int i = 0; i < hourNum; i++){
            JSONObject obj = hourlyData.getJSONObject(i);
            Hour hour = new Hour();
            hour.setIcon(obj.getString("icon"));
            hour.setSummary(obj.getString("summary"));
            hour.setTime(obj.getLong("time"));
            hour.setTemperature(obj.getDouble("temperature"));
            hour.setTimezone(timezone);
            hour.setApparentTemperature(obj.getDouble("apparentTemperature"));
            hour.setPrecipProbability(obj.getDouble("precipProbability"));
            hour.setDewPoint(obj.getDouble("apparentTemperature"));
            hour.setHumidity(obj.getDouble("humidity"));
            hour.setPressure(obj.getDouble("pressure"));
            hour.setWindSpeed(obj.getDouble("windSpeed"));
            hour.setWindGust(obj.getDouble("windGust"));
            hour.setWindBearing(obj.getInt("windBearing"));
            hour.setCloudCover(obj.getDouble("cloudCover"));
            hour.setUvIndex(obj.getInt("uvIndex"));
            hour.setVisibility(obj.getDouble("visibility"));
            hour.setOzone(obj.getDouble("ozone"));
            hours[i] = hour;
        }

        return hours;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException{

        JSONObject forecastObj = new JSONObject(jsonData);
        String timezone = forecastObj.getString("timezone");
        JSONObject daily = forecastObj.getJSONObject("daily");
        forecast.setDailySummary(daily.getString("summary"));
        JSONArray dailyData = daily.getJSONArray("data");
        int dayNum = dailyData.length();
        Day[] days = new Day[dayNum];
        for(int i = 0; i < dayNum; i++){
            JSONObject obj = dailyData.getJSONObject(i);
            Day day = new Day();
            day.setIcon(obj.getString("icon"));
            day.setSummary(obj.getString("summary"));
            day.setTime(obj.getLong("time"));
            day.setTimezone(timezone);
            day.setSunriseTime(obj.getLong("sunriseTime"));
            day.setSunsetTime(obj.getLong("sunsetTime"));
            day.setMoonPhase(obj.getDouble("moonPhase"));
            day.setPrecipProbability(obj.getDouble("precipProbability"));
            day.setTemperatureHigh(obj.getDouble("temperatureHigh"));
            day.setDewPoint(obj.getDouble("dewPoint"));
            day.setHumidity(obj.getDouble("humidity"));
            day.setPressure(obj.getDouble("pressure"));
            day.setWindSpeed(obj.getDouble("windSpeed"));
            day.setWindGust(obj.getDouble("windGust"));
            day.setWindBearing(obj.getInt("windBearing"));
            day.setCloudCover(obj.getDouble("cloudCover"));
            day.setUvIndex(obj.getInt("uvIndex"));
            day.setVisibility(obj.getDouble("visibility"));
            day.setOzone(obj.getDouble("ozone"));

            days[i] = day;
        }

        return days;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment alertDialog = new AlertDialogFragment();
        alertDialog.show(getFragmentManager(), "error dialog");
    }

    // *******************************
    //  VIEW CLICK EVENT HANDLERS
    // *******************************

    public void refreshOnClick(View view) {
        getLocationNameAndCurrentWeather();
        Toast.makeText(this, "Refreshing data", Toast.LENGTH_LONG).show();
    }

    public void hourlyOnClick(View view) {
        Intent intent = new Intent(this, HourlyForecastActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
        startActivity(intent, options.toBundle());
    }

    public void dailyOnClick(View view) {
        Intent intent = new Intent(getBaseContext(), DailyForecastActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
        startActivity(intent, options.toBundle());
    }

    public void locationOnClick(View view) {
        Intent intent = new Intent(getBaseContext(), LocationActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
        startActivity(intent, options.toBundle());
    }

    public void definitionOnClick(View view){
        TextView b = (TextView)view;
        String searchTerm = b.getText().toString();
        String message = searchTerm + " - " + WeatherDictionary.getDefinition(searchTerm);
        Toast.makeText(this, message , Toast.LENGTH_LONG).show();
    }
}