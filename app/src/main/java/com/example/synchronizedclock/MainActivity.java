package com.example.synchronizedclock;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private TextView clockTextView;       // TextView to display the clock time
    private Button formatToggleButton;     // Button to toggle 12-hour and 24-hour format
    private View internetStatusCircle;    // View to indicate internet status
    private Button pauseResumeButton;     // Button to pause/resume clock updates
    private boolean isClockPaused = false; // Flag to track if the clock is paused
    private boolean use12HourFormat = false; // Initial format is 24-hours
    private Handler handler;              // Handler for scheduling updates
    private ConnectivityManager connectivityManager; // To check internet connection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Center the pause button in the layout
        LinearLayout pauseButtonLayout = findViewById(R.id.pauseButtonLayout);
        pauseButtonLayout.setGravity(Gravity.CENTER);
        // Center the format button in the layout
        LinearLayout formatButtonLayout = findViewById(R.id.formatButtonLayout);
        formatButtonLayout.setGravity(Gravity.CENTER);
        // Center the clock text in the layout
        LinearLayout clockTextLayout = findViewById(R.id.clockTextLayout);
        clockTextLayout.setGravity(Gravity.CENTER);

        // Initialize UI elements
        clockTextView = findViewById(R.id.clockTextView);
        formatToggleButton = findViewById(R.id.formatToggleButton);
        pauseResumeButton = findViewById(R.id.pauseResumeButton);
        internetStatusCircle = findViewById(R.id.internetStatusCircle);

        // Create a new Handler associated with the main thread's Looper.
        // This allows you to post and execute Runnables on the main (UI) thread.
        handler = new Handler(Looper.getMainLooper());

        // Get the system's ConnectivityManager service, which is used to check
        // the status of the device's network connectivity.
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Toggle between 12-hour and 24-hour format when the button is clicked
        formatToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle between 12-hour and 24-hour format
                use12HourFormat = !use12HourFormat;
            }
        });

        // Pause or resume the clock when the button is clicked
        pauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClockPaused) {
                    // Resume the clock
                    isClockPaused = false;
                    pauseResumeButton.setText("Pause");
                    updateClockAndInternetStatus();
                } else {
                    // Pause the clock
                    isClockPaused = true;
                    pauseResumeButton.setText("Resume");
                    // Remove any existing callbacks to stop the clock updates
                    handler.removeCallbacksAndMessages(null);
                }
            }
        });
        // Start updating the time and internet status
        updateClockAndInternetStatus();
    }

    private void updateClockAndInternetStatus() {
        // Check if there is an internet connection
        boolean isConnected = isInternetConnected();

        // Changes the Background color of the Circle to white
        int circleColor = R.color.white;
        internetStatusCircle.setBackgroundResource(circleColor);

        // Update the color of the internetStatusCircle based on the internet status
        int circleDrawable = isConnected ? R.drawable.circle_green : R.drawable.circle_red;
        internetStatusCircle.setBackgroundResource(circleDrawable);

        if (isConnected) {
            // Fetch NTP time in the background
            new FetchNtpTimeTask().execute();
        } else {
            // Use the system time if no internet connection
            updateTimeWithSystemTime();
        }

        // Schedule the next update (every second)
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateClockAndInternetStatus();
            }
        }, 1000); // Update every 1000 ms (1 second)
    }

    // Check if there is an internet connection
    private boolean isInternetConnected() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    // Update the time display with the system time
    private void updateTimeWithSystemTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(use12HourFormat ? "hh:mm:ss a" : "HH:mm:ss", Locale.getDefault());
        String formattedTime = sdf.format(new Date());
        clockTextView.setText(formattedTime);
    }

    // Task to fetch NTP time in the background
    private class FetchNtpTimeTask extends AsyncTask<Void, Void, Date> {

        @Override
        protected Date doInBackground(Void... voids) {
            try {
                // Create an NTP client to fetch the time from an NTP server
                NTPUDPClient timeClient = new NTPUDPClient();

                // Set a timeout for the operation (2 seconds in this case)
                timeClient.setDefaultTimeout(2000);

                // Specify the NTP server to fetch the time from
                InetAddress inetAddress = InetAddress.getByName("1.se.pool.ntp.org");

                // Retrieve the time information from the NTP server
                TimeInfo timeInfo = timeClient.getTime(inetAddress);

                // Extract the NTP time in milliseconds from the time information
                long NTPTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();

                // Create a Date object from the NTP time
                return new Date(NTPTime);
            } catch (Exception e) {
                e.printStackTrace();
                // If an exception occurs (e.g., no internet connection or NTP server unreachable),
                // return the current system time as a fallback
                return new Date();
            }
        }

        @Override
        protected void onPostExecute(Date ntpTime) {
            // Format the NTP time with a specific timezone (e.g., GMT+2) and display it in 12-hour or 24-hour format
            SimpleDateFormat sdf = new SimpleDateFormat(use12HourFormat ? "hh:mm:ss a" : "HH:mm:ss", Locale.getDefault());

            // Set the desired timezone to GMT+2 for the formatted time
            TimeZone timeZone = TimeZone.getTimeZone("GMT+2");
            sdf.setTimeZone(timeZone);

            // Format the NTP time and update the clockTextView with the formatted time
            String formattedTime = sdf.format(ntpTime);
            clockTextView.setText(formattedTime);
        }
    }
}