package iestrassierra.dcorsan.ballcounter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class BallView extends View {

    private int size;
    private Paint paint;

    public BallView(Context context, int size, Paint paint) {
        super(context);
        this.size = size;
        this.paint = paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);
    }
}
