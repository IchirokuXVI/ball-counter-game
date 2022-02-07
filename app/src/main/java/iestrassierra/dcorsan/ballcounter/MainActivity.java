package iestrassierra.dcorsan.ballcounter;

import androidx.appcompat.app.ActionBar;
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
import android.view.WindowManager;
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

        View decorView = getWindow().getDecorView();
        // Hide the navigation bar, status bar and action bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        container = findViewById(R.id.container);

        executorService.execute(((Runnable) () -> {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int scrHeight = displayMetrics.heightPixels;
            int scrWidth = displayMetrics.widthPixels;

            int size = 150;
            int speed = 10;

            BallView[] balls = new BallView[30];
            Paint paint = new Paint();
            paint.setColor(Color.argb(255, (int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));

            paint.setStyle(Paint.Style.FILL);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);

            for (int i = 0; i < balls.length; i++) {
                balls[i] = new BallView(this, size, paint);
                container.addView(balls[i], params);
                int margin = 100;
                balls[i].setY((int)(Math.random() * (scrHeight - (size + margin))));
                balls[i].setX((int)(Math.random() * (scrWidth - (size + margin))));
            }

            try {
                Thread.sleep(3000);
                while (true) {
                    Thread.sleep(1000 / 60);
                    for (BallView ball : balls) {
                        moveBall(ball, speed, scrWidth - size, scrHeight - size, true);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

    }

    private void moveBall(BallView ball, int speed, float limitX, float limitY, boolean bounce) {
        double finalX = ball.getX();
        double finalY = ball.getY();

        // Convert to radians
        double angle = ball.getAngle() * Math.PI / 180;

        if (ball.getX() > limitX) {
            if (!bounce)
                finalX = 0;
            else
                ball.setTowardsX(false);
        } else if (ball.getX() < 0) {
            if (!bounce)
                finalX = limitX;
            else
                ball.setTowardsX(true);
        }

        if (ball.getY() > limitY) {
            if (!bounce)
                finalY = 0;
            else
                ball.setTowardsY(false);
        } else if (ball.getY() < 0) {
            if (!bounce)
                finalY = limitY;
            else
                ball.setTowardsY(true);
        }

        finalX += Math.cos(angle) * (ball.isTowardsX() ? speed : -speed);
        finalY += Math.sin(angle) * (ball.isTowardsY() ? speed : -speed);

        ball.setX((float)finalX);
        ball.setY((float)finalY);
    }
}