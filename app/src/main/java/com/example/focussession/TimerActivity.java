package com.example.focussession;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimerActivity extends AppCompatActivity {

    private static final long START_TIME_IN_MILLIS_SESSION = 1500000; // 25 minutes
    private static final long START_TIME_IN_MILLIS_BREAK = 300000; // 5 minutes

    private TextView timerTextView;
    private LottieAnimationView timerAnimation;
    private Button breakButton;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private boolean isBreakTime = false;
    private int completedSessions = 0;
    private int completedBreaks = 0;
    private long sessionStartTime;
    private long breakStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Toolbar toolbar = findViewById(R.id.backButton);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                saveSessionData();
                Intent intent = new Intent(TimerActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        timerTextView = findViewById(R.id.timerTextView);
        timerAnimation = findViewById(R.id.timerAnimation);
        breakButton = findViewById(R.id.breakButton);

        startSession();

        breakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBreakTime) {
                    startSession();
                } else {
                    startBreak();
                }
            }
        });
    }

    private void saveSessionData() {
        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        int focusedMinutes = completedSessions * 25;
        int totalBreaks = completedBreaks * 5;
        SettingsActivity.TimerSession session = new SettingsActivity.TimerSession(dateTime, focusedMinutes, totalBreaks);
        SettingsActivity.timerSessions.add(session);
    }

    private void startSession() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isBreakTime = false;
        sessionStartTime = System.currentTimeMillis();
        breakButton.setText("Take a break");
        timeLeftInMillis = START_TIME_IN_MILLIS_SESSION;
        timerAnimation.setAnimation(R.raw.session_animation);
        timerAnimation.playAnimation();
        startTimer();
    }

    private void startBreak() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isBreakTime = true;
        breakStartTime = System.currentTimeMillis();
        completedSessions += (System.currentTimeMillis() - sessionStartTime) / (1000 * 60); // Calculate minutes spent in session
        breakButton.setText("Start another session");
        timeLeftInMillis = START_TIME_IN_MILLIS_BREAK;
        timerAnimation.setAnimation(R.raw.break_animation);
        timerAnimation.playAnimation();
        startTimer();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                if (isBreakTime) {
                    completedBreaks += (System.currentTimeMillis() - breakStartTime) / (1000 * 60); // Calculate minutes spent in break
                    startSession();
                } else {
                    startBreak();
                }
            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        timerTextView.setText(timeFormatted);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}