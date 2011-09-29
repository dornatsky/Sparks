package com.sparks;

import android.graphics.PointF;
import android.os.SystemClock;

/**
 * Created by IntelliJ IDEA.
 * User: dornatsky
 * Date: 9/29/11
 * Time: 1:55 PM
 */
public class Spark {

    private PointF _velocity;
    private PointF _position;

    public Spark(PointF start, PointF release, long timeDiff){
        _position = release;
        float deltaX = _position.x - start.x;
        float deltaY = _position.y - start.y;
        _velocity= new PointF(deltaX/timeDiff, deltaY/timeDiff);
    }

    public PointF getVelocity(){
        return _velocity;
    }

    public PointF getPosition(){
        return _position;
    }

    public void move(long interval){
        PointF newPosition = new PointF(
            _position.x + _velocity.x * interval,
            _position.y + _velocity.y * interval);

        _position = newPosition;
    }


}
