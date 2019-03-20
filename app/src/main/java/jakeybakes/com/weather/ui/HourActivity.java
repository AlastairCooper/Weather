package jakeybakes.com.weather.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import jakeybakes.com.weather.R;
import jakeybakes.com.weather.databinding.ActivityHourBinding;
import jakeybakes.com.weather.weather.Forecast;
import jakeybakes.com.weather.weather.WeatherDictionary;

public class HourActivity extends AppCompatActivity {
    public static final String TAG = HourActivity.class.getSimpleName();
    private Forecast forecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forecast = (Forecast)getApplicationContext();
        final ActivityHourBinding binding = DataBindingUtil.setContentView(HourActivity.this,
                R.layout.activity_hour);
        binding.setHour(forecast.getCurrentHour());
        binding.setLocation(forecast.getLocation());

        Fade fade = new Fade();
        fade.setDuration(300);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setReturnTransition(fade);
        getWindow().setExitTransition(fade);
        getWindow().setReenterTransition(fade);
    }

    public void toastDefinition(View view){
        TextView txt = (TextView)view;
        String str = (String)((TextView) view).getText();
        Toast.makeText(this, WeatherDictionary.getDefinition(str),Toast.LENGTH_LONG).show();
    }

    public void fromHourToHourlyOnClick(View view){
        Intent intent = new Intent(this, HourlyForecastActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HourActivity.this);
        startActivity(intent, options.toBundle());
    }

}
