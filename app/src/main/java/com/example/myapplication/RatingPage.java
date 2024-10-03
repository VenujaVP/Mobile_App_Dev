package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RatingPage extends AppCompatActivity {

    private RatingBar ratingBar;
    private TextView ratingScale;
    private Button button;  // Added button declaration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rating_page);

        ratingBar = findViewById(R.id.ratingBar2);
        ratingScale = findViewById(R.id.textView4);
        button = findViewById(R.id.button2);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingScale.setText(String.valueOf(rating));

                switch ((int) ratingBar.getRating()) {
                    case 1:
                        ratingScale.setText("Very Bad");
                        break;
                    case 2:
                        ratingScale.setText("Bad");
                        break;
                    case 3:
                        ratingScale.setText("Good");
                        break;
                    case 4:
                        ratingScale.setText("Great");
                        break;
                    case 5:
                        ratingScale.setText("Awesome");
                        break;
                    default:
                        ratingScale.setText("");
                        break;
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {  // Corrected View.OnClickListener implementation
            @Override
            public void onClick(View view) {
                String message = String.valueOf(ratingBar.getRating());
                Toast.makeText(RatingPage.this, "Rating is: " + message, Toast.LENGTH_SHORT).show();  // Corrected class name for Toast
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
