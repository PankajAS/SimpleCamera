package com.wordpress.bytedebugger.simplecamera;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Plus 3 on 28-04-2017.
 */

public class ZoomViewTouchListner implements View.OnTouchListener {

    private GestureDetector mGestureDetector;
    ScaleGestureDetector mScaleGestureDetector;
    View mView, dragView;
    Context context;

    public ZoomViewTouchListner(Context context, View mView){
        this.mView = mView;
        this.context = context;
        mGestureDetector = new GestureDetector(mView.getContext(), mGestureListener);
        mScaleGestureDetector = new ScaleGestureDetector(mView.getContext(),new ZoomViewTouchListner.ScaleListener());
       // this.dragView = dragView;

    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mGestureDetector.onTouchEvent(motionEvent);

        return mScaleGestureDetector.onTouchEvent(motionEvent);
    }

    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        private float mMotionDownX, mMotionDownY;



        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public boolean onDown(MotionEvent e)
        {
            Toast.makeText(context,"onDown",Toast.LENGTH_SHORT).show();
            mMotionDownX = e.getRawX() - mView.getTranslationX();
            mMotionDownY = e.getRawY() - mView.getTranslationY();
            return true;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            Toast.makeText(context,"onScroll",Toast.LENGTH_SHORT).show();
          //  mView.setTranslationX(e2.getRawX() - mMotionDownX);
          //  mView.setTranslationY(e2.getRawY() - mMotionDownY);

            mView.setScaleY(e2.getRawY()/ mMotionDownY);
            mView.setScaleX(e2.getRawX()/ mMotionDownX);
            return true;
        }
    };

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
            Toast.makeText(context,"Scale Begin" ,Toast.LENGTH_SHORT).show();
            onScaleBegin = scale;

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

            onScaleEnd = scale;
            Toast.makeText(context,"Scale Ended",Toast.LENGTH_SHORT).show();

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
