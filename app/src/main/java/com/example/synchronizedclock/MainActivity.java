package com.example.synchronizedclock;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView clockTextView;
    private Button formatToggleButton;
    private boolean is24HourFormat = true; // Initial format is 24-hour

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clockTextView = findViewById(R.id.clockTextView);
        formatToggleButton = findViewById(R.id.formatToggleButton);

        formatToggleButton.setOnClickListener(view -> toggleTimeFormat());

        // Create a handler to update the clock every 10 sec
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateClock();
                handler.postDelayed(this, 10000); // 1000ms = 1 second
            }
        });
    }

    private void updateClock() {
        // Get the current time
        long currentTime = System.currentTimeMillis();

        // Format the time based on the selected format
        SimpleDateFormat sdf;
        if (is24HourFormat) {
            sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        } else {
            sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        }

        String time = sdf.format(currentTime);

        // Update the TextView with the current time
        clockTextView.setText(time);
    }

    public void toggleTimeFormat() {
        is24HourFormat = !is24HourFormat;
        updateClock(); // Update the clock format
    }
}
