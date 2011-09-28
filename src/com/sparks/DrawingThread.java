package com.sparks;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by IntelliJ IDEA.
 * User: dornatsky
 * Date: 9/27/11
 * Time: 5:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class DrawingThread extends Thread {
    private SurfaceHolder _holder;
    private Panel _panel;
    private boolean _isRunning = false;

    public DrawingThread (SurfaceHolder surfaceHolder, Panel panel)    {
        _holder = surfaceHolder;
        _panel = panel;
    }

    public void setRunning (boolean value){
        _isRunning = value;
    }

    @Override
    public void run(){
        Canvas c = null;
        while (_isRunning) {
            c = null;
            try {
                c =_holder.lockCanvas(null);
                synchronized (_holder){
                    _panel.onDraw(c);
                }
            }finally {
                    if (c!=null) {
                        _holder.unlockCanvasAndPost(c);
                    }
            }
        }
    }
}
