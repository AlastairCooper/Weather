package jakeybakes.com.weather.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
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
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.List;

import androidx.annotation.NonNull;
import jakeybakes.com.weather.R;
import jakeybakes.com.weather.adapters.DailyXAxisFormatter;
import jakeybakes.com.weather.adapters.Float1dpDataValueFormatter;
import jakeybakes.com.weather.adapters.Float2dpDataValueFormatter;
import jakeybakes.com.weather.adapters.IntegerDataValueFormatter;
import jakeybakes.com.weather.adapters.PercentDataValueFormatter;
import jakeybakes.com.weather.weather.Forecast;
import jakeybakes.com.weather.weather.WeatherDictionary;

public class DailyGraphsActivity extends AppCompatActivity {
    private static final int GRAPH_X_AXIS_TEXT_SIZE = 12;
    private static final int GRAPH_X_AXIS_TEXT_COLOUR = Color.argb(205,255,255,255);
    private static final float GRAPH_BAR_WIDTH = 0.8f;
    private static final int GRAPH_DATASET_TEXT_COLOUR = Color.WHITE;
    private static final int GRAPH_DATASET_BORDER_COLOUR = Color.rgb(159,44,48);
    private static final int GRAPH_DATASET_BAR_COLOUR = Color.rgb(48,63,159);
    private static final int GRAPH_BACKGROUND_COLOUR = Color.rgb(16,131,236);
    private static final float GRAPH_DATASET_BORDER_WIDTH = 1f;
    private static final float GRAPH_LEGEND_SET_Y_OFFSET = 5f;
    private static final int GRAPH_DATASET_SHADOW_COLOR = Color.rgb(100,100,100);
    private static final int GRAPH_BARCHART_ANIMATION_MILLISECONDS = 700;

    private Forecast forecast;
    private String[] dayLabels;
    private BarChart chart;
    private TextView dateToDateTitle;
    private Spinner spinner;
    private TextView graphDefinition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_graphs);

        // set Dark Sky hyperlink action
        TextView darkSky = findViewById(R.id.txtv_dailyGraphs_attribution);
        darkSky.setMovementMethod(LinkMovementMethod.getInstance());
        forecast = (Forecast) getApplicationContext();
        dayLabels = forecast.getDayLabels();
        graphDefinition = findViewById(R.id.txtv_graphDefinition);

        dateToDateTitle = (TextView) findViewById(R.id.txtv_dailyGraphs_dateTitle);
        dateToDateTitle.setText(forecast.getDailyForecastDateString());

        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,R.array.daily_graph_options,R.layout.choose_graph_spinner);
        spinner = (Spinner) findViewById(R.id.spn_dailyGraphs_chooseGraph);
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

        chart = (BarChart) findViewById(R.id.dailyGraphs_chart);
        spinner.setSelection(forecast.getCurrentGraph(),true);

    }

    public void onGraphTopicSelected(String graphTitle, int position){

        graphDefinition.setText(WeatherDictionary.getGraphDescription(graphTitle));
        forecast.setCurrentGraph(position);

        switch(position){
            case 0:
                createGraphHighTemperature(dayLabels, chart);
                break;
            case 1:
                createGraphPrecipitation(dayLabels, chart);
                break;
            case 2:
                createGraphAirPressure(dayLabels, chart);
                break;
            case 3:
                createGraphHumidity(dayLabels, chart);
                break;
            case 4:
                createGraphCloudCover(dayLabels, chart);
                break;
            case 5:
                createGraphWindSpeed(dayLabels, chart);
                break;
            case 6:
                createGraphOzone(dayLabels, chart);
                break;
            case 7:
                createGraphUvIndex(dayLabels, chart);
                break;
            case 8:
                createGraphVisibility(dayLabels, chart);
                break;
            default:
                createGraphHighTemperature(dayLabels, chart);
                break;
        }
    }

    //*************************
    // CREATE GRAPH METHODS
    //*************************

    private void createGraphHighTemperature(String[] dayLabels, @NonNull BarChart chartHighTemp) {
        List<BarEntry> entriesHighTemp = forecast.getDailyGraphDataTemperature();

        IBarDataSet dataSet = new BarDataSet(entriesHighTemp, "Degrees Centigrade"); // add entries to dataset
        dataSet.setValueTextColor(GRAPH_DATASET_TEXT_COLOUR); // styling, ...
        ((BarDataSet) dataSet).setBarBorderColor(GRAPH_DATASET_BORDER_COLOUR);
        ((BarDataSet) dataSet).setBarBorderWidth(GRAPH_DATASET_BORDER_WIDTH);
        ((BarDataSet) dataSet).setColor(GRAPH_DATASET_BAR_COLOUR);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(GRAPH_BAR_WIDTH); // set custom bar width
        barData.setValueFormatter(new Float1dpDataValueFormatter());

        XAxis xAxis = chartHighTemp.getXAxis();
        xAxis.setValueFormatter(new DailyXAxisFormatter(dayLabels));
        xAxis.setTextSize(GRAPH_X_AXIS_TEXT_SIZE);
        xAxis.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        YAxis yAxisLeft = chartHighTemp.getAxisLeft();
        YAxis yAxisRight = chartHighTemp.getAxisRight();
        yAxisLeft.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        yAxisRight.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        if(forecast.getLowestDailyHighTemp() < 0) {
            yAxisLeft.setAxisMinimum(-10f); // start at -10
            yAxisRight.setAxisMinimum(-10f); // start at -10
        } else{
            yAxisLeft.setAxisMinimum(0f); // start at 0
            yAxisRight.setAxisMinimum(0f); // start at 0
        }
        if(forecast.getHighestDailyHighTemp() < 25) {
            yAxisLeft.setAxisMaximum(25f); // the axis maximum is 25
            yAxisRight.setAxisMaximum(25f); // the axis maximum is 25
        } else{
            yAxisLeft.setAxisMaximum(40f); // the axis maximum is 40
            yAxisRight.setAxisMaximum(40f); // the axis maximum is 40
        }
        Description description = new Description();
        description.setText("");
        chartHighTemp.setDescription(description);
        chartHighTemp.setData(barData);
        chartHighTemp.setBackgroundColor(GRAPH_BACKGROUND_COLOUR);
        chartHighTemp.getLegend().setEnabled(true);   // sets the legend at the bottom of the graph to visible
        chartHighTemp.getLegend().setYOffset(GRAPH_LEGEND_SET_Y_OFFSET);  // adds padding to bottom of legend ???
        chartHighTemp.setFitBars(true); // make the x-axis fit exactly all bars
        chartHighTemp.animateX(GRAPH_BARCHART_ANIMATION_MILLISECONDS); // show graph with animation
        //chartHighTemp.invalidate(); // refresh

    }

    private void createGraphPrecipitation(String[] dayLabels, @NonNull BarChart chartPrecipitation) {
        List<BarEntry> entriesPrecip = forecast.getDailyGraphDataPrecipitation();

        IBarDataSet dataSet = new BarDataSet(entriesPrecip, "%"); // add entries to dataset
        dataSet.setValueTextColor(GRAPH_DATASET_TEXT_COLOUR); // styling, ...
        ((BarDataSet) dataSet).setBarBorderColor(GRAPH_DATASET_BORDER_COLOUR);
        ((BarDataSet) dataSet).setBarBorderWidth(GRAPH_DATASET_BORDER_WIDTH);
        ((BarDataSet) dataSet).setColor(GRAPH_DATASET_BAR_COLOUR);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(GRAPH_BAR_WIDTH); // set custom bar width
        barData.setValueFormatter(new PercentDataValueFormatter());

        XAxis xAxis = chartPrecipitation.getXAxis();
        xAxis.setValueFormatter(new DailyXAxisFormatter(dayLabels));
        xAxis.setTextSize(GRAPH_X_AXIS_TEXT_SIZE);
        xAxis.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        YAxis yAxisLeft = chartPrecipitation.getAxisLeft();
        YAxis yAxisRight = chartPrecipitation.getAxisRight();
            yAxisLeft.setAxisMinimum(0f); // start at 0
            yAxisRight.setAxisMinimum(0f); // start at 0
            yAxisLeft.setAxisMaximum(100f); // the axis maximum is 100
            yAxisRight.setAxisMaximum(100f); // the axis maximum is 100
        Description description = new Description();
        description.setText("");
        chartPrecipitation.setDescription(description);
        chartPrecipitation.setData(barData);
        chartPrecipitation.setBackgroundColor(GRAPH_BACKGROUND_COLOUR);
        chartPrecipitation.getLegend().setEnabled(true);   // sets the legend at the bottom of the graph to visible
        chartPrecipitation.getLegend().setYOffset(GRAPH_LEGEND_SET_Y_OFFSET);  // adds padding to bottom of legend ???
        chartPrecipitation.setFitBars(true); // make the x-axis fit exactly all bars
        chartPrecipitation.animateX(GRAPH_BARCHART_ANIMATION_MILLISECONDS); // show graph with animation
        //chartHighTemp.invalidate(); // refresh
    }

    private void createGraphUvIndex(String[] dayLabels, @NonNull BarChart chartUvIndex) {
        List<BarEntry> entriesUvIndex = forecast.getDailyGraphDataUvIndex();

        IBarDataSet dataSet = new BarDataSet(entriesUvIndex, "UV Index Value"); // add entries to dataset
        dataSet.setValueTextColor(GRAPH_DATASET_TEXT_COLOUR); // styling, ...
        ((BarDataSet) dataSet).setBarBorderColor(GRAPH_DATASET_BORDER_COLOUR);
        ((BarDataSet) dataSet).setBarBorderWidth(GRAPH_DATASET_BORDER_WIDTH);
        ((BarDataSet) dataSet).setColor(GRAPH_DATASET_BAR_COLOUR);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(GRAPH_BAR_WIDTH); // set custom bar width
        barData.setValueFormatter(new IntegerDataValueFormatter());

        XAxis xAxis = chartUvIndex.getXAxis();
        xAxis.setValueFormatter(new DailyXAxisFormatter(dayLabels));
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
        chartUvIndex.setData(barData);
        chartUvIndex.setBackgroundColor(GRAPH_BACKGROUND_COLOUR);
        chartUvIndex.getLegend().setEnabled(true);   // sets the legend at the bottom of the graph to visible
        chartUvIndex.getLegend().setYOffset(GRAPH_LEGEND_SET_Y_OFFSET);  // adds padding to bottom of legend ???
        chartUvIndex.setFitBars(true); // make the x-axis fit exactly all bars
        chartUvIndex.animateX(GRAPH_BARCHART_ANIMATION_MILLISECONDS); // show graph with animation
        //chartUvIndex.invalidate(); // refresh
    }

    private void createGraphCloudCover(String[] dayLabels, @NonNull BarChart chartCloudCover) {

        List<BarEntry> entriesCloudCover = forecast.getDailyGraphDataCloudCover();

        IBarDataSet dataSet = new BarDataSet(entriesCloudCover, "% Sky Covered"); // add entries to dataset
        dataSet.setValueTextColor(GRAPH_DATASET_TEXT_COLOUR); // styling, ...
        ((BarDataSet) dataSet).setBarBorderColor(GRAPH_DATASET_BORDER_COLOUR);
        ((BarDataSet) dataSet).setBarBorderWidth(GRAPH_DATASET_BORDER_WIDTH);
        ((BarDataSet) dataSet).setColor(GRAPH_DATASET_BAR_COLOUR);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(GRAPH_BAR_WIDTH); // set custom bar width
        barData.setValueFormatter(new PercentDataValueFormatter());

        XAxis xAxis = chartCloudCover.getXAxis();
        xAxis.setValueFormatter(new DailyXAxisFormatter(dayLabels));
        xAxis.setTextSize(GRAPH_X_AXIS_TEXT_SIZE);
        xAxis.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxisLeft = chartCloudCover.getAxisLeft();
        YAxis yAxisRight = chartCloudCover.getAxisRight();
        yAxisLeft.setAxisMinimum(0f); // start at 0
        yAxisRight.setAxisMinimum(0f); // start at 0
        yAxisLeft.setAxisMaximum(100f); // the axis maximum is 100
        yAxisRight.setAxisMaximum(100f); // the axis maximum is 100

        Description description = new Description();
        description.setText("");
        chartCloudCover.setDescription(description);

        chartCloudCover.setData(barData);
        chartCloudCover .setBackgroundColor(GRAPH_BACKGROUND_COLOUR);
        chartCloudCover.getLegend().setEnabled(true);   // sets the legend at the bottom of the graph to visible
        chartCloudCover.getLegend().setYOffset(GRAPH_LEGEND_SET_Y_OFFSET);  // adds padding to bottom of legend ???
        chartCloudCover.setFitBars(true); // make the x-axis fit exactly all bars
        chartCloudCover.animateX(GRAPH_BARCHART_ANIMATION_MILLISECONDS); // show graph with animation
        //chartCloudCover.invalidate(); // refresh
    }

    private void createGraphHumidity(String[] dayLabels, @NonNull BarChart chartHumidity) {

        List<BarEntry> entriesHumidity = forecast.getDailyGraphDataHumidity();

        IBarDataSet dataSet = new BarDataSet(entriesHumidity, "Relative Humidity"); // add entries to dataset
        dataSet.setValueTextColor(GRAPH_DATASET_TEXT_COLOUR); // styling, ...
        ((BarDataSet) dataSet).setBarBorderColor(GRAPH_DATASET_BORDER_COLOUR);
        ((BarDataSet) dataSet).setBarBorderWidth(GRAPH_DATASET_BORDER_WIDTH);
        ((BarDataSet) dataSet).setColor(GRAPH_DATASET_BAR_COLOUR);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(GRAPH_BAR_WIDTH); // set custom bar width
        barData.setValueFormatter(new Float2dpDataValueFormatter());

        XAxis xAxis = chartHumidity.getXAxis();
        xAxis.setValueFormatter(new DailyXAxisFormatter(dayLabels));
        xAxis.setTextSize(GRAPH_X_AXIS_TEXT_SIZE);
        xAxis.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxisLeft = chartHumidity.getAxisLeft();
        YAxis yAxisRight = chartHumidity.getAxisRight();
        yAxisLeft.setAxisMinimum(0f); // start at 0
        yAxisRight.setAxisMinimum(0f); // start at 0
        yAxisLeft.setAxisMaximum(1.01f); // the axis maximum is 1
        yAxisRight.setAxisMaximum(1.01f); // the axis maximum is 1

        Description description = new Description();
        description.setText("");
        chartHumidity.setDescription(description);

        chartHumidity.setData(barData);
        chartHumidity .setBackgroundColor(GRAPH_BACKGROUND_COLOUR);
        chartHumidity.getLegend().setEnabled(true);   // sets the legend at the bottom of the graph to visible
        chartHumidity.getLegend().setYOffset(GRAPH_LEGEND_SET_Y_OFFSET);  // adds padding to bottom of legend ???
        chartHumidity.setFitBars(true); // make the x-axis fit exactly all bars
        chartHumidity.animateX(GRAPH_BARCHART_ANIMATION_MILLISECONDS); // show graph with animation
        //chartHumidity.invalidate(); // refresh
    }

    private void createGraphOzone(String[] dayLabels, @NonNull BarChart chartOzone) {

        List<BarEntry> entriesOzone = forecast.getDailyGraphDataOzoneLevel();

        IBarDataSet dataSet = new BarDataSet(entriesOzone, "Dobson Units"); // add entries to dataset
        dataSet.setValueTextColor(GRAPH_DATASET_TEXT_COLOUR); // styling, ...
        ((BarDataSet) dataSet).setBarBorderColor(GRAPH_DATASET_BORDER_COLOUR);
        ((BarDataSet) dataSet).setBarBorderWidth(GRAPH_DATASET_BORDER_WIDTH);
        ((BarDataSet) dataSet).setColor(GRAPH_DATASET_BAR_COLOUR);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(GRAPH_BAR_WIDTH); // set custom bar width
        barData.setValueFormatter(new IntegerDataValueFormatter());

        XAxis xAxis = chartOzone.getXAxis();
        xAxis.setValueFormatter(new DailyXAxisFormatter(dayLabels));
        xAxis.setTextSize(GRAPH_X_AXIS_TEXT_SIZE);
        xAxis.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxisLeft = chartOzone.getAxisLeft();
        YAxis yAxisRight = chartOzone.getAxisRight();
        yAxisLeft.setAxisMinimum(0f); // start at 0
        yAxisRight.setAxisMinimum(0f); // start at 0
        yAxisLeft.setAxisMaximum(500f); // the axis maximum is 500
        yAxisRight.setAxisMaximum(500f); // the axis maximum is 500

        Description description = new Description();
        description.setText("");
        chartOzone.setDescription(description);

        chartOzone.setData(barData);
        chartOzone .setBackgroundColor(GRAPH_BACKGROUND_COLOUR);
        chartOzone.getLegend().setEnabled(true);   // sets the legend at the bottom of the graph to visible
        chartOzone.getLegend().setYOffset(GRAPH_LEGEND_SET_Y_OFFSET);  // adds padding to bottom of legend ???
        chartOzone.setFitBars(true); // make the x-axis fit exactly all bars
        chartOzone.animateX(GRAPH_BARCHART_ANIMATION_MILLISECONDS); // show graph with animation
        //chartOzone.invalidate(); // refresh
    }

    private void createGraphAirPressure(String[] dayLabels, @NonNull BarChart chartPressure) {

        List<BarEntry> entriesPressure = forecast.getDailyGraphDataAirPressure();

        IBarDataSet dataSet = new BarDataSet(entriesPressure, "Millibars"); // add entries to dataset
        dataSet.setValueTextColor(GRAPH_DATASET_TEXT_COLOUR); // styling, ...
        ((BarDataSet) dataSet).setBarBorderColor(GRAPH_DATASET_BORDER_COLOUR);
        ((BarDataSet) dataSet).setBarBorderWidth(GRAPH_DATASET_BORDER_WIDTH);
        ((BarDataSet) dataSet).setColor(GRAPH_DATASET_BAR_COLOUR);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(GRAPH_BAR_WIDTH); // set custom bar width
        barData.setValueFormatter(new IntegerDataValueFormatter());

        XAxis xAxis = chartPressure.getXAxis();
        xAxis.setValueFormatter(new DailyXAxisFormatter(dayLabels));
        xAxis.setTextSize(GRAPH_X_AXIS_TEXT_SIZE);
        xAxis.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxisLeft = chartPressure.getAxisLeft();
        YAxis yAxisRight = chartPressure.getAxisRight();
        yAxisLeft.setAxisMinimum(950f); // start at 0
        yAxisRight.setAxisMinimum(950f); // start at 0
        yAxisLeft.setAxisMaximum(1050f); // the axis maximum is 10
        yAxisRight.setAxisMaximum(1050f); // the axis maximum is 10

        Description description = new Description();
        description.setText("");
        chartPressure.setDescription(description);

        chartPressure.setData(barData);
        chartPressure .setBackgroundColor(GRAPH_BACKGROUND_COLOUR);
        chartPressure.getLegend().setEnabled(true);   // sets the legend at the bottom of the graph to visible
        chartPressure.getLegend().setYOffset(GRAPH_LEGEND_SET_Y_OFFSET);  // adds padding to bottom of legend ???
        chartPressure.setFitBars(true); // make the x-axis fit exactly all bars
        chartPressure.animateX(GRAPH_BARCHART_ANIMATION_MILLISECONDS); // show graph with animation
        //chartPressure.invalidate(); // refresh
    }

    private void createGraphWindSpeed(String[] dayLabels, @NonNull BarChart chartWindSpeed) {

        List<BarEntry> entriesWindSpeed = forecast.getDailyGraphDataWindSpeed();

        IBarDataSet dataSet = new BarDataSet(entriesWindSpeed, "Miles per Hour"); // add entries to dataset
        dataSet.setValueTextColor(GRAPH_DATASET_TEXT_COLOUR); // styling, ...
        ((BarDataSet) dataSet).setBarBorderColor(GRAPH_DATASET_BORDER_COLOUR);
        ((BarDataSet) dataSet).setBarBorderWidth(GRAPH_DATASET_BORDER_WIDTH);
        ((BarDataSet) dataSet).setColor(GRAPH_DATASET_BAR_COLOUR);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(GRAPH_BAR_WIDTH); // set custom bar width
        barData.setValueFormatter(new IntegerDataValueFormatter());

        XAxis xAxis = chartWindSpeed.getXAxis();
        xAxis.setValueFormatter(new DailyXAxisFormatter(dayLabels));
        xAxis.setTextSize(GRAPH_X_AXIS_TEXT_SIZE);
        xAxis.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxisLeft = chartWindSpeed.getAxisLeft();
        YAxis yAxisRight = chartWindSpeed.getAxisRight();
        yAxisLeft.setAxisMinimum(0f); // start at 0
        yAxisRight.setAxisMinimum(0f); // start at 0
        yAxisLeft.setAxisMaximum(50f); // the axis maximum is 10
        yAxisRight.setAxisMaximum(50f); // the axis maximum is 10

        Description description = new Description();
        description.setText("");
        chartWindSpeed.setDescription(description);

        chartWindSpeed.setData(barData);
        chartWindSpeed .setBackgroundColor(GRAPH_BACKGROUND_COLOUR);
        chartWindSpeed.getLegend().setEnabled(true);   // sets the legend at the bottom of the graph to visible
        chartWindSpeed.getLegend().setYOffset(GRAPH_LEGEND_SET_Y_OFFSET);  // adds padding to bottom of legend ???
        chartWindSpeed.setFitBars(true); // make the x-axis fit exactly all bars
        chartWindSpeed.animateX(GRAPH_BARCHART_ANIMATION_MILLISECONDS); // show graph with animation
        //chartWindSpeed.invalidate(); // refresh
    }

    private void createGraphVisibility(String[] dayLabels, @NonNull BarChart chartVisibility) {

        List<BarEntry> entriesVisibility = forecast.getDailyGraphDataVisibility();

        IBarDataSet dataSet = new BarDataSet(entriesVisibility, "Miles (max 10 miles)"); // add entries to dataset
        dataSet.setValueTextColor(GRAPH_DATASET_TEXT_COLOUR); // styling, ...
        ((BarDataSet) dataSet).setBarBorderColor(GRAPH_DATASET_BORDER_COLOUR);
        ((BarDataSet) dataSet).setBarBorderWidth(GRAPH_DATASET_BORDER_WIDTH);
        ((BarDataSet) dataSet).setColor(GRAPH_DATASET_BAR_COLOUR);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(GRAPH_BAR_WIDTH); // set custom bar width
        barData.setValueFormatter(new Float2dpDataValueFormatter());

        XAxis xAxis = chartVisibility.getXAxis();
        xAxis.setValueFormatter(new DailyXAxisFormatter(dayLabels));
        xAxis.setTextSize(GRAPH_X_AXIS_TEXT_SIZE);
        xAxis.setTextColor(GRAPH_X_AXIS_TEXT_COLOUR);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxisLeft = chartVisibility.getAxisLeft();
        YAxis yAxisRight = chartVisibility.getAxisRight();
        yAxisLeft.setAxisMinimum(0f); // start at 0
        yAxisRight.setAxisMinimum(0f); // start at 0
        yAxisLeft.setAxisMaximum(10f); // the axis maximum is 10
        yAxisRight.setAxisMaximum(10f); // the axis maximum is 10

        Description description = new Description();
        description.setText("");
        chartVisibility.setDescription(description);

        chartVisibility.setData(barData);
        chartVisibility .setBackgroundColor(GRAPH_BACKGROUND_COLOUR);
        chartVisibility.getLegend().setEnabled(true);   // sets the legend at the bottom of the graph to visible
        chartVisibility.getLegend().setYOffset(GRAPH_LEGEND_SET_Y_OFFSET);  // adds padding to bottom of legend ???
        chartVisibility.setFitBars(true); // make the x-axis fit exactly all bars
        chartVisibility.animateX(GRAPH_BARCHART_ANIMATION_MILLISECONDS); // show graph with animation
        //chartVisibility.invalidate(); // refresh
    }


    //*************************
    // NAVIGATION BUTTONS
    //*************************

    // Go to Hourly Graphs (NEXT 48 HOURS)
    public void fromDailyGraphToHourlyGraphsOnClick(View view) {
        Intent intent = new Intent(getBaseContext(), HourlyGraphsActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(DailyGraphsActivity.this);
        startActivity(intent, options.toBundle());
    }

    // BACK to - Daily forecast
    public void fromDailyGraphToDailyForecastOnClick(View view) {
        forecast.setCurrentGraph(0);
        Intent intent = new Intent(getBaseContext(), DailyForecastActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(DailyGraphsActivity.this);
        startActivity(intent, options.toBundle());
    }

}
