package jakeybakes.com.weather.weather;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by repoo on 04/03/2019.
 */

public class Hour  implements Serializable {

    private long time;
    private String summary;
    private double temperature;
    private String icon;
    private String timezone;
    private double apparentTemperature;
    private double precipProbability;
    private double dewPoint;
    private double humidity;
    private double pressure;
    private double windSpeed;
    private double windGust;
    private int windBearing;
    private double cloudCover;
    private int uvIndex;
    private double visibility;
    private double ozone;

    public long getTime() {
        return time;
    }

    public String getDayOfWeek(){
        SimpleDateFormat formatter = new SimpleDateFormat("E");
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        return formatter.format(new Date(time * 1000));
    }

    public String getFullDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE d MMMM yyyy - h a");
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        String fullDateWithPeriods = formatter.format(new Date(time * 1000));
        String fullDate = fullDateWithPeriods.replace(".", "");
        return fullDate;
    }

    public double getTemperature() {
        return (temperature - 32.0d)*(5.0d/9.0d);
    }

    public String getSummary() {
        return summary;
    }

    public String getIcon() {
        return icon;
    }

    public int getIconId() {
        return Forecast.getIconId(icon);
    }

    public String getTimezone() {
        return timezone;
    }

    public double getApparentTemperature() {
        return apparentTemperature;
    }

    public double getDewPoint() {
        return  (dewPoint - 32.0d)*(5.0d/9.0d);
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getWindGust() {
        return windGust;
    }

    public int getWindBearing() {
        return windBearing;
    }

    public String getWindDirectionFine(){

        return WeatherDictionary.getWindDirectionFine(windBearing);
    }

    public String getWindDirection(){

        return WeatherDictionary.getWindDirection(windBearing);
    }

    public double getCloudCover() {
        return cloudCover;
    }

    public int getUvIndex() {
        return uvIndex;
    }

    public double getVisibility() {
        return visibility;
    }

    public double getOzone() {
        return ozone;
    }

    public double getPrecipProbability() {
        return precipProbability;
    }

    public void setPrecipProbability(double precipProbability) {
        this.precipProbability = precipProbability;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void setApparentTemperature(double apparentTemperature) {
        this.apparentTemperature = apparentTemperature;
    }

    public void setDewPoint(double dewPoint) {
        this.dewPoint = dewPoint;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setWindGust(double windGust) {
        this.windGust = windGust;
    }

    public void setWindBearing(int windBearing) {
        this.windBearing = windBearing;
    }

    public void setCloudCover(double cloudCover) {
        this.cloudCover = cloudCover;
    }

    public void setUvIndex(int uvIndex) {
        this.uvIndex = uvIndex;
    }

    public void setVisibility(double visibility) {
        this.visibility = visibility;
    }

    public void setOzone(double ozone) {
        this.ozone = ozone;
    }

    public Hour() {
    }

    public String getFormattedTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("h a");
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        String fullDateWithPeriods = formatter.format(new Date(time * 1000));
        String fullDate = fullDateWithPeriods.replace(".", "");
        return fullDate;
    }

    @Override
    public String toString() {
        return "Hour{" +
                "time=" + time +
                ", summary='" + summary + '\'' +
                ", temperature=" + temperature +
                ", icon='" + icon + '\'' +
                ", timezone='" + timezone + '\'' +
                ", apparentTemperature=" + apparentTemperature +
                ", dewPoint=" + dewPoint +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                ", windSpeed=" + windSpeed +
                ", windGust=" + windGust +
                ", windBearing=" + windBearing +
                ", cloudCover=" + cloudCover +
                ", uvIndex=" + uvIndex +
                ", visibility=" + visibility +
                ", ozone=" + ozone +
                '}';
    }
}
