package com.example.synchronizedclock;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.widget.TextView;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private TextView clockTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clockTextView = findViewById(R.id.clockTextView);

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

        // Convert the time to a readable format (e.g., 12:00 AM)
        String time = android.text.format.DateFormat.getTimeFormat(this).format(currentTime);

        // Update the TextView with the current time
        clockTextView.setText(time);
    }
}