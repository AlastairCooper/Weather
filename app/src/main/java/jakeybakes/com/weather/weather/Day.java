package jakeybakes.com.weather.weather;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Day  implements Serializable {
    public static final String TAG = Day.class.getSimpleName();

    private long time;
    private String timezone;
    private String summary;
    private String icon;
    private long sunriseTime;
    private long sunsetTime;
    private double moonPhase;
    private double precipProbability;
    private double temperatureHigh;
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

    public Day() {

    }

    public long getTime() {
        return time;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getSummary() {
        return summary;
    }

    public String getIcon() {
        return icon;
    }

    public int getIconId(){
        return Forecast.getIconId(getIcon());
    }

    public String getSunriseTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("h:m a");
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        String fullTime = formatter.format(new Date(sunriseTime * 1000));
        String retVal = fullTime.replace(".","");
        return retVal;
    }

    public String getSunsetTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("h:m a");
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        String fullTime = formatter.format(new Date(sunsetTime * 1000));
        String retVal = fullTime.replace(".","");
        return retVal;
    }

    public double getMoonPhase() {
        return moonPhase;
    }

    public double getPrecipProbability() {
        return precipProbability;
    }

    public double getTemperatureHigh() {
        return (temperatureHigh - 32.0d)*(5.0d/9.0d);
    }

    public double getDewPoint() {
        return (dewPoint - 32.0d)*(5.0d/9.0d);
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

    public String getDayOfWeek(){
        SimpleDateFormat formatter = new SimpleDateFormat("E");
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        return formatter.format(new Date(time * 1000));
    }

    public String getDateAndMonth(){
        SimpleDateFormat formatter = new SimpleDateFormat("d/M");
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        return formatter.format(new Date(time * 1000));
    }

    public String getFullDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE d MMMM yyyy");
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        return formatter.format(new Date(time * 1000));
    }

    public String getWindDirectionFine(){

        return WeatherDictionary.getWindDirectionFine(windBearing);
    }

    public String getWindDirection(){

        return WeatherDictionary.getWindDirection(windBearing);
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setSunriseTime(long sunriseTime) {
        this.sunriseTime = sunriseTime;
    }

    public void setSunsetTime(long sunsetTime) {
        this.sunsetTime = sunsetTime;
    }

    public void setMoonPhase(double moonPhase) {
        this.moonPhase = moonPhase;
    }

    public void setPrecipProbability(double precipProbability) {
        this.precipProbability = precipProbability;
    }

    public void setTemperatureHigh(double temperatureHigh) {
        this.temperatureHigh = temperatureHigh;
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


    @Override
    public String toString() {
        return "Day{" +
                "time=" + time +
                ", summary='" + summary + '\'' +
                ", icon='" + icon + '\'' +
                ", sunriseTime=" + sunriseTime +
                ", sunsetTime=" + sunsetTime +
                ", moonPhase=" + moonPhase +
                ", precipProbability=" + precipProbability +
                ", temperatureHigh=" + temperatureHigh +
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
