package com.example.synchronizedclock;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public class MainActivity extends AppCompatActivity {

    private TextView clockTextView;
    private Button formatToggleButton;
    private boolean use12HourFormat = false; // Initial format is 24-hours
    private Handler handler;
    private ConnectivityManager connectivityManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clockTextView = findViewById(R.id.clockTextView);
        formatToggleButton = findViewById(R.id.formatToggleButton);
        formatToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle between 12-hour and 24-hour format
                use12HourFormat = !use12HourFormat;
            }
        });
        handler = new Handler(Looper.getMainLooper());
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Start updating the time
        updateClock();
    }
    private void updateClock() {
        // Check if there is an internet connection
        boolean isConnected = isInternetConnected();

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
                updateClock();
            }
        }, 1000); // Update every 1000 ms (1 second)
    }
    private boolean isInternetConnected() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
    private void updateTimeWithSystemTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(use12HourFormat ? "hh:mm:ss a" : "HH:mm:ss", Locale.getDefault());
        String formattedTime = sdf.format(new Date());
        clockTextView.setText(formattedTime);
    }
    private class FetchNtpTimeTask extends AsyncTask<Void, Void, Date> {

        @Override
        protected Date doInBackground(Void... voids) {
            try {
                NTPUDPClient timeClient = new NTPUDPClient();
                timeClient.setDefaultTimeout(2000);
                InetAddress inetAddress = InetAddress.getByName("1.se.pool.ntp.org");
                TimeInfo timeInfo = timeClient.getTime(inetAddress);
                long NTPTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                return new Date(NTPTime);
            } catch (Exception e) {
                e.printStackTrace();
                return new Date(); // If an exception occurs, return system time
            }
        }

        @Override
        protected void onPostExecute(Date ntpTime) {
            // Format the time with a specific timezone (e.g., GMT+2) and display 12-hour or 24-hour format
            SimpleDateFormat sdf = new SimpleDateFormat(use12HourFormat ? "hh:mm:ss a" : "HH:mm:ss", Locale.getDefault());
            TimeZone timeZone = TimeZone.getTimeZone("GMT+2");
            sdf.setTimeZone(timeZone);
            String formattedTime = sdf.format(ntpTime);

            clockTextView.setText(formattedTime);
        }
    }
}