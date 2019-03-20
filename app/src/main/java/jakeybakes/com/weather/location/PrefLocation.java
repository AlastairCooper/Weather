package jakeybakes.com.weather.location;
import java.io.Serializable;
import java.text.DecimalFormat;

public class PrefLocation  implements Serializable  {

    private String latitude;
    private String longitude;
    private String location;

    public String getLatitude() {
        return latitude;
    }
    public Float getLatFloat() {
        return Float.valueOf(latitude);
    }
    public String getLatString() {
        return String.format("%.05f", Float.valueOf(latitude));
    }

    public String getLongitude() {
        return longitude;
    }
    public Float getLongFloat() {
        return Float.valueOf(longitude);
    }
    public String getLongString() {
        return String.format("%.05f", Float.valueOf(longitude));
    }

    public String getLocation() {
        return location;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public PrefLocation(String latitude, String longitude, String location) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
    }

    public String getSaveString(){
        return latitude + "_" + longitude + "_" + location;
    }
}
