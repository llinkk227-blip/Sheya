package com.example.sheya5;

import android.app.Activity;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends Activity {

    private TextView timerText;
    private TextView countdownText;
    private TextView exerciseName;
    private Button startButton;
    private Button pauseButton;
    private Button stopButton;
    private LinearLayout controlRow;
    private PersonView personView;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    private int selectedMinutes = 5;
    private int remaining = 300;
    private int exerciseRemaining = 30;
    private int index = 0;

    private boolean running = false;
    private boolean paused = false;
    private boolean countdown = false;

    private int[] sequence;

    private final String[][] exercises = {
            {"Поворот вправо", "Медленно поверните голову вправо.", "right"},
            {"Поворот влево", "Медленно поверните голову влево.", "left"},
            {"Наклон вправо", "Мягко наклоните голову к правому плечу.", "tiltRight"},
            {"Наклон влево", "Мягко наклоните голову к левому плечу.", "tiltLeft"},
            {"Подбородок к груди", "Мягко опустите подбородок вниз.", "down"},
            {"Вытяжение макушкой", "Потянитесь макушкой вверх.", "up"},
            {"Плечи вверх — вниз", "Поднимите плечи и спокойно опустите.", "shoulders"},
            {"Плечи назад", "Медленно отведите плечи назад.", "back"},
            {"Сведение лопаток", "Мягко сведите лопатки.", "scapula"},
            {"Расслабление", "Опустите плечи и спокойно подышите.", "relax"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        timerText = findViewById(R.id.timerText);
        countdownText = findViewById(R.id.countdownText);
        exerciseName = findViewById(R.id.exerciseName);

        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);

        controlRow = findViewById(R.id.controlRow);
        personView = findViewById(R.id.personView);

        setupDuration(R.id.min4, 4);
        setupDuration(R.id.min5, 5);
        setupDuration(R.id.min6, 6);
        setupDuration(R.id.min8, 8);
        setupDuration(R.id.min10, 10);
        setupDuration(R.id.min15, 15);

        startButton.setOnClickListener(v -> startWorkout());

        pauseButton.setOnClickListener(v -> togglePause());

        stopButton.setOnClickListener(v -> stopWorkout());

        updateTimer();
    }

    private void setupDuration(int id, int minutes) {

        View button = findViewById(id);

        button.setOnClickListener(v -> {

            if (running) {
                return;
            }

            selectedMinutes = minutes;
            remaining = minutes * 60;

            updateTimer();

            exerciseName.setText("Готовы?");
            countdownText.setText("");
        });
    }

    private void startWorkout() {

        if (running) {
            return;
        }

        running = true;
        paused = false;

        remaining = selectedMinutes * 60;
        exerciseRemaining = 30;

        sequence = new int[remaining / 30];

        int previous = -1;

        for (int i = 0; i < sequence.length; i++) {

            int next;

            do {
                next = random.nextInt(exercises.length);
            } while (next == previous && exercises.length > 1);

            sequence[i] = next;
            previous = next;
        }

        index = 0;

        startButton.setVisibility(View.GONE);
        controlRow.setVisibility(View.VISIBLE);

        updateTimer();
        showExercise();

        startCountdown();
    }

    private void startCountdown() {

        countdown = true;

        countdownText.setText("3");
        signal();

        handler.postDelayed(() -> {

            if (running && !paused) {
                countdownText.setText("2");
                signal();
            }

        }, 1000);

        handler.postDelayed(() -> {

            if (running && !paused) {
                countdownText.setText("1");
                signal();
            }

        }, 2000);

        handler.postDelayed(() -> {

            if (running && !paused) {

                countdown = false;
                countdownText.setText("");

                tick();
            }

        }, 3000);
    }

    private void tick() {

        if (!running || paused || countdown) {
            return;
        }

        remaining--;
        exerciseRemaining--;

        updateTimer();

        if (remaining <= 0) {
            finishWorkout();
            return;
        }

        if (exerciseRemaining <= 0) {

            index++;

            if (index >= sequence.length) {
                index = sequence.length - 1;
            }

            exerciseRemaining = 30;

            showExercise();
            signal();
        }

        handler.postDelayed(this::tick, 1000);
    }

    private void showExercise() {

        if (sequence == null || sequence.length == 0) {
            return;
        }

        int exercise = sequence[index];

        exerciseName.setText(
                exercises[exercise][0]
                        + "\n"
                        + exercises[exercise][1]
        );

        personView.setPose(exercises[exercise][2]);
    }

    private void togglePause() {

        if (!running) {
            return;
        }

        if (!paused) {

            paused = true;

            pauseButton.setText("ПРОДОЛЖИТЬ");
            countdownText.setText("Пауза");

        } else {

            paused = false;

            pauseButton.setText("ПАУЗА");
            countdownText.setText("");

            handler.postDelayed(this::tick, 1000);
        }
    }

    private void stopWorkout() {

        running = false;
        paused = false;
        countdown = false;

        handler.removeCallbacksAndMessages(null);

        startButton.setVisibility(View.VISIBLE);
        controlRow.setVisibility(View.GONE);

        pauseButton.setText("ПАУЗА");

        remaining = selectedMinutes * 60;
        exerciseRemaining = 30;
        index = 0;

        exerciseName.setText("Готовы?");
        countdownText.setText("");

        personView.setPose("");

        updateTimer();
    }

    private
