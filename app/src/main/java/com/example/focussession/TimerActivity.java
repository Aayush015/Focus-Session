package com.example.focussession;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class TimerActivity extends AppCompatActivity {
    private static final long SESSION_TIME = 25 * 60 * 1000; // 25 minutes
    private static final long BREAK_TIME = 5 * 60 * 1000; // 5 minutes

    private LottieAnimationView lottieAnimationView;
    private TextView timerTextView;
    private Button breakButton;
    private CountDownTimer countDownTimer;
    private boolean isSessionActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        timerTextView = findViewById(R.id.timerTextView);
        breakButton = findViewById(R.id.breakButton);

        startSessionTimer();

        breakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBreakTimer();
            }
        });
    }

    private void startSessionTimer() {
        countDownTimer = new CountDownTimer(SESSION_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimer(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                timerTextView.setText("00:00");
                isSessionActive = false;
                breakButton.setVisibility(View.VISIBLE);
                lottieAnimationView.setAnimation(R.raw.break_animation);
                lottieAnimationView.playAnimation();
            }
        }.start();
    }

    private void startBreakTimer() {
        countDownTimer.cancel();
        countDownTimer = new CountDownTimer(BREAK_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimer(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                timerTextView.setText("00:00");
                isSessionActive = true;
                breakButton.setVisibility(View.GONE);
                lottieAnimationView.setAnimation(R.raw.session_animation);
                lottieAnimationView.playAnimation();
                startSessionTimer();
            }
        }.start();
    }

    private void updateTimer(long millisUntilFinished) {
        int minutes = (int) (millisUntilFinished / 1000) / 60;
        int seconds = (int) (millisUntilFinished / 1000) % 60;
        timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
    }

}
