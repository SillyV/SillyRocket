package com.rocket.vasili.sillyrocket;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class RocketAnimation extends Animation implements Animation.AnimationListener
{
    private static final String TAG = "SillyV.RocketAnimation";
    private final View view;

    private static final long DEFAULT_ANIMATION_DURATION = 2000;
    private final int targetY;
    private final float aim;
    private final int targetX;
    private final float originalY;
    private final float originalX;
    private static final float INCREASE_VECTOR = 2f;
    private final int originalWidth;
    private final RocketListener listener;

    public interface RocketListener
    {
        void onRocketLanded();
    }


    public RocketAnimation(View view, int targetY, int targetX, float aim, RocketListener listener, long duration)
    {
        this.setAnimationListener(this);
        this.view = view;
        this.listener = listener;
        this.aim = aim;
        this.targetX = targetX;
        this.targetY = targetY;
        this.originalY = view.getY();
        this.originalX = view.getX();
        this.originalWidth = view.getWidth();
        if (duration < 1000)
        {
            duration = 1000;
        }
        else if (duration > 3000)
        {
            duration = 3000;
        }
        setDuration(duration);
    }


    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t)
    {
        super.applyTransformation(interpolatedTime, t);

        // move vertically
        int newX = (int) (originalX + (targetX - originalX) * interpolatedTime);
        view.setX(newX);

        int newY = (int) (calculateY(interpolatedTime) * targetY);
        // Log.d(TAG,targetY + " - targetY ,   "+ newY+" - newX");
        view.setY(newY);

        view.setRotation(155 * interpolatedTime - 45);
        //increase my size

//        ViewGroup.LayoutParams params = view.getLayoutParams();
//        params.width = originalWidth + (int) (((originalWidth * INCREASE_VECTOR) - originalWidth)  * interpolatedTime);
//
//
//        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) view.getLayoutParams();

        //view.setLayoutParams(params);
    }

    private float calculateY(float interpolatedTime)
    {
        //float y = ((float)Math.pow ((double)(4 * (interpolatedTime - .5)),2d));
        float y = -(aimCalculator(aim, -4, -1) * interpolatedTime * interpolatedTime + aimCalculator(aim, 4, 2) * interpolatedTime - 1);
        Log.d(TAG, interpolatedTime + " turns into " + y);
        return y;
    }

    private float aimCalculator(float aim, int from, int to)
    {
        return ((to - from) * aim + from);
    }

    @Override
    public void onAnimationStart(Animation animation)
    {

    }

    @Override
    public void onAnimationEnd(Animation animation)
    {
        view.setVisibility(View.GONE);
        view.setX(originalX);
        view.setY(originalY);
        view.setRotation(-45);
        view.invalidate();
        listener.onRocketLanded();

    }

    @Override
    public void onAnimationRepeat(Animation animation)
    {

    }
}
