package iestrassierra.dcorsan.ballcounter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BallsActivity extends AppCompatActivity {
    public static boolean STOP_BALLS;

    FrameLayout container;
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    long gameTimer;
    long frameTime;
    List<BallView> balls;
    HashMap<String, Paint> colors;
    HashMap<String, Integer> ballsPerColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balls);

        // Get default preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        frameTime  = 1000/60;
        gameTimer = 0;
        balls = new ArrayList();
        STOP_BALLS = true;

        colors = new HashMap<>();
        ballsPerColor = new HashMap<>();

        container = findViewById(R.id.container);

        TextView textViewReady = findViewById(R.id.balls_ready);
        TextView timer = findViewById(R.id.balls_timer);

        // Get screen size to have a more precise bounce
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int scrHeight = displayMetrics.heightPixels - getNavigationBarHeight();
        int scrWidth = displayMetrics.widthPixels;

        // init variables that depend on preferences
        int size = 150;
        int numberColors = Integer.parseInt(prefs.getString("num_colors", "4"));
        int maxBallsColor = Integer.parseInt(prefs.getString("max_balls", "4"));
        int speed = Integer.parseInt(prefs.getString("speed", "8"));
        boolean bounce = prefs.getBoolean("bounce", true);
        int maxTime = Integer.parseInt(prefs.getString("time", "20"));

        textViewReady.setText(R.string.balls_ready);

        // Get animations from XML files
        Animation anim_appear = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.appear_inc_size);

        Animation anim_disappear = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.disappear);

        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.pi_pi_gooo);
        // Set listener to make the animation disappear after it has already appeared
        anim_appear.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                if (!mp.isPlaying())
                    mp.start();
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                textViewReady.startAnimation(anim_disappear);
            }
        });

        // Set listener to show the next text. When the disappear animation ends an appear animation
        // starts until all the 3 texts (READY, SET, GO) have been shown
        anim_disappear.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                if (textViewReady.getText().equals(getApplicationContext().getResources().getString(R.string.balls_ready)))
                    textViewReady.setText(R.string.balls_set);
                else if (textViewReady.getText().equals(getApplicationContext().getResources().getString(R.string.balls_set)))
                    textViewReady.setText(R.string.balls_go);
                else {
                    // Hide view because its animations have ended
                    textViewReady.setVisibility(View.INVISIBLE);

                    // Start balls movement
                    STOP_BALLS = false;
                    synchronized (balls) {
                        balls.notifyAll();
                    }
                    return; // Stop animation
                }

                // Start appear animation again (with the new text)
                textViewReady.startAnimation(anim_appear);
            }
        });

        // Start appear animation. The next animations are handled through events
        textViewReady.startAnimation(anim_appear);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);

        executorService.execute(() -> {
            // Get colors for the balls from the resource files
            String[] availableColors = this.getResources().getStringArray(R.array.available_ball_colors);

            for (int i = 0; i < numberColors; i++) {
                int color;
                String colorName;
                // Select a random color. Do it until getting one that wasn't selected before
                // The most efficient way would be removing the selected options from array so that
                // options couldn't appear again
                do {
                    int index = (int) (Math.random() * availableColors.length);
                    colorName = availableColors[index];
                    color = getResources().getColor(getResources().getIdentifier(colorName, "color", getPackageName()));
                } while (colors.containsKey(colorName));

                // Create a paint with the randomly selected color
                Paint paint = new Paint();
                paint.setColor(color);
                paint.setStyle(Paint.Style.FILL);

                colors.put(colorName, paint);
                ballsPerColor.put(colorName, (int)(Math.random() * maxBallsColor));

                for (int j = 0; j < ballsPerColor.get(colorName); j++) {
                    BallView ball = new BallView(this, size, speed, bounce, paint);

                    // The UI Thread is the only one able to add views to the container
                    runOnUiThread(() -> {
                        container.addView(ball, params);
                        if (!bounce)
                            container.addView(ball.tmpBall, params);
                    });

                    int margin = 100;

                    // Set random positions to start
                    ball.setY((int)(Math.random() * (scrHeight - (size + margin))));
                    ball.setX((int)(Math.random() * (scrWidth - (size + margin))));

                    // Add new ball to the list of balls to display it later on screen
                    balls.add(ball);
                }
            }

            // Debugging to know the amount of balls per color
            for (Map.Entry<String, Integer> n : ballsPerColor.entrySet())
                System.out.println(n.getKey() + ": " + n.getValue());

            try {
                synchronized (balls) {
                    while (true) {
                        while (STOP_BALLS)
                            balls.wait();

                        // This needs API level 24 =(
                        // balls.forEach((ball) -> ball.move(scrWidth, scrHeight));

                        // Cannot use this approach because it gives a lot of errors
                        // So I handled the timer on another thread (which is way worse)
                        // gameTimer += frameTime;

                        // runOnUiThread(() ->
                        //         timer.setText(String.valueOf((int) (gameTimer / 1000))));

                        for (BallView ball : balls)
                            ball.move(scrWidth, scrHeight);

                        Thread.sleep(frameTime);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        executorService.execute(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);

                    // This is not nearly as good as the other way that I tried before
                    // Increase gameTimer with frameTime on each frame in the same thread
                    // as the balls. That way it will stop when the balls are stopped
                    // And also it will be resumed instantly when the balls move
                    // But that approach gave me a lot of errors, for some reason in a few
                    // seconds the memory was full

                    if (!STOP_BALLS) {
                        gameTimer += 1;

                        // Display seconds passed since started
                        runOnUiThread(() ->
                                timer.setText(String.valueOf((int) (gameTimer))));

                        // If the timer reaches the max time then go to the final activity
                        if ((int) gameTimer == maxTime) {
                            Intent intent = new Intent(this, FinalActivity.class);
                            intent.putExtra("colors_answer", ballsPerColor);
                            startActivity(intent);
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Stop balls on click for debugging. Easier to count balls this way
        container.setOnClickListener((v) -> {
            STOP_BALLS = !STOP_BALLS;
            synchronized (balls) {
                balls.notifyAll();
            }
        });

        /* Toggle bounce with long click for debugging purposes
           Bounce must be off by default before using this
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
        */
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

    // Do nothing when pressing back
    @Override
    public void onBackPressed() { }
}