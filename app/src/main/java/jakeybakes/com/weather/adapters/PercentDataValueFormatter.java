package jakeybakes.com.weather.adapters;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

public    class PercentDataValueFormatter  implements IValueFormatter  {

    public PercentDataValueFormatter() {
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        // return String with value of the float value rounded to the nearest integer
        return String.valueOf(Math.round(value) + "%");
    }
}
