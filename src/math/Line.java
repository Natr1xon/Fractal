package math;

import java.awt.*;

public class Line {
    private Point pointStart = null;
    private Point pointEnd = null;

    public Point getPointEnd() {
        return pointEnd;
    }

    public void setPointEnd(Point pointEnd) {
        this.pointEnd = pointEnd;
    }

    public Point getPointStart() {
        return pointStart;
    }

    public void setPointStart(Point pointStart) {
        this.pointStart = pointStart;
    }

    public void addPoint(Point point){
        if(pointStart == null) pointStart = point;
        else pointEnd = point;
    }

    public void clear(){
        pointStart = null;
        pointEnd = null;
    }

    public int distance(){
        return (int) Math.sqrt(Math.pow(pointStart.x - pointEnd.x,2) - Math.pow(pointStart.y - pointEnd.y,2));
    }

    public boolean isInvalid(){
        return pointStart == null || pointEnd == null;
    }
}
