package edu.smartdoor.imank.smartdoor;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SettingsActivity extends BaseActivity {

    TextView updateEvery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        updateEvery = (TextView) findViewById(R.id.tv_ping);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/Montserrat-Regular.otf");
        updateEvery.setTypeface(typeface);
    }

}
