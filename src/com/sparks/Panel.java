package com.sparks;

import android.content.Context;
import android.graphics.*;
import android.os.SystemClock;
import android.view.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: dornatsky
 * Date: 9/27/11
 * Time: 4:02 PM
 */

public class Panel extends SurfaceView implements SurfaceHolder.Callback {
    private final Object _locker = new Object();

    private DrawingThread _thread;
    private PointF _throwStartPosition;
    private long _throwStartTime;
    private List<Spark> _sparks = new ArrayList<Spark>();

    private int _width;
    private int _height;

    private static final float CIRCLE_RADIUS = 20;

    public Panel(Context context){
        super(context);
        getHolder().addCallback(this);
        _thread = new DrawingThread(getHolder(), this);
        setFocusable(true);
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        _width = display.getWidth();
        _height = display.getHeight();
    }

    @Override
    public void onDraw(Canvas canvas){
        canvas.drawColor(Color.WHITE);

        List<Spark> sparks = new ArrayList<Spark>(_sparks);
        List<Spark> sparksToRemove = new ArrayList<Spark>();

        for (Spark spark: sparks){
                spark.move();

                if (spark.getPosition().x > _width || spark.getPosition().y > _height) {
                    sparksToRemove.add(spark);
                }
                canvas.drawCircle(spark.getPosition().x, spark.getPosition().y, CIRCLE_RADIUS, new Paint());
        }

        if (sparksToRemove.size()>0){
            synchronized (_locker){
                _sparks.removeAll(sparksToRemove);
            }
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
            Spark spark = new Spark(
               _throwStartPosition,
               new PointF(event.getX(), event.getY()),
               event.getEventTime() - _throwStartTime);
            synchronized (_locker){
            _sparks.add(spark);
            }
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