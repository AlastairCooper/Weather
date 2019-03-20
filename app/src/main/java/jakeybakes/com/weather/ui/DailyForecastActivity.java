package jakeybakes.com.weather.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.method.LinkMovementMethod;
import android.transition.Fade;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakeybakes.com.weather.R;
import jakeybakes.com.weather.adapters.DailyAdapter;
import jakeybakes.com.weather.databinding.ActivityDailyForecastBinding;
import jakeybakes.com.weather.weather.Day;
import jakeybakes.com.weather.weather.Forecast;

public class DailyForecastActivity extends AppCompatActivity {
    public static final String TAG = DailyForecastActivity.class.getSimpleName();
    private DailyAdapter adapter;
    private ActivityDailyForecastBinding binding;
    private Forecast forecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get weather data from (global variable) forecast
        forecast = (Forecast) getApplicationContext();
        Day[] days = forecast.getDailyForecast();
        List<Day> daysList = new ArrayList<>(Arrays.asList(days));
        String dailySummary = forecast.getDailySummary();

        // bind data to activity views
        binding = DataBindingUtil.setContentView(this, R.layout.activity_daily_forecast);
        binding.setPageSummary(dailySummary);
        adapter = new DailyAdapter(daysList, this, this);
        binding.dailyListView.setAdapter(adapter);
        binding.dailyListView.setHasFixedSize(true);
        binding.dailyListView.setLayoutManager(new LinearLayoutManager(this));
        binding.dailyListView.addItemDecoration(new DividerItemDecoration(binding.dailyListView.getContext(),
                DividerItemDecoration.VERTICAL));

        // set Dark Sky hyperlink action
        TextView darkSky = findViewById(R.id.txtv_dailyForecast_attribution);
        darkSky.setMovementMethod(LinkMovementMethod.getInstance());

        // set Activity transition
        Fade fade = new Fade();
        fade.setDuration(300);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setReturnTransition(fade);
        getWindow().setExitTransition(fade);
        getWindow().setReenterTransition(fade);
    }

    public void fromDailyToMainOnClick(View view){
        Intent intent = new Intent(this, MainActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(DailyForecastActivity.this);
        startActivity(intent, options.toBundle());
    }

    public void fromDailyToDailyGraphs(View view){
        Intent intent = new Intent(this, DailyGraphsActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(DailyForecastActivity.this);
        startActivity(intent, options.toBundle());
    }

    public void dailyMoreDetailsOnClick(View view){
        Day day = (Day)view.getTag();
        forecast.setCurrentDay(day);
        Intent intent = new Intent(this, DayActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(DailyForecastActivity.this);
        startActivity(intent, options.toBundle());
    }

}
