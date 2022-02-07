package iestrassierra.dcorsan.ballcounter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class BallView extends View {

    private int size;
    private Paint paint;
    private int angle;
    private boolean towardsX;
    private boolean towardsY;


    public BallView(Context context, int size, Paint paint) {
        super(context);
        this.size = size;
        this.paint = paint;
        this.angle = ((int)(Math.random() * 360)) + 1;

        towardsX = Math.random() < 0.5;
        towardsY = Math.random() < 0.5;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
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
