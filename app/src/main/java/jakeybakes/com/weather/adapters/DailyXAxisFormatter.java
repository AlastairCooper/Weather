package jakeybakes.com.weather.adapters;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public    class DailyXAxisFormatter  implements IAxisValueFormatter  {

    private String[] mValues;

    public DailyXAxisFormatter(String[] values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        return mValues[(int) value];
    }


}

