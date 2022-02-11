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
        this.angle = (float) ((Math.random() * 90) * Math.PI / 180);

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
        return this.getParent() != null && this.getX() > 0 - size && this.getX() < scrWidth && this.getY() > 0 - size && this.getY() < scrHeight;
    }

    public boolean isFullyOnScreen(float scrWidth, float scrHeight) {
        return this.getParent() != null && this.getX() > 0 && this.getX() < scrWidth - size && this.getY() > 0 && this.getY() < scrHeight - size;
    }

    public boolean wouldBeOnScreen(float x, float y, float scrWidth, float scrHeight) {
        return x > 0 && x < scrWidth - size && y > 0 && y < scrHeight - size;
    }

    public boolean isFullyOnScreenExceptBorder(float scrWidth, float scrHeight) {
        if (this.getParent() == null)
            return false;

        float limitX = scrWidth - size;
        float limitY = scrHeight - size;
        boolean touchingStartX = !towardsX && getX() < 0;
        boolean touchingEndX = towardsX && getX() > limitX;
        boolean touchingStartY = !towardsY && getY() < 0;
        boolean touchingEndY = towardsY && getY() > limitY;

        if (towardsX && towardsY)
            return !(touchingEndX && touchingEndY);
        else if (!towardsX && !towardsY)
            return !(touchingStartX && touchingStartY);
        else if (!towardsX && towardsY)
            return !(touchingStartX && touchingEndY);
        else // if (towardsX && !towardsY) // Last possible case
            return !(touchingEndX && touchingStartY);
    }

    public BallView checkBorders(float scrWidth, float scrHeight) {
        float limitX = scrWidth - size;
        float limitY = scrHeight - size;
        boolean touchingStartX = !towardsX && getX() < 1;
        boolean touchingEndX = towardsX && getX() > limitX;
        boolean touchingStartY = !towardsY && getY() < 1;
        boolean touchingEndY = towardsY && getY() > limitY;

        // System.out.println(this);

        if (bounce) {
            if (touchingStartX)
                towardsX = true;
            else if (touchingEndX)
                towardsX = false;

            if (touchingStartY)
                towardsY = true;
            else if (touchingEndY)
                towardsY = false;

        } else if (tmpBall == null && (touchingStartX || touchingEndX || touchingStartY || touchingEndY)) {
            float finalX =getX();
            float finalY = getY();

            tmpBall = new BallView(this.getContext(), size, speed, paint, angle, bounce, towardsX, towardsY);

            if (touchingStartX)
                finalX = getX() + scrWidth;
            else if (touchingEndX)
                finalX = getX() - scrWidth;

            if (touchingStartY)
                finalY = getY() + scrHeight;
            else if (touchingEndY)
                finalY = getY() - scrHeight;

            tmpBall.setX(finalX);
            tmpBall.setY(finalY);

            return tmpBall;
        }

        return null;
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

    @Override
    public String toString() {
        return "BallView{" +
                "size=" + size +
                ", speed=" + speed +
                ", angle=" + angle +
                ", bounce=" + bounce +
                ", x=" + getX() +
                ", y=" + getY() +
                ", towardsX=" + towardsX +
                ", towardsY=" + towardsY +
                ", tmpBall=" + tmpBall +
                '}';
    }
}
