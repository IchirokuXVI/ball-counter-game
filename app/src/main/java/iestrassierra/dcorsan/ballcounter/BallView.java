package iestrassierra.dcorsan.ballcounter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;

public class BallView extends View {

    private int size;
    private int speed;
    private Paint paint;
    private float angle;
    private boolean bounce;
    private boolean towardsX;
    private boolean towardsY;

    public BallView tmpBall = null;

    public BallView(Context context, int size, int speed, boolean bounce, Paint paint) {
        super(context);
        this.size = size;
        this.speed = speed;
        this.paint = paint;

        // Generate an angle between 0 and 90, then convert it to radians
        this.angle = (int) ((Math.random() * 90) * Math.PI / 180);

        this.bounce = bounce;

        // Generate two booleans to know at which direction is moving
        towardsX = Math.random() < 0.5;
        towardsY = Math.random() < 0.5;
    }

    public BallView(Context context, int size, int speed, Paint paint, float angle, boolean bounce, boolean towardsX, boolean towardsY) {
        super(context);
        this.size = size;
        this.speed = speed;
        this.paint = paint;
        this.angle = angle;
        this.bounce = bounce;
        this.towardsX = towardsX;
        this.towardsY = towardsY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);
    }

    public boolean isOnScreen(float scrWidth, float scrHeight) {
        // Little margin to make sure the ball isn't on the screen
        int margin = speed + 10;
        return this.getX() > 0 - margin && this.getX() < scrWidth + margin && this.getY() > 0 - margin && this.getY() < scrHeight + margin;
    }

    public boolean isFullyOnScreen(float scrWidth, float scrHeight) {
        // Little margin to make sure the ball isn't on the screen
        return this.getX() > 0 && this.getX() < scrWidth - size && this.getY() > 0 && this.getY() < scrHeight - size;
    }

    public boolean wouldBeOnScreen(float x, float y, float scrWidth, float scrHeight) {
        return x > 0 && x < scrWidth - size && y > 0 && y < scrHeight - size;
    }

    public BallView checkBorders(float scrWidth, float scrHeight) {
        float limitX = scrWidth - size;
        float limitY = scrHeight - size;
        boolean touchingStartX = getX() < 1;
        boolean touchingEndX = getX() > limitX;
        boolean touchingStartY = getY() < 1;
        boolean touchingEndY = getY() > limitY;

//        System.out.println("x start: " + touchingStartX);
//        System.out.println("x end: " + touchingEndX);
//        System.out.println("y start: " + touchingStartY);
//        System.out.println("y end: " + touchingEndY);

        if (bounce) {
            if (touchingStartX)
                towardsX = true;
            else if (touchingEndX)
                towardsX = false;

            if (touchingStartY)
                towardsY = true;
            else if (touchingEndY)
                towardsY = false;

            // towardsX = touchingStartX || !touchingEndX;
            // towardsY = touchingStartY || !touchingEndY;
        } else if (tmpBall == null) {
            if (touchingStartX || touchingEndX) {
                tmpBall = new BallView(this.getContext(), size, speed, paint, angle, bounce, towardsX, towardsY);
                tmpBall.setX(touchingStartX ? scrWidth : 0);
                tmpBall.setY(getY());
            }

            if (touchingStartY || touchingEndY) {
                tmpBall = new BallView(this.getContext(), size, speed, paint, angle, bounce, towardsX, towardsY);
                tmpBall.setY(touchingStartY ? scrHeight : 0);
                tmpBall.setX(getX());
            }
            return tmpBall;
        }

        return null;
        // return touchingStartX || touchingEndX || touchingStartY || touchingEndY;
    }

    public void move() {
        this.setX((float) (getX() + Math.cos(angle) * (towardsX ? speed : -speed)));
        this.setY((float) (getY() + Math.sin(angle) * (towardsY ? speed : -speed)));

        if (!bounce && tmpBall != null) {
            tmpBall.move();
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public boolean isBounce() {
        return bounce;
    }

    public void setBounce(boolean bounce) {
        this.bounce = bounce;
    }

    public boolean isTowardsX() {
        return towardsX;
    }

    public void setTowardsX(boolean towardsX) {
        this.towardsX = towardsX;
    }

    public boolean isTowardsY() {
        return towardsY;
    }

    public void setTowardsY(boolean towardsY) {
        this.towardsY = towardsY;
    }
}
