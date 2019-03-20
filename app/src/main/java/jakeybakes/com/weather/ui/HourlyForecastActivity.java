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
import jakeybakes.com.weather.adapters.HourlyAdapter;
import jakeybakes.com.weather.databinding.ActivityHourlyForecastBinding;
import jakeybakes.com.weather.weather.Forecast;
import jakeybakes.com.weather.weather.Hour;

public class HourlyForecastActivity extends AppCompatActivity {
public static final String TAG = HourlyForecastActivity.class.getSimpleName();
    private HourlyAdapter adapter;
    private ActivityHourlyForecastBinding binding;
    private Forecast forecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        forecast = (Forecast) getApplicationContext();
        Hour[] hours = forecast.getHourlyForecast();
        List<Hour> hoursList = new ArrayList<>(Arrays.asList(hours));
        String hourlySummary = forecast.getHourlySummary();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_hourly_forecast);
        binding.setPageSummary(hourlySummary);
        adapter = new HourlyAdapter(hoursList, this, this);
        binding.hourlyListView.setAdapter(adapter);
        binding.hourlyListView.setHasFixedSize(true);
        binding.hourlyListView.setLayoutManager(new LinearLayoutManager(this));
        binding.hourlyListView.addItemDecoration(new DividerItemDecoration(binding.hourlyListView.getContext(),
                DividerItemDecoration.VERTICAL));

        // set Dark Sky hyperlink action
        TextView darkSky = findViewById(R.id.txtv_hourlyForecast_attribution);
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

    public void fromHourlyToMainOnClick(View view){
        Intent intent = new Intent(this, MainActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HourlyForecastActivity.this);
        startActivity(intent, options.toBundle());
    }

    public void fromHourlyToHourlyGraphs(View view){
        Intent intent = new Intent(this, HourlyGraphsActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HourlyForecastActivity.this);
        startActivity(intent, options.toBundle());
    }

    public void fromHourlyToHourDetails(View view){
        Hour hour = (Hour)view.getTag();
        forecast.setCurrentHour(hour);
        Intent intent = new Intent(this, HourActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HourlyForecastActivity.this);
        startActivity(intent, options.toBundle());
    }

}