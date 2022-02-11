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

        executorService.execute(() -> {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int scrHeight = displayMetrics.heightPixels - getNavigationBarHeight();
            int scrWidth = displayMetrics.widthPixels;

            int size = 150;
            int speed = 35;
            boolean bounce = false;

            BallView[] balls = new BallView[16];

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);

            for (int i = 0; i < balls.length; i++) {
                Paint paint = new Paint();
                paint.setColor(Color.argb(255, (int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));
                paint.setStyle(Paint.Style.FILL);
                balls[i] = new BallView(this, size, speed, bounce, paint);
                container.addView(balls[i], params);
                int margin = 100;
                balls[i].setY((int)(Math.random() * (scrHeight - (size + margin))));
                balls[i].setX((int)(Math.random() * (scrWidth - (size + margin))));
            }

            try {
                int counter = 0;
                while (true) {
                    //counter++;
                    Thread.sleep(1000/60);
                    for (int i = 0; i < balls.length; i++) {

                        BallView tmpBall = balls[i].checkBorders(scrWidth, scrHeight);

                        // Si no se ha creado una bola temporal pero ya había una de antes...
                        if (tmpBall == null && balls[i].tmpBall != null) {
                            if (!balls[i].isOnScreen(scrWidth, scrHeight) && balls[i].tmpBall.isFullyOnScreenExceptBorder(scrWidth, scrHeight)) {
                                int index = i;

                                // Auxiliar variable to hold the ball that is going to be removed
                                BallView viewToDestroy = balls[index];
                                balls[i] = viewToDestroy.tmpBall;

                                // By using the auxiliar variable I can avoid synchronizing
                                // the current thread with the ui thread
                                runOnUiThread(() -> {
                                    container.removeView(viewToDestroy);
                                    System.out.println(container.getChildCount());
                                });
                            }
                        }

                        if (tmpBall != null)
                            runOnUiThread(() -> container.addView(tmpBall));

                        balls[i].move();
                    }

                    //if (counter % 500 == 0)
                        //Thread.sleep(3000);
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