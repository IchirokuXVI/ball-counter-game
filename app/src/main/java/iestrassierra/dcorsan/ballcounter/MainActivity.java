package iestrassierra.dcorsan.ballcounter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
    ConstraintLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);

        int size = 150;

        while (true) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            params.topMargin = 0 - size / 2;
            params.leftMargin = 0 - size / 2;

            View viewShape = new View(this) {
                @Override
                protected void onDraw(Canvas canvas) {
                    // canvas.drawColor(Color.CYAN);
                    Paint paint = new Paint();
                    paint.setColor(Color.argb(255, (int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));

                    paint.setStyle(Paint.Style.FILL);

                    canvas.drawCircle(size / 2, size / 2, size / 2, paint);
                }
            };

            container.addView(viewShape, params);
        }
    }
}