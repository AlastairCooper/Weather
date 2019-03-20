package jakeybakes.com.weather.weather;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by repoo on 01/03/2019.
 */

public class CurrentWeather  implements Serializable {
public static final String TAG = CurrentWeather.class.getSimpleName();
    private String locationLabel;
    private String icon;
    private long time;
    private double temperature;
    private double humidity;
    private double precipChance;
    private String summary;
    private String timezone;

    private double apparentTemperature;
    private double precipIntensity;
    private double dewPoint;
    private double pressure;
    private double windSpeed;
    private double windGust;
    private int windBearing;
    private String windSummary;
    private double cloudCover;
    private int uvIndex;
    private double visibility;
    private double ozone;

    public CurrentWeather(String locationLabel, String icon, long time, double temperature,
                          double humidity, double precipChance, String summary, String timezone,
                          double apparentTemperature,
                          double precipIntensity, double dewPoint, double pressure, double windSpeed,
                          double windGust, int windBearing, double cloudCover, int uvIndex,
                          double visibility, double ozone) {
        this.locationLabel = locationLabel;
        this.icon = icon;
        this.time = time;
        this.temperature = getCelciusValue(temperature);
        this.humidity = doubleRound(humidity,2);
        this.precipChance = precipChance;
        this.summary = summary;
        this.timezone = timezone;
        this.apparentTemperature = apparentTemperature;
        this.precipIntensity = precipIntensity;
        this.dewPoint = doubleRound(getCelciusValue(dewPoint),2);
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.windGust = windGust;
        this.windBearing = windBearing;
        this.windSummary = "Wind: " + Math.round(windSpeed) + "mph from " + getWindDirection()
                            + " - (gusts " + Math.round(windGust) + "mph)";
        this.cloudCover = cloudCover;
        this.uvIndex = uvIndex;
        this.visibility = visibility;
        this.ozone = ozone;
    }

    private double getCelciusValue(double temperature) {
        return  (temperature - 32.00d) * (5.00d/9.00d);
    }

    public String getWindSummary() {
        return windSummary;
    }

    public String getLocationLabel() {
        return locationLabel;
    }

    public String getIcon() {
        return icon;
    }

    public long getTime() {
        return time;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPrecipChance() {
        return precipChance;
    }

    public String getSummary() {
        return summary;
    }

    public String getTimezone() {
        return timezone;
    }

    public double getApparentTemperature() {
        return apparentTemperature;
    }

    public double getPrecipIntensity() {
        return precipIntensity;
    }

    public double getDewPoint() {
        return dewPoint;
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

    public String getFormattedTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        return formatter.format(new Date(time * 1000));
    }
    public static double doubleRound(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public String getWindDirectionFine(){

        return WeatherDictionary.getWindDirectionFine(windBearing);
    }

    public String getWindDirection(){

        return WeatherDictionary.getWindDirection(windBearing);
    }


    public int getIconId(){
        return Forecast.getIconId(getIcon());
    }
}