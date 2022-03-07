package iestrassierra.dcorsan.ballcounter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        // Get the constraint layout to add the new views
        ConstraintLayout containerInputs = findViewById(R.id.final_container_inputs);
        // Create a constraint set that will store the current constraints later on
        ConstraintSet set = new ConstraintSet();

        // Hashmap for saving EditTexts for later checking of the introduced values
        HashMap<String, EditText> editTexts = new HashMap();

        // Get map from the BallsActivity to generate the views and also get the answers
        Map<String, Integer> answers = (Map<String, Integer>) getIntent().getSerializableExtra("colors_answer");

        int lastId = 0;
        for (Map.Entry<String, Integer> answer : answers.entrySet()) {
            TextView textView = new TextView(this);
            textView.setId(View.generateViewId());

            EditText editText = new EditText(this);
            editText.setId(View.generateViewId());
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);

            // Set text of the TextView to the name of the color with the first character in uppercase
            textView.setText(Character.toUpperCase(answer.getKey().charAt(0)) + answer.getKey().substring(1));

            containerInputs.addView(textView);
            containerInputs.addView(editText);

            // Get the current constraints on the constraint layout
            set.clone(containerInputs);


            // Set constraints to views
            //             PARENT        PARENT
            //                               ^
            //       (TOP TO EditText)       |
            // PARENT <--- TextView <--- EditText PARENT
            //                               ^
            //       (TOP TO EditText)       |
            // PARENT <--- TextView <--- EditText PARENT
            // ...

            // Textview constraints
            //     start has a constraint to parent start
            //     top has a constraint to top editText
            //     bottom has a constraint to bottom editText
            //         By having top and bottom constraints the TextView will be vertically centered to the EditText
            set.connect(textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            // set.connect(textView.getId(), ConstraintSet.END, editText.getId(), ConstraintSet.START);
            set.connect(textView.getId(), ConstraintSet.TOP, editText.getId(), ConstraintSet.TOP);
            set.connect(textView.getId(), ConstraintSet.BOTTOM, editText.getId(), ConstraintSet.BOTTOM);

            // EditText constraints
            //     top has a constraint to top parent (first editText only)
            //         For the next editTexts the constraint will be to the bottom of last editText
            //     start has a constraint to end textView and 32 margin so they are not touching
            set.connect(editText.getId(), ConstraintSet.TOP, editTexts.isEmpty() ? ConstraintSet.PARENT_ID : lastId, editTexts.isEmpty() ? ConstraintSet.TOP : ConstraintSet.BOTTOM);
            set.connect(editText.getId(), ConstraintSet.START, textView.getId(), ConstraintSet.END, 32);
            // set.connect(editText.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

            // Tried to make a chain but I couldn't make it work
            // set.createHorizontalChain(textView.getId(), ConstraintSet.RIGHT, editText.getId(), ConstraintSet.LEFT, new int[] {textView.getId(), editText.getId()}, null, ConstraintSet.CHAIN_SPREAD);

            // Apply the new constraints
            set.applyTo(containerInputs);

            // Add editText to map so it can be checked later and also to know which was the last created EditText
            editTexts.put(answer.getKey(), editText);
            // Keep key of the last EditText to make constraint for the next ones
            lastId = editText.getId();
        }

        // Check answers when clicking button
        findViewById(R.id.checkAnswers).setOnClickListener((v) -> {
            boolean missingData = false;
            int rightGuesses = 0;
            for (Map.Entry<String, Integer> validAnswer : answers.entrySet()) {
                // If there is missing data break the loop
                // Otherwise if the value is correct increase the counter
                if (editTexts.get(validAnswer.getKey()).getText().toString().isEmpty()) {
                    missingData = true;
                    break;
                } else if (Integer.parseInt(editTexts.get(validAnswer.getKey()).getText().toString()) == validAnswer.getValue())
                    rightGuesses++;
            }

            if (missingData)
                Toast.makeText(this, "Missing data", Toast.LENGTH_SHORT).show();
            else {
                if (rightGuesses == answers.size()) {
                    MediaPlayer.create(this, R.raw.you_win).start();
                    Toast.makeText(this, "Congratulations !", Toast.LENGTH_SHORT).show();
                } else {
                    MediaPlayer.create(this, R.raw.you_lose).start();
                    Toast.makeText(this, "You only answered " + rightGuesses + " correctly. Try again !", Toast.LENGTH_SHORT).show();
                }

                // Back to main activity
                startActivity(new Intent(this, MainActivity.class));
            }
        });

    }

    // Do nothing when pressing back
    @Override
    public void onBackPressed() { }
}