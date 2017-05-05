package com.wordpress.bytedebugger.simplecamera;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Plus 3 on 22-04-2017.
 */

public class MoveViewTouchListener implements View.OnTouchListener, RotationGestureDetector.OnRotationGestureListener
{
    private GestureDetector mGestureDetector;
    ScaleGestureDetector mScaleGestureDetector;
    private RotationGestureDetector mRotationDetector;
    private View mView, dragView;
    Context context;
    private int Position_X;
    private int Position_Y;
    long startTime = 0 ;

    public MoveViewTouchListener(Context context, View view)
    {
        mGestureDetector = new GestureDetector(view.getContext(), mGestureListener);
        mScaleGestureDetector = new ScaleGestureDetector(view.getContext(),new ScaleListener());
        mRotationDetector = new RotationGestureDetector(this);
        mView = view;
        this.dragView = dragView;
        this.context = context;

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
    //    v.setRotation(v.getRotation() + 90);
        Log.e("rotation", String.valueOf(v.getRotation()));

        mView.invalidate();

        mRotationDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);
         mGestureDetector.onTouchEvent(event);
        return false;
    }

    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        private float mMotionDownX, mMotionDownY;



        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public boolean onDown(MotionEvent e)
        {
            mMotionDownX = e.getRawX() - mView.getTranslationX();
            mMotionDownY = e.getRawY() - mView.getTranslationY();
            return true;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            mView.setTranslationX(e2.getRawX() - mMotionDownX);
            mView.setTranslationY(e2.getRawY() - mMotionDownY);

            //mView.setScaleY(e2.getRawY() - mMotionDownY);
            return true;
        }
    };

    @Override
    public void OnRotation(RotationGestureDetector rotationDetector) {
        float angle = rotationDetector.getAngle();
        //mView.setOrientation((mView.getOrientation() + 90) % 360);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        private float scale = 1f;
        float onScaleBegin = 0;
        float onScaleEnd = 0;

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            mView.setScaleX(scale);
            mView.setScaleY(scale);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {

            onScaleBegin = scale;

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

            onScaleEnd = scale;

            if (onScaleEnd > onScaleBegin){
                Toast.makeText(context,"Scaled Up by a factor of  " + String.valueOf( onScaleEnd / onScaleBegin ), Toast.LENGTH_SHORT  ).show();
            }

            if (onScaleEnd < onScaleBegin){
                Toast.makeText(context,"Scaled Down by a factor of  " + String.valueOf( onScaleBegin / onScaleEnd ), Toast.LENGTH_SHORT  ).show();
            }

            super.onScaleEnd(detector);
        }
    }


}
