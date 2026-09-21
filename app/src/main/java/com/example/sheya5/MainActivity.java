
package com.example.sheya5;

import android.app.Activity;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.*;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends Activity {

    private TextView timerText, countdownText, exerciseName;
    private Button startButton, pauseButton, stopButton;
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
            {"Поворот вправо","Медленно поверните голову вправо.","right"},
            {"Поворот влево","Медленно поверните голову влево.","left"},
            {"Наклон вправо","Мягко наклоните голову к правому плечу.","tiltRight"},
            {"Наклон влево","Мягко наклоните голову к левому плечу.","tiltLeft"},
            {"Подбородок к груди","Опустите подбородок к груди.","down"},
            {"Вытяжение","Потянитесь макушкой вверх.","up"},
            {"Плечи вверх","Поднимите плечи и опустите.","shoulders"},
            {"Плечи назад","Отведите плечи назад.","back"},
            {"Лопатки","Сведите лопатки.","scapula"},
            {"Расслабление","Спокойно подышите.","relax"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        timerText = findViewById(R.id.timerText);
        countdownText = findViewById(R.id.countdownText);
        exerciseName = findViewById(R.id.exerciseName);

        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);

        controlRow = findViewById(R.id.controlRow);
        personView = findViewById(R.id.personView);

        setupDuration(R.id.min4,4);
        setupDuration(R.id.min5,5);
        setupDuration(R.id.min6,6);
        setupDuration(R.id.min8,8);
        setupDuration(R.id.min10,10);
        setupDuration(R.id.min15,15);

        startButton.setOnClickListener(v -> startWorkout());
        pauseButton.setOnClickListener(v -> togglePause());
        stopButton.setOnClickListener(v -> stopWorkout());

        updateTimer();
    }

    private void setupDuration(int id,int minutes){
        findViewById(id).setOnClickListener(v->{
            if(running) return;
            selectedMinutes=minutes;
            remaining=minutes*60;
            updateTimer();
            exerciseName.setText("Готовы?");
            countdownText.setText("");
        });
    }

    private void startWorkout(){
        if(running) return;

        running=true;
        paused=false;
        countdown=false;

        remaining=selectedMinutes*60;
        exerciseRemaining=30;

        sequence=new int[remaining/30];
        int prev=-1;

        for(int i=0;i<sequence.length;i++){
            int n;
            do{
                n=random.nextInt(exercises.length);
            }while(n==prev);
            sequence[i]=n;
            prev=n;
        }

        index=0;

        startButton.setVisibility(View.GONE);
        controlRow.setVisibility(View.VISIBLE);

        showExercise();
        updateTimer();

        startCountdown();
    }

    private void startCountdown(){
        countdown=true;
        countdownText.setText("3");
        signal();

        handler.postDelayed(()->{
            if(running&&!paused){
                countdownText.setText("2");
                signal();
            }
        },1000);

        handler.postDelayed(()->{
            if(running&&!paused){
                countdownText.setText("1");
                signal();
            }
        },2000);

        handler.postDelayed(()->{
            if(running&&!paused){
                countdown=false;
                countdownText.setText("");
                tick();
            }
        },3000);
    }

    private void tick(){
        if(!running||paused||countdown) return;

        remaining--;
        exerciseRemaining--;

        updateTimer();

        if(remaining<=0){
            finishWorkout();
            return;
        }

        if(exerciseRemaining<=0){
            index++;
            if(index>=sequence.length){
                index=sequence.length-1;
            }
            exerciseRemaining=30;
            showExercise();
            signal();
        }

        handler.postDelayed(this::tick,1000);
    }

    private void showExercise(){
        int e=sequence[index];
        exerciseName.setText(
                exercises[e][0]+"\n"+exercises[e][1]
        );
        personView.setPose(exercises[e][2]);
    }

    private void togglePause(){
        if(!running) return;

        if(!paused){
            paused=true;
            pauseButton.setText("ПРОДОЛЖИТЬ");
            countdownText.setText("Пауза");
        }else{
            paused=false;
            pauseButton.setText("ПАУЗА");
            countdownText.setText("");
            handler.postDelayed(this::tick,1000);
        }
    }

    private void stopWorkout(){
        running=false;
        paused=false;
        countdown=false;

        handler.removeCallbacksAndMessages(null);

        startButton.setVisibility(View.VISIBLE);
        controlRow.setVisibility(View.GONE);

        pauseButton.setText("ПАУЗА");

        remaining=selectedMinutes*60;
        exerciseRemaining=30;
        index=0;

        exerciseName.setText("Готовы?");
        countdownText.setText("");
        personView.setPose("");

        updateTimer();
    }

    private void finishWorkout(){
        running=false;
        paused=false;
        countdown=false;

        handler.removeCallbacksAndMessages(null);

        timerText.setText("00:00");
        exerciseName.setText("Зарядка завершена");
        countdownText.setText("Отличная работа!");

        startButton.setVisibility(View.VISIBLE);
        controlRow.setVisibility(View.GONE);

        signal();
    }

    private void updateTimer(){
        int m=remaining/60;
        int s=remaining%60;

        timerText.setText(
                String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        m,s
                )
        );
    }

    private void signal(){
        try{
            ToneGenerator tg=new ToneGenerator(
                    AudioManager.STREAM_NOTIFICATION,80);
            tg.startTone(
                    ToneGenerator.TONE_PROP_BEEP,120);
            handler.postDelayed(tg::release,180);
        }catch(Exception ignored){}

        try{
            Vibrator v=(Vibrator)getSystemService(VIBRATOR_SERVICE);
            if(v!=null){
                if(Build.VERSION.SDK_INT>=26){
                    v.vibrate(
                        VibrationEffect.createOneShot(
                            70,
                            VibrationEffect.DEFAULT_AMPLITUDE));
                }else{
                    v.vibrate(70);
                }
            }
        }catch(Exception ignored){}
    }
}
