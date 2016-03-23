package com.rocket.vasili.sillyrocket;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements RocketAnimation.RocketListener
{

    private static final String TAG = "SillyV.MainActivity";

    private static final int LOADING_GAME = 0;
    private static final int WAITING_FOR_BUTTON_CLICK = 1;
    private static final int RUNNING_TIMER = 2;
    private static final int DISPLAYING_RESULTS = 3;
    private int gameState = 0;


    View sky;
    View target;
    View rocket;
    TextView timer;
    TextView scoreTV;

    int score = 0;
    boolean readyToShoot = false;
    private float startAim;
    long down;
    int currentLocationOfTarget;
    float currentAim;

    Random randomgenerator = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rocket = findViewById(R.id.rocket);
        sky = findViewById(R.id.sky);
        target = findViewById(R.id.target);
        scoreTV = (TextView) findViewById(R.id.score_text_view);
        timer = (TextView) findViewById(R.id.timer_text_view);
        rocket.setRotation(-45);
        gameState = WAITING_FOR_BUTTON_CLICK;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                down = System.currentTimeMillis();
                startAim = event.getY();
                return true;
            case MotionEvent.ACTION_UP:
                if (readyToShoot)
                {
                    readyToShoot = false;
                    currentAim = startAim - event.getY();
                    startAnimation(currentAim, System.currentTimeMillis() - down);
                }
                return true;
        }
        return false;
    }

    private void startAnimation(float v, long duration)
    {
//        mparams = (RelativeLayout.LayoutParams) rocket.getLayoutParams();
//        originalX = mparams.getMarginEnd();
        float aim = v / sky.getHeight();
        RocketAnimation anim = new RocketAnimation(rocket, sky.getHeight() - rocket.getHeight() - 32, sky.getWidth() - rocket.getWidth() - 32, aim, this, duration);
        rocket.startAnimation(anim);
    }

    @Override
    public void onRocketLanded()
    {
        rocket.clearAnimation();
        readyToShoot = true;
        rocket.setVisibility(View.VISIBLE);
        rocket.invalidate();
        sky.invalidate();


        checkForTargetHit();


    }

    private void checkForTargetHit()
    {
        int myaim = (int) (sky.getHeight() - currentAim);
        Log.d(TAG, (myaim) + " - " + currentLocationOfTarget);
        if (myaim  < currentLocationOfTarget + 150 && myaim > currentLocationOfTarget - 150)
        {
            Log.d(TAG, (myaim) + " - " + currentLocationOfTarget + "Entered Target");

            score+=1;
            scoreTV.setText("Targets hit: " + score);
                    setTargetLocation();
        }
    }

    public void onStartClick(final View view)
    {
        gameState = RUNNING_TIMER;
        score = 0;
        readyToShoot = true;
        view.setVisibility(View.GONE);
        sky.setBackgroundResource(R.drawable.backgroundgame);
        scoreTV.setVisibility(View.VISIBLE);
        timer.setVisibility(View.VISIBLE);

        CountDownTimer Count = new CountDownTimer(60000, 1000)
        {
            public void onTick(long millisUntilFinished)
            {
                timer.setText("Seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish()
            {
                timer.setText("Finished");
                gameState = WAITING_FOR_BUTTON_CLICK;
                score = 0;
                readyToShoot = false;
                sky.setBackgroundResource(R.drawable.background);
                scoreTV.setVisibility(View.VISIBLE);
                timer.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);


            }
        };

        Count.start();
        setTargetLocation();
    }

    public void setTargetLocation()
    {
        int xxx = sky.getHeight() - target.getHeight() - 300;
        currentLocationOfTarget = randomgenerator.nextInt(xxx) + 16;
        findViewById(R.id.target).setY(currentLocationOfTarget);
    }


}
