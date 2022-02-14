package iestrassierra.dcorsan.ballcounter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    public static boolean STOP_BALLS = false;

    FrameLayout container;
    ExecutorService executorService  = Executors.newFixedThreadPool(4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int scrHeight = displayMetrics.heightPixels - getNavigationBarHeight();
        int scrWidth = displayMetrics.widthPixels;

        int size = 150;
        int speed = 20;
        boolean bounce = false;

        BallView[] balls = new BallView[6];

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);

        container.setOnClickListener((v) -> {
            STOP_BALLS = !STOP_BALLS;
            synchronized (balls) {
                balls.notify();
            }
        });

        container.setOnLongClickListener((v) -> {
            STOP_BALLS = true;
            synchronized (balls) {
                for (BallView ball : balls)
                    ball.setBounce(!ball.isBounce());
                STOP_BALLS = false;

                Toast.makeText(this, "Rebote/atravesar intercambiado", Toast.LENGTH_LONG);
                balls.notify();
            }
            return true;
        });

        executorService.execute(() -> {
            for (int i = 0; i < balls.length; i++) {
                Paint paint = new Paint();
                paint.setColor(Color.argb(255, (int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));
                paint.setStyle(Paint.Style.FILL);
                balls[i] = new BallView(this, size, speed, bounce, paint);
                int index = i;
                runOnUiThread(() -> {
                    container.addView(balls[index], params);
                    container.addView(balls[index].tmpBall, params);
                });
                int margin = 100;
                balls[i].setY((int)(Math.random() * (scrHeight - (size + margin))));
                balls[i].setX((int)(Math.random() * (scrWidth - (size + margin))));
            }

            try {
                synchronized (balls) {
                    while (true) {
                        while (STOP_BALLS)
                            balls.wait();
                        Thread.sleep(1000/60);
                        for (int i = 0; i < balls.length; i++) {
                            balls[i].move(scrWidth, scrHeight);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private int getNavigationBarHeight() {
        Resources resources = this.getResources();

        int id = resources.getIdentifier(
                    resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
                        "navigation_bar_height" : "navigation_bar_height_landscape",
                "dimen", "android");

        if (id > 0) {
            return resources.getDimensionPixelSize(id);
        }
        return 0;
    }
}