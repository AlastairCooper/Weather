package jakeybakes.com.weather.adapters;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public    class Float2dpDataValueFormatter  implements IValueFormatter  {
    private DecimalFormat mFormat;

    public Float2dpDataValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0.00"); // use two decimals
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        // write your logic here
        return mFormat.format(value);
    }
}
