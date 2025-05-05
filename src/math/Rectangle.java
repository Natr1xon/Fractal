package math;

import java.awt.*;

public class Rectangle {
    private Point pointStart = null;
    private Point pointEnd = null;

    public void addPoint(Point point){
        if(pointStart == null) pointStart = point;
        else pointEnd = point;
    }

    public void clear(){
        pointStart = null;
        pointEnd = null;
    }

    public boolean isInvalid(){
        return pointStart == null || pointEnd == null;
    }

    public int getX() throws IllegalStateException{
        if(isInvalid()) throw new IllegalStateException("Rectangle is not properly defined");
        return Math.min(pointStart.x,pointEnd.x);
    }

    public int getY() throws IllegalStateException{
        if(isInvalid()) throw new IllegalStateException("Rectangle is not properly defined");
        return Math.min(pointStart.y,pointEnd.y);
    }

    public int getWidth() throws IllegalStateException{
        if(isInvalid()) throw new IllegalStateException("Rectangle is not properly defined");
        return Math.max(pointStart.x,pointEnd.x) - Math.min(pointStart.x,pointEnd.x);
    }

    public int getHeight() throws IllegalStateException{
        if(isInvalid()) throw new IllegalStateException("Rectangle is not properly defined");
        return Math.max(pointStart.y,pointEnd.y) - Math.min(pointStart.y,pointEnd.y);
    }
}
