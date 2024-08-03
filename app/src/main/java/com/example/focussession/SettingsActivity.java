package com.example.focussession;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    public static class TimerSession {
        private final String dateTime;
        private final int focusedMinutes;
        private final int totalBreaks;

        public TimerSession(String dateTime, int focusedMinutes, int totalBreaks) {
            this.dateTime = dateTime;
            this.focusedMinutes = focusedMinutes;
            this.totalBreaks = totalBreaks;
        }

        public String getDateTime() {
            return dateTime;
        }

        public int getFocusedMinutes() {
            return focusedMinutes;
        }

        public int getTotalBreaks() {
            return totalBreaks;
        }
    }

    public static List<TimerSession> timerSessions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Handle back button
        Toolbar toolbar = findViewById(R.id.backButton);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.btnSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // Display timer sessions
        LinearLayout sessionContainer = findViewById(R.id.sessionContainer);
        TextView totalFocusedMinutesView = findViewById(R.id.totalFocusedMinutes);
        TextView totalBreaksTakenView = findViewById(R.id.totalBreaksTaken);

        int totalFocusedMinutes = 0;
        int totalBreaks = 0;

        for (TimerSession session : timerSessions) {
            LinearLayout sessionRow = new LinearLayout(this);
            sessionRow.setOrientation(LinearLayout.HORIZONTAL);

            TextView dateTimeView = new TextView(this);
            dateTimeView.setText(session.getDateTime());
            dateTimeView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            TextView focusedMinutesView = new TextView(this);
            focusedMinutesView.setText(String.valueOf(session.getFocusedMinutes()));
            focusedMinutesView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            TextView totalBreaksView = new TextView(this);
            totalBreaksView.setText(String.valueOf(session.getTotalBreaks()));
            totalBreaksView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            sessionRow.addView(dateTimeView);
            sessionRow.addView(focusedMinutesView);
            sessionRow.addView(totalBreaksView);
            sessionContainer.addView(sessionRow);

            totalFocusedMinutes += session.getFocusedMinutes();
            totalBreaks += session.getTotalBreaks();
        }

        totalFocusedMinutesView.setText(String.format("Total Focused Minutes: %d", totalFocusedMinutes));
        totalBreaksTakenView.setText(String.format("Total Breaks Taken: %d", totalBreaks));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
