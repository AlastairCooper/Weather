package jakeybakes.com.weather.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.transition.Fade;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.List;

import jakeybakes.com.weather.R;
import jakeybakes.com.weather.adapters.DailyXAxisFormatter;
import jakeybakes.com.weather.adapters.HourlyXAxisFormatter;
import jakeybakes.com.weather.adapters.LineGraphXAxisRenderer;
import jakeybakes.com.weather.weather.Forecast;
import jakeybakes.com.weather.weather.WeatherDictionary;

public class HourlyGraphsActivity extends AppCompatActivity {
    private static final int GRAPH_X_AXIS_TEXT_SIZE = 12;
    private static final int GRAPH_X_AXIS_TEXT_COLOUR = Color.argb(205,255,255,255);
    private static final int GRAPH_DATASET_TEXT_COLOUR = Color.WHITE;
    private static final int GRAPH_BACKGROUND_COLOUR = Color.rgb(16,131,236);
    private static final float GRAPH_LEGEND_SET_Y_OFFSET = 5f;
    private static final int GRAPH_LINECHART_ANIMATION_MILLISECONDS = 700;
    private static final float GRAPH_BAR_WIDTH = 0.8f;
    private static final int GRAPH_DATASET_BORDER_COLOUR = Color.rgb(159,44,48);
    private static final int GRAPH_DATASET_BAR_COLOUR = Color.rgb(48,63,159);
    private static final float GRAPH_DATASET_BORDER_WIDTH = 1f;
    private static final int GRAPH_BARCHART_ANIMATION_MILLISECONDS = 700;

    private Forecast forecast;
    private String[] hourLabels;
    private LineChart chart;
    private BarChart chartBar;
    private TextView dateToDateTitle;
    private Spinner spinner;
    private TextView graphDefinition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_graphs);
        // set Dark Sky hyperlink action
        TextView darkSky = findViewById(R.id.txtv_hourlyGraphs_attribution);
        darkSky.setMovementMethod(LinkMovementMethod.getInstance());

        forecast = (Forecast) getApplicationContext();
        hourLabels = forecast.getHourLabels();
        graphDefinition = findViewById(R.id.txtv_hourlyGraphs_graphDefinition);

        dateToDateTitle = (TextView) findViewById(R.id.txtv_hourlyGraphs_dateTitle);
        dateToDateTitle.setText(forecast.getHourlyForecastDateString());

        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,R.array.hourly_graph_options,R.layout.choose_graph_spinner);
        spinner = (Spinner) findViewById(R.id.spn_hourlyGraphs_chooseGraph);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onGraphTopicSelected(parent.getItemAtPosition(position).toString(),position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Fade fade = new Fade();
        fade.setDuration(300);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setReturnTransition(fade);
        getWindow().setExitTransition(fade);
        getWindow().setReenterTransition(fade);

        chart = findViewById(R.id.hourlyGraphs_chart);
        chartBar = findViewById(R.id.hourlyGraphs_chartBar);

        spinner.setSelection(forecast.getCurrentGraph(),true);
    }

    private void onGraphTopicSelected(String graphTitle, int position) {

        graphDefinition.setText(WeatherDictionary.getGraphDescription(graphTitle));
        forecast.setCurrentGraph(position);

        switch(position){
            case 0:
                createGraphTemperature(hourLabels, chart);
                break;
            case 1:
                createGraphPrecipitation(hourLabels, chart);
                break;
            case 2:
                createGraphAirPressure(hourLabels, chart);
                break;
            case 3:
                createGraphHumidity(hourLabels, chart);
                break;
            case 4:
                createGraphCloudCover(hourLabels, chart);
                break;
            case 5:
                createGraphWindSpeed(hourLabels, chart);
                break;
            case 6:
                createGraphOzone(hourLabels, chart);
                break;
            case 7:
                createGraphUvIndex(hourLabels, chartBar);
                break;
            case 8:
                createGraphVisibility(hourLabels, chart);
                break;
            default:
                createGraphTemperature(hourLabels, chart);
                break;
        }
    }

    //*************************
    // CREATE GRAPH METHODS
    //*************************

    private void createGraphTemperature(String[] hourLabels, @NonNull LineChart chartTemp) {

        chart.setVisibility(View.VISIBLE);
        chartBar.setVisibility(View.INVISIBLE);

        List<Entry> entriesHighTemp = forecast.getHourlyGraphDataTemperature();

        ILineDataSet dataSet = new LineDataSet(entriesHighTemp, "Degrees Centigrade"); // add entries to dataset
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);

        XAxis xAxis = chartTemp.getXAxis();
        xAxis.setValueFormatter(new HourlyXAxisFormatter(hourLabels));
        xAxis.setTextSize(GRAPH_X_AXIS_TEXT_SIZE);
        xAxis.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(0);

        YAxis yAxisLeft = chartTemp.getAxisLeft();
        YAxis yAxisRight = chartTemp.getAxisRight();
        yAxisLeft.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        yAxisRight.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        setYAxisLowestVal(yAxisLeft, yAxisRight);
        setYAxisHighestVal(yAxisLeft, yAxisRight);

        Description description = new Description();
        description.setText("");
        chartTemp.setDescription(description);
        chartTemp.setXAxisRenderer(new LineGraphXAxisRenderer(chartTemp.getViewPortHandler(), chartTemp.getXAxis(), chartTemp.getTransformer(YAxis.AxisDependency.LEFT)));
        chartTemp.setData(lineData);
        chartTemp.setExtraOffsets(0f, 5f, 0f, 18f);
        Legend legend = chartTemp.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.getCalculatedLineSizes();
        chartTemp.setBackgroundColor(GRAPH_BACKGROUND_COLOUR);
        chartTemp.animateX(GRAPH_LINECHART_ANIMATION_MILLISECONDS); // show graph with animation
        //chartHighTemp.invalidate(); // refresh

    }

    private void setYAxisHighestVal(@NonNull YAxis yAxisLeft, @NonNull YAxis yAxisRight) {
        int highVal = forecast.getHighestHourlyHighTemp();
        if(highVal >= 40) {
            yAxisLeft.setAxisMaximum(50f);
            yAxisRight.setAxisMaximum(50f);
        } else if(highVal >= 35) {
            yAxisLeft.setAxisMaximum(40f);
            yAxisRight.setAxisMaximum(40f);
        } else if(highVal >= 30) {
            yAxisLeft.setAxisMaximum(35f);
            yAxisRight.setAxisMaximum(35f);
        } else if(highVal >= 25) {
            yAxisLeft.setAxisMaximum(30f);
            yAxisRight.setAxisMaximum(30f);
        } else if(highVal >= 20) {
            yAxisLeft.setAxisMaximum(25f);
            yAxisRight.setAxisMaximum(25f);
        } else {
            yAxisLeft.setAxisMaximum(20f);
            yAxisRight.setAxisMaximum(20f);
        }
    }

    private void setYAxisLowestVal(@NonNull YAxis yAxisLeft, @NonNull YAxis yAxisRight) {
        int lowVal = forecast.getLowestHourlyHighTemp();
        if(lowVal <= -20) {
            yAxisLeft.setAxisMinimum(-30f);
            yAxisRight.setAxisMinimum(-30f);
        } else if(lowVal <= -10) {
            yAxisLeft.setAxisMinimum(-20f);
            yAxisRight.setAxisMinimum(-20f);
        } else if(lowVal <= 0) {
            yAxisLeft.setAxisMinimum(-10f);
            yAxisRight.setAxisMinimum(-10f);
        } else if(lowVal <= 10) {
            yAxisLeft.setAxisMinimum(0f);
            yAxisRight.setAxisMinimum(0f);
        } else {
            yAxisLeft.setAxisMinimum(10f);
            yAxisRight.setAxisMinimum(10f);
        }
    }

    private void createGraphPrecipitation(String[] hourLabels, @NonNull LineChart chartPrecip) {

        chart.setVisibility(View.VISIBLE);
        chartBar.setVisibility(View.INVISIBLE);

        List<Entry> entriesPrecip = forecast.getHourlyGraphDataPrecipitation();

        ILineDataSet dataSet = new LineDataSet(entriesPrecip, "Probability (%)"); // add entries to dataset
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);

        XAxis xAxis = chartPrecip.getXAxis();
        xAxis.setValueFormatter(new HourlyXAxisFormatter(hourLabels));
        xAxis.setTextSize(GRAPH_X_AXIS_TEXT_SIZE);
        xAxis.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(0);

        YAxis yAxisLeft = chartPrecip.getAxisLeft();
        YAxis yAxisRight = chartPrecip.getAxisRight();
        yAxisLeft.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        yAxisRight.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        yAxisLeft.setAxisMinimum(0f);
        yAxisRight.setAxisMinimum(0f);
        yAxisLeft.setAxisMaximum(100f);
        yAxisRight.setAxisMaximum(100f);

        Description description = new Description();
        description.setText("");
        chartPrecip.setDescription(description);
        chartPrecip.setXAxisRenderer(new LineGraphXAxisRenderer(chartPrecip.getViewPortHandler(),
                chartPrecip.getXAxis(), chartPrecip.getTransformer(YAxis.AxisDependency.LEFT)));
        chartPrecip.setData(lineData);
        chartPrecip.setExtraOffsets(0f, 5f, 0f, 18f);
        Legend legend = chartPrecip.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.getCalculatedLineSizes();
        chartPrecip.setBackgroundColor(GRAPH_BACKGROUND_COLOUR);
        chartPrecip.animateX(GRAPH_LINECHART_ANIMATION_MILLISECONDS); // show graph with animation
        //chartHighTemp.invalidate(); // refresh

    }

    private void createGraphAirPressure(String[] hourLabels, @NonNull LineChart chartPressure) {

        chart.setVisibility(View.VISIBLE);
        chartBar.setVisibility(View.INVISIBLE);

        List<Entry> entriesPressure = forecast.getHourlyGraphDataAirPressure();

        ILineDataSet dataSet = new LineDataSet(entriesPressure, "Millibars"); // add entries to dataset
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);

        XAxis xAxis = chartPressure.getXAxis();
        xAxis.setValueFormatter(new HourlyXAxisFormatter(hourLabels));
        xAxis.setTextSize(GRAPH_X_AXIS_TEXT_SIZE);
        xAxis.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(0);

        YAxis yAxisLeft = chartPressure.getAxisLeft();
        YAxis yAxisRight = chartPressure.getAxisRight();
        yAxisLeft.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        yAxisRight.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        yAxisLeft.setAxisMinimum(940f);
        yAxisRight.setAxisMinimum(940f);
        yAxisLeft.setAxisMaximum(1060f);
        yAxisRight.setAxisMaximum(1060f);

        Description description = new Description();
        description.setText("");
        chartPressure.setDescription(description);
        chartPressure.setXAxisRenderer(new LineGraphXAxisRenderer(chartPressure.getViewPortHandler(),
                chartPressure.getXAxis(), chartPressure.getTransformer(YAxis.AxisDependency.LEFT)));
        chartPressure.setData(lineData);
        chartPressure.setExtraOffsets(0f, 5f, 0f, 18f);
        Legend legend = chartPressure.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.getCalculatedLineSizes();
        chartPressure.setBackgroundColor(GRAPH_BACKGROUND_COLOUR);
        chartPressure.animateX(GRAPH_LINECHART_ANIMATION_MILLISECONDS); // show graph with animation
        //chartHighTemp.invalidate(); // refresh

    }

    private void createGraphHumidity(String[] hourLabels, @NonNull LineChart chartHumidity) {

        chart.setVisibility(View.VISIBLE);
        chartBar.setVisibility(View.INVISIBLE);

        List<Entry> entriesHumidity = forecast.getHourlyGraphDataHumidity();

        ILineDataSet dataSet = new LineDataSet(entriesHumidity, "Relative Humidity (0-1)"); // add entries to dataset
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);

        XAxis xAxis = chartHumidity.getXAxis();
        xAxis.setValueFormatter(new HourlyXAxisFormatter(hourLabels));
        xAxis.setTextSize(GRAPH_X_AXIS_TEXT_SIZE);
        xAxis.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(0);

        YAxis yAxisLeft = chartHumidity.getAxisLeft();
        YAxis yAxisRight = chartHumidity.getAxisRight();
        yAxisLeft.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        yAxisRight.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        yAxisLeft.setAxisMinimum(0f);
        yAxisRight.setAxisMinimum(0f);
        yAxisLeft.setAxisMaximum(1.01f);
        yAxisRight.setAxisMaximum(1.01f);

        Description description = new Description();
        description.setText("");
        chartHumidity.setDescription(description);
        chartHumidity.setXAxisRenderer(new LineGraphXAxisRenderer(chartHumidity.getViewPortHandler(),
                chartHumidity.getXAxis(), chartHumidity.getTransformer(YAxis.AxisDependency.LEFT)));
        chartHumidity.setData(lineData);
        chartHumidity.setExtraOffsets(0f, 5f, 0f, 18f);
        Legend legend = chartHumidity.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.getCalculatedLineSizes();
        chartHumidity.setBackgroundColor(GRAPH_BACKGROUND_COLOUR);
        chartHumidity.animateX(GRAPH_LINECHART_ANIMATION_MILLISECONDS); // show graph with animation
        //chartHighTemp.invalidate(); // refresh

    }

    private void createGraphCloudCover(String[] hourLabels, @NonNull LineChart chartCloudCover) {

        chart.setVisibility(View.VISIBLE);
        chartBar.setVisibility(View.INVISIBLE);

        List<Entry> entriesCloudCover = forecast.getHourlyGraphDataCloudCover();

        ILineDataSet dataSet = new LineDataSet(entriesCloudCover, "% covered"); // add entries to dataset
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);

        XAxis xAxis = chartCloudCover.getXAxis();
        xAxis.setValueFormatter(new HourlyXAxisFormatter(hourLabels));
        xAxis.setTextSize(GRAPH_X_AXIS_TEXT_SIZE);
        xAxis.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(0);

        YAxis yAxisLeft = chartCloudCover.getAxisLeft();
        YAxis yAxisRight = chartCloudCover.getAxisRight();
        yAxisLeft.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        yAxisRight.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        yAxisLeft.setAxisMinimum(0f);
        yAxisRight.setAxisMinimum(0f);
        yAxisLeft.setAxisMaximum(100.01f);
        yAxisRight.setAxisMaximum(100.01f);

        Description description = new Description();
        description.setText("");
        chartCloudCover.setDescription(description);
        chartCloudCover.setXAxisRenderer(new LineGraphXAxisRenderer(chartCloudCover.getViewPortHandler(),
                chartCloudCover.getXAxis(), chartCloudCover.getTransformer(YAxis.AxisDependency.LEFT)));
        chartCloudCover.setData(lineData);
        chartCloudCover.setExtraOffsets(0f, 5f, 0f, 18f);
        Legend legend = chartCloudCover.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.getCalculatedLineSizes();
        chartCloudCover.setBackgroundColor(GRAPH_BACKGROUND_COLOUR);
        chartCloudCover.animateX(GRAPH_LINECHART_ANIMATION_MILLISECONDS); // show graph with animation
        //chartHighTemp.invalidate(); // refresh

    }

    private void createGraphVisibility(String[] hourLabels, @NonNull LineChart chartVisibility) {

        chart.setVisibility(View.VISIBLE);
        chartBar.setVisibility(View.INVISIBLE);

        List<Entry> entriesVisib = forecast.getHourlyGraphDataVisibility();

        ILineDataSet dataSet = new LineDataSet(entriesVisib, "Miles (max 10 miles)"); // add entries to dataset
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);

        XAxis xAxis = chartVisibility.getXAxis();
        xAxis.setValueFormatter(new HourlyXAxisFormatter(hourLabels));
        xAxis.setTextSize(GRAPH_X_AXIS_TEXT_SIZE);
        xAxis.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(0);

        YAxis yAxisLeft = chartVisibility.getAxisLeft();
        YAxis yAxisRight = chartVisibility.getAxisRight();
        yAxisLeft.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        yAxisRight.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        yAxisLeft.setAxisMinimum(0f);
        yAxisRight.setAxisMinimum(0f);
        yAxisLeft.setAxisMaximum(10.01f);
        yAxisRight.setAxisMaximum(10.01f);

        Description description = new Description();
        description.setText("");
        chartVisibility.setDescription(description);
        chartVisibility.setXAxisRenderer(new LineGraphXAxisRenderer(chartVisibility.getViewPortHandler(),
                chartVisibility.getXAxis(), chartVisibility.getTransformer(YAxis.AxisDependency.LEFT)));
        chartVisibility.setData(lineData);
        chartVisibility.setExtraOffsets(0f, 5f, 0f, 18f);
        Legend legend = chartVisibility.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.getCalculatedLineSizes();
        chartVisibility.setBackgroundColor(GRAPH_BACKGROUND_COLOUR);
        chartVisibility.animateX(GRAPH_LINECHART_ANIMATION_MILLISECONDS); // show graph with animation
        //chartHighTemp.invalidate(); // refresh

    }

    private void createGraphOzone(String[] hourLabels, @NonNull LineChart chartOzone) {

        chart.setVisibility(View.VISIBLE);
        chartBar.setVisibility(View.INVISIBLE);

        List<Entry> entriesOzone = forecast.getHourlyGraphDataOzoneLevels();

        ILineDataSet dataSet = new LineDataSet(entriesOzone, "Dobson Units"); // add entries to dataset
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);

        XAxis xAxis = chartOzone.getXAxis();
        xAxis.setValueFormatter(new HourlyXAxisFormatter(hourLabels));
        xAxis.setTextSize(GRAPH_X_AXIS_TEXT_SIZE);
        xAxis.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(0);

        YAxis yAxisLeft = chartOzone.getAxisLeft();
        YAxis yAxisRight = chartOzone.getAxisRight();
        yAxisLeft.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        yAxisRight.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        yAxisLeft.setAxisMinimum(0f);
        yAxisRight.setAxisMinimum(0f);
        yAxisLeft.setAxisMaximum(500.01f);
        yAxisRight.setAxisMaximum(500.01f);

        Description description = new Description();
        description.setText("");
        chartOzone.setDescription(description);
        chartOzone.setXAxisRenderer(new LineGraphXAxisRenderer(chartOzone.getViewPortHandler(),
                chartOzone.getXAxis(), chartOzone.getTransformer(YAxis.AxisDependency.LEFT)));
        chartOzone.setData(lineData);
        chartOzone.setExtraOffsets(0f, 5f, 0f, 18f);
        Legend legend = chartOzone.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.getCalculatedLineSizes();
        chartOzone.setBackgroundColor(GRAPH_BACKGROUND_COLOUR);
        chartOzone.animateX(GRAPH_LINECHART_ANIMATION_MILLISECONDS); // show graph with animation
        //chartHighTemp.invalidate(); // refresh
    }

    private void createGraphUvIndex(String[] hourLabels, @NonNull BarChart chartUvIndex) {

        chart.setVisibility(View.INVISIBLE);
        chartBar.setVisibility(View.VISIBLE);

        List<BarEntry> entriesUvIndex = forecast.getHourlyGraphDataUvIndex();

        BarDataSet dataSet = new BarDataSet(entriesUvIndex, "UV Index Value"); // add entries to dataset
        dataSet.setValueTextColor(GRAPH_DATASET_TEXT_COLOUR); // styling, ...
        dataSet.setColor(GRAPH_DATASET_BAR_COLOUR);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(GRAPH_BAR_WIDTH); // set custom bar width
        barData.setDrawValues(false);

        XAxis xAxis = chartUvIndex.getXAxis();
        xAxis.setValueFormatter(new DailyXAxisFormatter(hourLabels));
        xAxis.setTextSize(GRAPH_X_AXIS_TEXT_SIZE);
        xAxis.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxisLeft = chartUvIndex.getAxisLeft();
        YAxis yAxisRight = chartUvIndex.getAxisRight();
        yAxisLeft.setAxisMinimum(0f); // start at 0
        yAxisRight.setAxisMinimum(0f); // start at 0
        yAxisLeft.setAxisMaximum(12f); // the axis maximum is 12
        yAxisRight.setAxisMaximum(12f); // the axis maximum is 12
        Description description = new Description();
        description.setText("");
        chartUvIndex.setDescription(description);
        chartUvIndex.setXAxisRenderer(new LineGraphXAxisRenderer(chartUvIndex.getViewPortHandler(),
                chartUvIndex.getXAxis(), chartUvIndex.getTransformer(YAxis.AxisDependency.LEFT)));
        chartUvIndex.setData(barData);
        chartUvIndex.setExtraOffsets(0f, 5f, 0f, 18f);
        chartUvIndex.setBackgroundColor(GRAPH_BACKGROUND_COLOUR);
        Legend legend = chartUvIndex.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.getCalculatedLineSizes();
        chartUvIndex.setFitBars(true); // make the x-axis fit exactly all bars
        chartUvIndex.animateX(GRAPH_BARCHART_ANIMATION_MILLISECONDS); // show graph with animation
    }

    private void createGraphWindSpeed(String[] hourLabels, @NonNull LineChart chartWindSpeed) {

        chart.setVisibility(View.VISIBLE);
        chartBar.setVisibility(View.INVISIBLE);

        List<Entry> entriesWindSpeed = forecast.getHourlyGraphDataWindSpeed();

        ILineDataSet dataSet = new LineDataSet(entriesWindSpeed, "Miles per Hour"); // add entries to dataset
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);

        XAxis xAxis = chartWindSpeed.getXAxis();
        xAxis.setValueFormatter(new HourlyXAxisFormatter(hourLabels));
        xAxis.setTextSize(GRAPH_X_AXIS_TEXT_SIZE);
        xAxis.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(0);

        YAxis yAxisLeft = chartWindSpeed.getAxisLeft();
        YAxis yAxisRight = chartWindSpeed.getAxisRight();
        yAxisLeft.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        yAxisRight.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        yAxisLeft.setAxisMinimum(0f);
        yAxisRight.setAxisMinimum(0f);
        yAxisLeft.setAxisMaximum(60.01f);
        yAxisRight.setAxisMaximum(60.01f);

        Description description = new Description();
        description.setText("");
        chartWindSpeed.setDescription(description);
        chartWindSpeed.setXAxisRenderer(new LineGraphXAxisRenderer(chartWindSpeed.getViewPortHandler(),
                chartWindSpeed.getXAxis(), chartWindSpeed.getTransformer(YAxis.AxisDependency.LEFT)));
        chartWindSpeed.setData(lineData);
        chartWindSpeed.setExtraOffsets(0f, 5f, 0f, 18f);
        Legend legend = chartWindSpeed.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.getCalculatedLineSizes();
        chartWindSpeed.setBackgroundColor(GRAPH_BACKGROUND_COLOUR);
        chartWindSpeed.animateX(GRAPH_LINECHART_ANIMATION_MILLISECONDS); // show graph with animation
        //chartHighTemp.invalidate(); // refresh
    }


    //*************************
    // NAVIGATION BUTTONS
    //*************************

    // Go to Daily Graphs (NEXT 7 DAYS)
    public void fromHourlyGraphToDailyGraphOnClick(View view) {
        Intent intent = new Intent(getBaseContext(), DailyGraphsActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HourlyGraphsActivity.this);
        startActivity(intent, options.toBundle());
    }

    // BACK - to Hourly forecast
    public void fromHourlyGraphsBackToHourlyForecastOnClick(View view) {
        forecast.setCurrentGraph(0);
        Intent intent = new Intent(getBaseContext(), HourlyForecastActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HourlyGraphsActivity.this);
        startActivity(intent, options.toBundle());
    }
}
