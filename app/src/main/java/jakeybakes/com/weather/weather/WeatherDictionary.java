package jakeybakes.com.weather.weather;

import org.jetbrains.annotations.Contract;
import androidx.annotation.NonNull;

/**
 * Created by repoo on 05/03/2019.
 */

public final class WeatherDictionary   {

    @org.jetbrains.annotations.Contract(pure = true)
    public static String getDefinition(@NonNull String searchTerm){

        String definition;
        switch(searchTerm){
            case "Cloud Cover": definition = "The percentage of sky occluded by clouds";
                break;
            case "Dew Point": definition = "The dew point in degrees Centigrade";
                break;
            case "High Temp": definition = "The daytime high temperature";
                break;
            case "Humidity": definition = "The relative humidity, between 0 and 1 inclusive";
                break;
            case "Moon Phase": definition = "The fractional part of the lunation number during the given day (0 or 1 = new moon; 0.5 = full moon)";
                break;
            case "Ozone": definition = "The columnar density of total atmospheric ozone at the given time. Measured in Dobson units";
                break;
            case "Precipitation": definition = "The probability of precipitation occurring, expressed as a percentage";
                break;
            case "Pressure": definition = "The sea-level air pressure in millibars";
                break;
            case "Sunrise": definition = "The time the sun will rise";
                break;
            case "Sunset": definition = "The time the sun will set";
                break;
            case "Temperature": definition = "The air temperature in degrees Centigrade";
                break;
            case "UV Index": definition = "Index numbers based on the estimated amount of UV radiation reaching Earth's surface";
                break;
            case "Visibility": definition = "The average visibility in miles, capped at 10 miles";
                break;
            case "Wind": definition = "Average speed and direction of the wind, with maximum gust speed";
                break;
            case "": definition = "";
                break;
            default: definition = "";
                break;
        }
        return definition;
    }

    @Contract(pure = true)
    public static String getGraphDescription(@NonNull String searchTerm){

        String definition;
        switch(searchTerm){
            case "Cloud Cover": definition = "The percentage of sky occluded by clouds";
                break;
            case "Dew Point": definition = "The dew point in degrees Centigrade";
                break;
            case "High Temperature": definition = "The maximum expected daytime air temperature";
                break;
            case "Humidity": definition = "The relative humidity, between 0 and 1 inclusive";
                break;
            case "Ozone Level": definition = "The columnar density of total atmospheric ozone";
                break;
            case "Chance of Precipitation": definition = "The probability of precipitation occurring, expressed as a percentage";
                break;
            case "Air Pressure": definition = "The sea-level air pressure";
                break;
            case "Temperature": definition = "The sea-level air temperature";
                break;
            case "UV Index": definition = "Index numbers are based on the estimated amount of UV radiation reaching Earth's surface";
                break;
            case "Visibility": definition = "The average visibility in miles, capped at 10 miles";
                break;
            case "Wind Speed": definition = "Average speed of the wind";
                break;
            case "": definition = "";
                break;
            default: definition = "";
                break;
        }
        return definition;
    }

    @Contract(pure = true)
    public static String getWindDirection(int windBearing){

        String direction = "N";
        if(windBearing >= 0 && windBearing < 23){
            direction = "N";
        } else if(windBearing >= 23 && windBearing < 67){
            direction = "NE";
        } else if(windBearing >= 67 && windBearing < 112){
            direction = "E";
        } else if(windBearing >= 112 && windBearing < 157){
            direction = "SE";
        } else if(windBearing >= 157 && windBearing < 202){
            direction = "S";
        } else if(windBearing >= 202 && windBearing < 247){
            direction = "SW";
        } else if(windBearing >= 247 && windBearing < 292){
            direction = "W";
        } else if(windBearing >= 292 && windBearing < 337){
            direction = "NW";
        } else if(windBearing >= 337){
            direction = "N";
        }
        return direction;
    }

    @Contract(pure = true)
    public static String getWindDirectionFine(int windBearing){

        String direction = "";
        if(windBearing >= 0 && windBearing < 12){
            direction = "N";
        } else if(windBearing >= 12 && windBearing < 34){
            direction = "NNE";
        } else if(windBearing >= 34 && windBearing < 57){
            direction = "NE";
        } else if(windBearing >= 57 && windBearing < 78){
            direction = "ENE";
        } else if(windBearing >= 78 && windBearing < 102){
            direction = "E";
        } else if(windBearing >= 102 && windBearing < 125){
            direction = "ESE";
        } else if(windBearing >= 125 && windBearing < 148){
            direction = "SE";
        } else if(windBearing >= 148 && windBearing < 168){
            direction = "SSE";
        } else if(windBearing >= 168 && windBearing < 192){
            direction = "S";
        } else if(windBearing >= 192 && windBearing < 215){
            direction = "SSW";
        } else if(windBearing >= 215 && windBearing < 237){
            direction = "SW";
        } else if(windBearing >= 237 && windBearing < 259){
            direction = "WSW";
        } else if(windBearing >= 259 && windBearing < 281){
            direction = "W";
        } else if(windBearing >= 281 && windBearing < 303){
            direction = "WNW";
        } else if(windBearing >= 303 && windBearing < 326){
            direction = "NW";
        } else if(windBearing >= 326 && windBearing < 348){
            direction = "NNW";
        } else if(windBearing >= 348){
            direction = "N";
        }
        return direction;
    }

}
