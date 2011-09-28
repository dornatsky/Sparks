package com.sparks;

import android.content.Context;
import android.graphics.*;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: dornatsky
 * Date: 9/27/11
 * Time: 4:02 PM
 */

public class Panel extends SurfaceView implements SurfaceHolder.Callback {

    private Object _locker = new Object();
    private DrawingThread _thread;
    private PointF _velocity;
    private PointF _position;

    float _x;
    float _y;
    long _lastCapture;

    private long _lastRedraw = new Date().getTime();
    private static final float CIRCLE_RADIUS = 20;


    public Panel(Context context){
        super (context);
        getHolder().addCallback(this);
        getHolder().addCallback(this);
        _thread = new DrawingThread(getHolder(), this);
        setFocusable(true);
    }

    @Override
    public void onDraw(Canvas canvas){
        canvas.drawColor(Color.WHITE);

        if (_position !=null && _velocity!=null) {
            synchronized (_locker){
                long delta = SystemClock.uptimeMillis() - _lastRedraw;
                PointF newPosition = new PointF(
                        _position.x + _velocity.x * delta,
                        _position.y + _velocity.y * delta);

                canvas.drawCircle(newPosition.x, newPosition.y, CIRCLE_RADIUS, new Paint());
                _position = newPosition;
                _lastRedraw = SystemClock.uptimeMillis();
            }
        }
    }

    @Override
    public boolean  onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            synchronized (_locker){
              _x = event.getX();
              _y = event.getY();
              _lastCapture = SystemClock.uptimeMillis();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
             synchronized (_locker){
                 PointF velocity = getVelocity(event);
                 if (velocity !=null) {
                    _position  = new PointF(event.getX(), event.getY());
                    _velocity = velocity;
                }
            }
        }

        return true;
    }

    private PointF getVelocity(MotionEvent event) {
        if (event == null)
            return null;

        long timeDiff = SystemClock.uptimeMillis() - _lastCapture;
        float deltaX = event.getX() - _x;
        float deltaY = event.getY() - _y;

        PointF result = new PointF(deltaX/timeDiff, deltaY/timeDiff);
        return result;
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        _thread.setRunning(true);
        _thread.start();
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        _thread.setRunning(false);
        while (retry) {
            try {
            _thread.join();
            retry = false;
            }catch (InterruptedException e){}
        }
    }
}