package jakeybakes.com.weather.ui;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import jakeybakes.com.weather.R;
import jakeybakes.com.weather.location.MyLocation;
import jakeybakes.com.weather.weather.CurrentWeather;
import jakeybakes.com.weather.weather.Day;
import jakeybakes.com.weather.weather.Forecast;
import jakeybakes.com.weather.weather.Hour;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationActivity extends AppCompatActivity  implements OnMapReadyCallback {

    private static final String TAG = "Maps_Activity";
    public static final String PREFS_FILE = "com.jakeybakes.weather.preferences";
    public static final String KEY_SAVED_LOCATIONS = "saved_locations";

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private MapView mapView;
    private GoogleMap mMap;
    private MarkerOptions marker;
    private TextView txtv_latitude;
    private TextView txtv_longitude;
    private TextView txtv_location;
    private Forecast forecast;
    private Button btn_showMeWeather;
    private Button btn_saveLocation;
    private Button btn_myLocations;
    private String latChosen;
    private String longChosen;
    private String locChosen;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private TransitionManager mTransitionManager;
    private Scene mExpandedScene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        forecast = (Forecast)getApplicationContext();

        mSharedPreferences = getSharedPreferences(PREFS_FILE,Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        btn_showMeWeather = findViewById(R.id.btn_showMeTheWeather);
        btn_myLocations = findViewById(R.id.btn_loadMyLocations);
        toggleShowWeatherButton(false);
        btn_saveLocation = findViewById(R.id.btn_saveLocationToPreferences);
        btn_saveLocation.setOnClickListener( v -> saveToSharedPreferences() );
        btn_saveLocation.setVisibility(View.INVISIBLE);
        txtv_latitude = findViewById(R.id.txtv_loc_latitude);
        txtv_longitude = findViewById(R.id.txtv_loc_longitude);
        txtv_location = findViewById(R.id.txtv_loc_location);
        mapView = findViewById(R.id.mapv_loc_map);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);


        Fade fade = new Fade();
        fade.setDuration(300);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setReturnTransition(fade);
        getWindow().setExitTransition(fade);
        getWindow().setReenterTransition(fade);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       // mMap.setMinZoomPreference(6); //the higher the zoom, the closer to earth, and the smaller the scale

        // Set Map Centre near Tamworth and move the camera
        double startLat = 52.660d;
        double startLong = -1.798d;
        LatLng england = new LatLng(startLat, startLong);
        //marker = new MarkerOptions().position(england);
        //mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(england));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(startLat, startLong), 6.0f));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            //add location button click listener
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){
                @Override
                public boolean onMyLocationButtonClick()
                {
                    getUserLocation();
                    return false;
                }
            });

        }
        // Set onClickListener
        mMap.setOnMapClickListener( latLng->{
            forecast.setBeingUpdated(true);
            mMap.clear();
            LatLng newPos = new LatLng(latLng.latitude, latLng.longitude);
            marker = new MarkerOptions().position(newPos);
            mMap.addMarker(marker);
            setLocationValues(latLng.latitude,latLng.longitude);
            getCurrentWeather(latLng.latitude,latLng.longitude,getAddress(latLng.latitude,latLng.longitude));
        });
    }


    private void getUserLocation() {
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                // Got the location!
                // @Override gotLocation()
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String loc = getAddress(latitude,longitude);
                getCurrentWeather(latitude, longitude, loc);
                setLocationValues(latitude,longitude);
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(this, locationResult);
    }


    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(LocationActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if(addresses.size() > 0) {
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
                return "Unknown";
            } else{
                return "Unknown";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    public String getLocationName(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if(addresses.size() > 0) {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                add = add + "\n" + obj.getCountryName();
                add = add + "\n" + obj.getCountryCode();
                add = add + "\n" + obj.getAdminArea();
                add = add + "\n" + obj.getPostalCode();
                add = add + "\n" + obj.getSubAdminArea();
                add = add + "\n" + obj.getLocality();
                add = add + "\n" + obj.getSubThoroughfare();
                return add;
            } else{
                return "Unknown";
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return "Unknown";
        }
    }
    private void setLocationValues(double latitude, double longitude) {
        Log.d(TAG, "setLocationValues: " + getLocationName(this,latitude,longitude));
        latChosen = String.valueOf(latitude);
        longChosen = String.valueOf(longitude);
        locChosen = getAddress(latitude,longitude);
        txtv_latitude.setText(latChosen);
        txtv_longitude.setText(longChosen);
        txtv_location.setText(locChosen);
        toggleLocationButtons();
    }

    private void toggleShowWeatherButton(boolean isClickable){
        if(isClickable){
            btn_showMeWeather.setEnabled(true);
            btn_showMeWeather.setAlpha(1f);
        } else{
            btn_showMeWeather.setEnabled(false);
            btn_showMeWeather.setAlpha(0.3f);
        }
    }
    private void toggleLocationButtons(){
        btn_myLocations.setVisibility(View.INVISIBLE);
        btn_saveLocation.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    private void getCurrentWeather(double latitude, double longitude, String location) {

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
                            parseJsonData(jsonData, location);
                            runOnUiThread( () -> toggleShowWeatherButton(true));
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

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void parseJsonData(String jsonData, String locationString) throws JSONException{

        forecast = (Forecast)getApplicationContext();
        forecast.setCurrentWeather(getCurrentDetails(jsonData, locationString));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));
        forecast.setAlreadySet(true);
        forecast.setBeingUpdated(false);
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

    private void alertUserAboutError() {
        AlertDialogFragment alertDialog = new AlertDialogFragment();
        alertDialog.show(getFragmentManager(), "error dialog");

    }

    public void saveToSharedPreferences(){
        Intent intent = new Intent(getBaseContext(), SaveLocationActivity.class);
        intent.putExtra("latitude",latChosen);
        intent.putExtra("longitude",longChosen);
        intent.putExtra("location",locChosen);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LocationActivity.this);
        startActivity(intent, options.toBundle());
    }

    public void showMeTheWeatherOnClick(View view) {
        if(!forecast.isBeingUpdated()) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LocationActivity.this);
            startActivity(intent, options.toBundle());
        }
    }

    public void loadMyLocationsOnClick(View view) {
        Intent intent = new Intent(getBaseContext(), LocationsLibraryActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LocationActivity.this);
        startActivity(intent, options.toBundle());
    }
}
