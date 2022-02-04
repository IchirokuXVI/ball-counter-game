package iestrassierra.dcorsan.ballcounter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    FrameLayout container;
    ExecutorService executorService  = Executors.newFixedThreadPool(4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);

        executorService.execute(((Runnable) () -> {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int scrHeight = displayMetrics.heightPixels;
            int scrWidth = displayMetrics.widthPixels;

            int size = 150;
            int speed = 5;

            BallView[] balls = new BallView[6];
            Paint paint = new Paint();
            paint.setColor(Color.argb(255, (int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));

            paint.setStyle(Paint.Style.FILL);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);

            for (int i = 0; i < balls.length; i++) {
                balls[i] = new BallView(this, size, paint);
                container.addView(balls[i], params);
                int defaultPos = 300;
                balls[i].setY(defaultPos * (int)((i + 1) * defaultPos / scrHeight));
                balls[i].setX((i + 1) * defaultPos);
            }
            try {
                Thread.sleep(5000);
                while (true) {

                        Thread.sleep(16);
                        for (BallView ball : balls) {
                            ball.setX((ball.getX() < scrWidth ? ball.getX() : 0) + speed);
                            ball.setY((ball.getY() < scrHeight ? ball.getY() : 0) + speed);
                        }

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }));

    }
}