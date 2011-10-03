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

    private DrawingThread _thread;
    private PointF _throwStartPosition;
    private long _throwStartTime;
    private Spark _spark;

    private static final float CIRCLE_RADIUS = 20;

    public Panel(Context context){
        super (context);
        getHolder().addCallback(this);
        _thread = new DrawingThread(getHolder(), this);
        setFocusable(true);
    }

    @Override
    public void onDraw(Canvas canvas){
        canvas.drawColor(Color.WHITE);

        if (_spark!=null) {
                Spark spark =_spark;
                spark.move();
                canvas.drawCircle(spark.getPosition().x, spark.getPosition().y, CIRCLE_RADIUS, new Paint());
        }
    }

    @Override
    public boolean  onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
              _throwStartPosition = new PointF(
                      event.getX(),
                      event.getY());
              _throwStartTime = SystemClock.uptimeMillis();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            _spark = new Spark(
               _throwStartPosition,
               new PointF(event.getX(), event.getY()),
               event.getEventTime() - _throwStartTime);
        }

        return true;
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