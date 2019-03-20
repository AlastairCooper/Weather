package jakeybakes.com.weather.weather;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakeybakes.com.weather.R;

import static jakeybakes.com.weather.weather.Day.TAG;

public class Forecast extends Application implements Serializable{

    private CurrentWeather currentWeather;
    private Hour[] hourlyForecast;
    private Day[] dailyForecast;
    private String dailySummary;
    private String hourlySummary;
    private boolean isAlreadySet;
    private boolean isBeingUpdated = false;
    private String location;
    private Day currentDay;
    private Hour currentHour;
    private int currentGraph = 0;

    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }

    public Hour[] getHourlyForecast() {
        return hourlyForecast;
    }

    public Day[] getDailyForecast() {
        return dailyForecast;
    }

    public String getDailySummary() {
        return dailySummary;
    }

    public String getHourlySummary() {
        return hourlySummary;
    }

    public boolean isAlreadySet() {
        return isAlreadySet;
    }

    public String getLocation() {
        return location;
    }

    public Day getCurrentDay() {
        return currentDay;
    }

    public Hour getCurrentHour() {
        return currentHour;
    }

    public int getCurrentGraph() {
        return currentGraph;
    }

    public void setCurrentGraph(int currentGraph) {
        this.currentGraph = currentGraph;
    }

    public boolean isBeingUpdated() {
        return isBeingUpdated;
    }

    public void setBeingUpdated(boolean beingUpdated) {
        isBeingUpdated = beingUpdated;
    }

    public void setCurrentHour(Hour currentHour) {
        this.currentHour = currentHour;
    }

    public void setCurrentDay(Day currentDay) {
        this.currentDay = currentDay;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAlreadySet(boolean alreadySet) {
        isAlreadySet = alreadySet;
    }

    public void setCurrentWeather(CurrentWeather currentWeather) {
        this.currentWeather = currentWeather;
    }

    public void setHourlyForecast(Hour[] hourlyForecast) {
        this.hourlyForecast = hourlyForecast;
    }

    public void setDailyForecast(Day[] dailyForecast) {
        this.dailyForecast = dailyForecast;
    }

    public void setDailySummary(String dailySummary) {
        this.dailySummary = switchSummaryFahrenheitForCentigrade(dailySummary);
    }

    public void setHourlySummary(String hourlySummary) {
        this.hourlySummary = switchSummaryFahrenheitForCentigrade(hourlySummary);
    }

    @Contract(pure = true)
    public static int getIconId(@NonNull String iconString){
        int iconId = R.drawable.clear_day;
        switch(iconString){
            case "clear-day":
                iconId = R.drawable.clear_day;
                break;
            case "clear-night":
                iconId = R.drawable.clear_night;
                break;
            case "rain":
                iconId = R.drawable.rain;
                break;
            case "snow":
                iconId = R.drawable.snow;
                break;
            case "sleet":
                iconId = R.drawable.sleet;
                break;
            case "wind":
                iconId = R.drawable.wind;
                break;
            case "fog":
                iconId = R.drawable.fog;
                break;
            case "cloudy":
                iconId = R.drawable.cloudy;
                break;
            case "partly-cloudy-day":
                iconId = R.drawable.partly_cloudy;
                break;
            case "partly-cloudy-night":
                iconId = R.drawable.cloudy_night;
                break;
        }
        return iconId;
    }

    @Override
    public String toString() {
        return dailyForecast[0].toString();
    }

    public String switchSummaryFahrenheitForCentigrade(String summary) {
        String tempFound = null;
        //System.out.println(summary);
        Pattern p = Pattern.compile("\\d+\\WF");
        Matcher m = p.matcher(summary);
        while(m.find()) {
            tempFound = m.group();
            tempFound = tempFound.substring(0,tempFound.length()-2);
            //System.out.println(tempFound);
        }
        if(tempFound != null) {
            int newTemp = Math.round((Integer.valueOf(tempFound) - 32) * 5/9);
            //System.out.println(newTemp);
            summary = summary
                    .replace(tempFound,String.valueOf(newTemp))
                    .replace("F ","C ");
        }
        //System.out.println(summary);
        return summary;
    }

    //
    //  GETTERS FOR DAILY GRAPHS
    ////////////////////////////////

    public String getDailyForecastDateString(){

        SimpleDateFormat formatter = new SimpleDateFormat("EEEE d MMM");
        formatter.setTimeZone(TimeZone.getTimeZone(dailyForecast[0].getTimezone()));
        String startDate = formatter.format(new Date(dailyForecast[0].getTime() * 1000));
        int len = dailyForecast.length;
        String endDate = formatter.format(new Date(dailyForecast[len - 1].getTime() * 1000));

        return startDate + " - " + endDate;
    }

    public String[] getDayLabels(){
        int len = dailyForecast.length;
        Log.d(TAG, "dailyForecast.length is: " + len);
        String[] labels = new String[len];
        SimpleDateFormat formatter = new SimpleDateFormat("E");
        formatter.setTimeZone(TimeZone.getTimeZone(dailyForecast[0].getTimezone()));
        for(int i = 0; i < len; i++){
            labels[i] = formatter.format(new Date(dailyForecast[i].getTime() * 1000));
        }
        return labels;
    }

    public List<BarEntry> getDailyGraphDataTemperature(){

        List<BarEntry> entries = new ArrayList<>();
        float counter = 0;
        for (Day day : dailyForecast) {
            // turn your data into Entry objects
            entries.add(new BarEntry((float)counter, (float)day.getTemperatureHigh()));
            counter++;
        }
        return entries;
    }
    public int getLowestDailyHighTemp(){
        int len = dailyForecast.length;
        Double[] vals = new Double[len];
        for(int i = 0; i < len; i++){
            vals[i] = dailyForecast[i].getTemperatureHigh();
        }
        Arrays.sort(vals);
        return (int)Math.round(vals[0]);
    }
    public int getHighestDailyHighTemp(){
        int len = dailyForecast.length;
        Double[] vals = new Double[len];
        for(int i = 0; i < len; i++){
            vals[i] = dailyForecast[i].getTemperatureHigh();
        }
        Arrays.sort(vals);
        return (int)Math.round(vals[len - 1]);
    }

    public List<BarEntry> getDailyGraphDataPrecipitation(){

        List<BarEntry> entries = new ArrayList<>();
        float counter = 0;
        for (Day day : dailyForecast) {
            // turn your data into Entry objects
            entries.add(new BarEntry((float)counter, (float)day.getPrecipProbability() * 100));
            counter++;
        }
        return entries;
    }

    public List<BarEntry> getDailyGraphDataAirPressure(){

        List<BarEntry> entries = new ArrayList<>();
        float counter = 0;
        for (Day day : dailyForecast) {
            // turn your data into Entry objects
            entries.add(new BarEntry((float)counter, (float)day.getPressure()));
            counter++;
        }
        return entries;
    }

    public List<BarEntry> getDailyGraphDataHumidity(){

        List<BarEntry> entries = new ArrayList<>();
        float counter = 0;
        for (Day day : dailyForecast) {
            // turn your data into Entry objects
            entries.add(new BarEntry((float)counter, (float)day.getHumidity()));
            counter++;
        }
        return entries;
    }

    public List<BarEntry> getDailyGraphDataCloudCover(){

        List<BarEntry> entries = new ArrayList<>();
        float counter = 0;
        for (Day day : dailyForecast) {
            // turn your data into Entry objects
            entries.add(new BarEntry((float)counter, (float)day.getCloudCover() * 100));
            counter++;
        }
        return entries;
    }

    public List<BarEntry> getDailyGraphDataVisibility(){

        List<BarEntry> entries = new ArrayList<>();
        float counter = 0;
        for (Day day : dailyForecast) {
            // turn your data into Entry objects
            entries.add(new BarEntry((float)counter, (float)day.getVisibility()));
            counter++;
        }
        return entries;
    }

    public List<BarEntry> getDailyGraphDataWindSpeed(){

        List<BarEntry> entries = new ArrayList<>();
        float counter = 0;
        for (Day day : dailyForecast) {
            // turn your data into Entry objects
            entries.add(new BarEntry((float)counter, (float)day.getWindSpeed()));
            counter++;
        }
        return entries;
    }

    public List<BarEntry> getDailyGraphDataOzoneLevel(){

        List<BarEntry> entries = new ArrayList<>();
        float counter = 0;
        for (Day day : dailyForecast) {
            // turn your data into Entry objects
            entries.add(new BarEntry((float)counter, (float)day.getOzone()));
            counter++;
        }
        return entries;
    }

    public List<BarEntry> getDailyGraphDataUvIndex(){

        List<BarEntry> entries = new ArrayList<>();
        float counter = 0;
        for (Day day : dailyForecast) {
            // turn your data into Entry objects
            entries.add(new BarEntry((float)counter, (float)day.getUvIndex()));
            counter++;
        }
        return entries;
    }

    //
    //  GETTERS FOR HOURLY GRAPHS
    ////////////////////////////////

    public String getHourlyForecastDateString(){

        SimpleDateFormat formatter = new SimpleDateFormat("EEEE ha");
        formatter.setTimeZone(TimeZone.getTimeZone(hourlyForecast[0].getTimezone()));
        String startTime = formatter.format(new Date(hourlyForecast[0].getTime() * 1000)).replace(".","");
        int len = hourlyForecast.length;
        String endTime = formatter.format(new Date(hourlyForecast[len - 1].getTime() * 1000)).replace(".","");

        return startTime + " - " + endTime;
    }

    public String[] getHourLabels(){
        int len = hourlyForecast.length;
        Log.d(TAG, "dailyForecast.length is: " + len);
        String[] labels = new String[len];
        SimpleDateFormat formatter = new SimpleDateFormat("E\nha");
        formatter.setTimeZone(TimeZone.getTimeZone(hourlyForecast[0].getTimezone()));
        for(int i = 0; i < len; i++){
            labels[i] = formatter.format(new Date(hourlyForecast[i].getTime() * 1000)).replace(".","");
        }
        return labels;
    }

    public List<Entry> getHourlyGraphDataTemperature(){

        List<Entry> entries = new ArrayList<>();
        float counter = 0;
        for (Hour hour : hourlyForecast) {
            // turn your data into Entry objects
            entries.add(new Entry((float)counter, (float)hour.getTemperature()));
            counter++;
        }
        return entries;
    }
    public int getLowestHourlyHighTemp(){
        int len = hourlyForecast.length;
        Double[] vals = new Double[len];
        for(int i = 0; i < len; i++){
            vals[i] = hourlyForecast[i].getTemperature();
        }
        Arrays.sort(vals);
        return (int)Math.round(vals[0]);
    }
    public int getHighestHourlyHighTemp(){
        int len = hourlyForecast.length;
        Double[] vals = new Double[len];
        for(int i = 0; i < len; i++){
            vals[i] = hourlyForecast[i].getTemperature();
        }
        Arrays.sort(vals);
        return (int)Math.round(vals[len - 1]);
    }


    public List<Entry> getHourlyGraphDataPrecipitation(){

        List<Entry> entries = new ArrayList<>();
        float counter = 0;
        for (Hour hour : hourlyForecast) {
            // turn your data into Entry objects
            entries.add(new Entry((float)counter, (float)Math.round(hour.getPrecipProbability() * 100)));
            counter++;
        }
        return entries;
    }

    public List<Entry> getHourlyGraphDataOzoneLevels(){

        List<Entry> entries = new ArrayList<>();
        float counter = 0;
        for (Hour hour : hourlyForecast) {
            // turn your data into Entry objects
            entries.add(new Entry((float)counter, (float)hour.getOzone()));
            counter++;
        }
        return entries;
    }

    public List<Entry> getHourlyGraphDataVisibility(){

        List<Entry> entries = new ArrayList<>();
        float counter = 0;
        for (Hour hour : hourlyForecast) {
            // turn your data into Entry objects
            entries.add(new Entry((float)counter, (float)hour.getVisibility()));
            counter++;
        }
        return entries;
    }

    public List<Entry> getHourlyGraphDataCloudCover(){

        List<Entry> entries = new ArrayList<>();
        float counter = 0;
        for (Hour hour : hourlyForecast) {
            // turn your data into Entry objects
            entries.add(new Entry((float)counter, (float)Math.round(hour.getCloudCover() * 100)));
            counter++;
        }
        return entries;
    }

    public List<Entry> getHourlyGraphDataWindSpeed(){

        List<Entry> entries = new ArrayList<>();
        float counter = 0;
        for (Hour hour : hourlyForecast) {
            // turn your data into Entry objects
            entries.add(new Entry((float)counter, (float)hour.getWindSpeed()));
            counter++;
        }
        return entries;
    }

    public List<BarEntry> getHourlyGraphDataUvIndex(){

        List<BarEntry> entries = new ArrayList<>();
        float counter = 0;
        for (Hour hour : hourlyForecast) {
            // turn your data into Entry objects
            entries.add(new BarEntry((float)counter, (float)hour.getUvIndex()));
            counter++;
        }
        return entries;
    }

    public List<Entry> getHourlyGraphDataAirPressure(){

        List<Entry> entries = new ArrayList<>();
        float counter = 0;
        for (Hour hour : hourlyForecast) {
            // turn your data into Entry objects
            entries.add(new Entry((float)counter, (float)hour.getPressure()));
            counter++;
        }
        return entries;
    }

    public List<Entry> getHourlyGraphDataHumidity(){

        List<Entry> entries = new ArrayList<>();
        float counter = 0;
        for (Hour hour : hourlyForecast) {
            // turn your data into Entry objects
            entries.add(new Entry((float)counter, (float)hour.getHumidity()));
            counter++;
        }
        return entries;
    }

}
