package com.sillyv.vasili.boomtank;

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
    ///constants
    private static final int LOADING_GAME = 0;
    private static final int WAITING_FOR_BUTTON_CLICK = 1;
    private static final int RUNNING_TIMER = 2;
    private static final int DISPLAYING_RESULTS = 3;
    private int gameState = 0;


    //views
    View sky;
    View target;
    View rocket;
    TextView timer;
    TextView scoreTV;


//useful vairables
    int score = 0;
    boolean readyToShoot = false;
    long down;
    int currentLocationOfTarget;
    float horizontalSpeed;
    float verticalSpeed;


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
                verticalSpeed = event.getY();
                horizontalSpeed = event.getX();
                Log.d(TAG, "STARTING POSITION X" +horizontalSpeed + "");
                return true;
            case MotionEvent.ACTION_UP:
                if (readyToShoot)
                {


                    readyToShoot = false;
                    verticalSpeed  -= event.getY();
                    horizontalSpeed = event.getX() - horizontalSpeed;

                    double distance = Math.sqrt(verticalSpeed * verticalSpeed + horizontalSpeed + horizontalSpeed);

                    Log.d(TAG, "ENDING POSITION X" +horizontalSpeed + "");
                    startAnimation(verticalSpeed, horizontalSpeed, System.currentTimeMillis() - down);
                }
                return true;
        }
        return false;
    }

    private void startAnimation(float v, float h, long duration)
    {
//        mparams = (RelativeLayout.LayoutParams) rocket.getLayoutParams();
//        originalX = mparams.getMarginEnd();
        float aimV = v / sky.getHeight();
        float aimH = h / sky.getWidth();
        RocketAnimation anim = new RocketAnimation(rocket, sky.getHeight() - rocket.getHeight() - 32, sky.getWidth() - rocket.getWidth() - 32, aimV,aimH, this, duration);
        rocket.startAnimation(anim);
    }

    @Override
    public void onRocketLanded(float h)
    {
        rocket.clearAnimation();
        readyToShoot = true;
        rocket.setVisibility(View.VISIBLE);
        rocket.invalidate();
        sky.invalidate();


        checkForTargetHit(h);


    }

    private void checkForTargetHit( float h)
    {
        int myaim = (int) (sky.getHeight() - h);
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
