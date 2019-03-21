package jakeybakes.com.weather.ui;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import jakeybakes.com.weather.R;

public class SaveLocationActivity extends AppCompatActivity {
    public static final String TAG = SaveLocationActivity.class.getSimpleName();
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String latChosen;
    private String longChosen;
    private String locChosen;
    private TextView txtv_latitude;
    private TextView txtv_longitude;
    private TextView txtv_location;
    private EditText edtx_location;
    private Button btn_save;
    private Button btn_backToMap;
    private Button btn_myLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_location);

        mSharedPreferences = getSharedPreferences(LocationActivity.PREFS_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        Intent intent = getIntent();
        latChosen = intent.getStringExtra("latitude");
        longChosen = intent.getStringExtra("longitude");
        locChosen = intent.getStringExtra("location");
        txtv_latitude = findViewById(R.id.txtv_savLoc_latitude);
        txtv_longitude = findViewById(R.id.txtv_savLoc_longitude);
        txtv_location = findViewById(R.id.txtv_savLoc_location);
        edtx_location = findViewById(R.id.edtx_savLoc_locationNameToSave);
        txtv_latitude.setText(latChosen);
        txtv_longitude.setText(longChosen);
        txtv_location.setText(locChosen);
        edtx_location.setText(locChosen);
        btn_save = findViewById(R.id.btn_savLoc_save);
        btn_save.setOnClickListener((v)-> saveToSharedPreferences());
        btn_backToMap = findViewById(R.id.btn_savLoc_BackToMap);
        btn_backToMap.setOnClickListener((v)-> goBackToMapOnClick());
        btn_myLocations = findViewById(R.id.btn_savLoc_myLocations);
        btn_myLocations.setOnClickListener((v)-> goToMyLocationsOnClick());

        Fade fade = new Fade();
        fade.setDuration(300);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setReturnTransition(fade);
        getWindow().setExitTransition(fade);
        getWindow().setReenterTransition(fade);
    }

    public void saveToSharedPreferences(){
        EditText edt = findViewById(R.id.edtx_savLoc_locationNameToSave);
        String locSaveName = edt.getText().toString();
        if(locSaveName.contains("#") || locSaveName.contains("_")){
            Toast.makeText(this, "Names cannot include '#' or '_' characters",Toast.LENGTH_LONG).show();
        } else {
            if (locSaveName.equals("")) {
                locSaveName = locChosen;
            }
            String saveValue = latChosen + "_" + longChosen + "_" + locSaveName;
            String existingString = mSharedPreferences.getString(LocationActivity.KEY_SAVED_LOCATIONS, "");
            if (existingString.equals("")) {
                mEditor.putString(LocationActivity.KEY_SAVED_LOCATIONS, saveValue);
            } else {
                mEditor.putString(LocationActivity.KEY_SAVED_LOCATIONS, existingString + "#" + saveValue);
            }
            mEditor.apply();
            goBackToMapOnClick();
        }
    }

    private void goBackToMapOnClick() {
        Intent intent = new Intent(getBaseContext(), LocationActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SaveLocationActivity.this);
        startActivity(intent, options.toBundle());
    }

    private void goToMyLocationsOnClick() {
        Intent intent = new Intent(getBaseContext(), LocationsLibraryActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SaveLocationActivity.this);
        startActivity(intent, options.toBundle());
    }

}
