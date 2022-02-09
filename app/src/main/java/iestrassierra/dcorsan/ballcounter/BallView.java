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
    private int angle;
    private boolean bounce;
    private boolean towardsX;
    private boolean towardsY;


    public BallView(Context context, int size, int speed, boolean bounce, Paint paint) {
        super(context);
        this.size = size;
        this.speed = speed;
        this.paint = paint;
        this.angle = (int)(Math.random() * 90);
        this.bounce = bounce;

        towardsX = Math.random() < 0.5;
        towardsY = Math.random() < 0.5;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);
    }

    public boolean isOnScreen(float scrWidth, float scrHeight) {
        return this.getX() > 0 && this.getX() < scrWidth - size && this.getY() > 0 && this.getY() < scrHeight - size;
    }

    public boolean wouldBeOnScreen(float x, float y, float scrWidth, float scrHeight) {
        return x > 0 && x < scrWidth - size && y > 0 && y < scrHeight - size;
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

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
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
