package jakeybakes.com.weather.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakeybakes.com.weather.R;
import jakeybakes.com.weather.adapters.PrefLocationAdapter;
import jakeybakes.com.weather.databinding.ActivityLocationsLibraryBinding;
import jakeybakes.com.weather.location.PrefLocation;
import jakeybakes.com.weather.weather.CurrentWeather;
import jakeybakes.com.weather.weather.Day;
import jakeybakes.com.weather.weather.Forecast;
import jakeybakes.com.weather.weather.Hour;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationsLibraryActivity extends AppCompatActivity {
    public static final String TAG = SaveLocationActivity.class.getSimpleName();
    private PrefLocationAdapter adapter;
    private ActivityLocationsLibraryBinding binding;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private List<PrefLocation> myLocations;
    private int currentLocationIndex;
    private PrefLocation currentPrefLoc;
    private Forecast forecast;

    private boolean myLocationsEdited = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        forecast = (Forecast)getApplicationContext();

        mSharedPreferences = getSharedPreferences(LocationActivity.PREFS_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        String currentPrefs = mSharedPreferences.getString(LocationActivity.KEY_SAVED_LOCATIONS ,"");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_locations_library);

        binding.txtvMyLocEntryEditInstruction.setVisibility(View.INVISIBLE);
        binding.btnMylocEntryEditSave.setVisibility(View.INVISIBLE);
        binding.edtxMyLocEditNewName.setVisibility(View.INVISIBLE);

        if(forecast.isAlreadySet()) {
            binding.btnMyLocBackToWeather.setEnabled(true);
            binding.btnMyLocBackToWeather.setAlpha(1f);
            binding.btnMyLocBackToWeather.setOnClickListener((v) -> goToCurrentLocationWeather());
        } else{
            binding.btnMyLocBackToWeather.setEnabled(false);
            binding.btnMyLocBackToWeather.setAlpha(0.3f);
        }
        binding.btnMyLocBackToMap.setOnClickListener((v)-> goBackToMapOnClick());

        if(currentPrefs.length() == 0){
            binding.txtvMyLocPageInstruction.setText(R.string.no_locations_saved);
        } else{
            myLocations = new ArrayList<>();
            String[] locs = currentPrefs.split("#");
            for(String loc : locs){
                if(loc.length() > 0) {
                    String[] locData = loc.split("_");
                    myLocations.add(new PrefLocation(locData[0], locData[1], locData[2]));
                }
            }
            adapter = new PrefLocationAdapter(myLocations, this, this);
            binding.rcyvMyLocLocList.setAdapter(adapter);
            binding.rcyvMyLocLocList.setLayoutManager(new LinearLayoutManager(this));
            binding.rcyvMyLocLocList.addItemDecoration(new DividerItemDecoration(binding.rcyvMyLocLocList.getContext(),
                    DividerItemDecoration.VERTICAL));
            binding.txtvMyLocPageInstruction.setText(String.format("You have %s locations saved\nSelect a location",myLocations.size()));
        }

        toggleLocationOptionButtons(false);

        // set Activity transition
        Fade fade = new Fade();
        fade.setDuration(300);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setReturnTransition(fade);
        getWindow().setExitTransition(fade);
        getWindow().setReenterTransition(fade);
    }

    // **********************************
    //  RecyclerView LIST ITEM - ON_CLICK
    // **********************************

    public void preferredLocationOnClick(View view, int position) {
        currentPrefLoc = (PrefLocation)view.getTag();
        currentLocationIndex = position;
        toggleLocationOptionButtons(true);
        Log.d(TAG, "preferredLocationOnClick: Hi, position is ... " + position);
    }

    // ***********************************************
    //  MY LOCATION ENTRY BUTTONS and related methods
    // ***********************************************

    public void myLocEntryShowWeather(View view) {
        Double lat = Double.valueOf(myLocations.get(currentLocationIndex).getLatitude());
        Double lon = Double.valueOf(myLocations.get(currentLocationIndex).getLongitude());
        String loc = myLocations.get(currentLocationIndex).getLocation();
        getCurrentWeather(lat,lon,loc);

    }

    public void myLocEntryRemove(View view) {
        Log.d(TAG, "myLocEntryRemove: Remove Location");
        myLocations.remove(currentLocationIndex);
        writeNewSharedPrefs();
    }

    public void myLocEntryEdit(View view) {
        Log.d(TAG, "myLocEntryEdit: Edit Location");
        // hide buttons and show edit widgets
        toggleLocationOptionButtons(false);
        binding.txtvMyLocEntryEditInstruction.setVisibility(View.VISIBLE);
        binding.btnMylocEntryEditSave.setVisibility(View.VISIBLE);
        binding.edtxMyLocEditNewName.setVisibility(View.VISIBLE);
        binding.edtxMyLocEditNewName.setText(myLocations.get(currentLocationIndex).getLocation());
    }

    public void myLocEntryEditSave(View view) {
        Log.d(TAG, "myLocEntryEditSave: Saving new Location");
        // verify text entered
        String newName = binding.edtxMyLocEditNewName.getText().toString();
        if(newName.contains("#") || newName.contains("_")){
            Toast.makeText(this, "Names cannot include '#' or '_' characters",Toast.LENGTH_LONG).show();
        } else {
            if (newName.length() > 0) {
                // hide edit widgets
                binding.txtvMyLocEntryEditInstruction.setVisibility(View.INVISIBLE);
                binding.btnMylocEntryEditSave.setVisibility(View.INVISIBLE);
                binding.edtxMyLocEditNewName.setVisibility(View.INVISIBLE);
                // use editText value to set new location in myLocations list, then rewrite shared prefs
                myLocations.get(currentLocationIndex).setLocation(newName);
                writeNewSharedPrefs();
            } else{
                Toast.makeText(this, "Names must contain at least 1 character",Toast.LENGTH_LONG).show();
            }
        }
        hideKeyboard(this);
    }

    private void writeNewSharedPrefs() {
        // create String of prefLocation data
        String totalStr = "";
        boolean firstTime = true;
        for(PrefLocation loc : myLocations){
            if(!firstTime) totalStr += "#";
            totalStr += loc.getSaveString();
            firstTime = false;
        }
        // save it to shared preferences
        mEditor.putString(LocationActivity.KEY_SAVED_LOCATIONS, totalStr);
        mEditor.apply();
        // reload recyclerview and message
        adapter.notifyDataSetChanged();
        if(myLocations.size() > 0) {
            binding.txtvMyLocPageInstruction.setText(String
                    .format("You have %s locations saved\nSelect a location", myLocations.size()));
        } else {
            binding.txtvMyLocPageInstruction.setText(R.string.no_locations_saved);
        }
        adapter.selectedPos = RecyclerView.NO_POSITION;
        toggleLocationOptionButtons(false);
    }

    public static void hideKeyboard(@NonNull Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void toggleLocationOptionButtons(boolean isEnabled){
        if(isEnabled){
            binding.btnMyLocEntryEdit.setEnabled(true);
            binding.btnMyLocEntryRemove.setEnabled(true);
            binding.btnMyLocEntryShow.setEnabled(true);
            binding.btnMyLocEntryEdit.setVisibility(View.VISIBLE);
            binding.btnMyLocEntryRemove.setVisibility(View.VISIBLE);
            binding.btnMyLocEntryShow.setVisibility(View.VISIBLE);
            Log.d(TAG, "toggleLocationOptionButtons: Inside toggle true");
        }else{
            binding.btnMyLocEntryEdit.setEnabled(false);
            binding.btnMyLocEntryRemove.setEnabled(false);
            binding.btnMyLocEntryShow.setEnabled(false);
            binding.btnMyLocEntryEdit.setVisibility(View.INVISIBLE);
            binding.btnMyLocEntryRemove.setVisibility(View.INVISIBLE);
            binding.btnMyLocEntryShow.setVisibility(View.INVISIBLE);
            Log.d(TAG, "toggleLocationOptionButtons: Inside toggle false");
        }
    }

    // *******************************
    //  GET WEATHER FUNCTIONS
    // *******************************

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
                            runOnUiThread( ()-> goToCurrentLocationWeather());
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

    // *******************************
    //  NAVIGATION BUTTONS
    // *******************************

    private void goBackToMapOnClick() {
        if(!forecast.isBeingUpdated()) {
            Intent intent = new Intent(getBaseContext(), LocationActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LocationsLibraryActivity.this);
            startActivity(intent, options.toBundle());
        }
    }

    private void goToCurrentLocationWeather() {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LocationsLibraryActivity.this);
            startActivity(intent, options.toBundle());
    }

}
