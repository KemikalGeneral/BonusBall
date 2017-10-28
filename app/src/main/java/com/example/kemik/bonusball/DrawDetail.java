package com.example.kemik.bonusball;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kemik.bonusball.Entities.Draw;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DrawDetail extends AppCompatActivity {

    private TextView tv_drawName;
    private TextView tv_startDate;
    private TextView tv_drawValue;
    private TextView tv_ticketValue;
    private LinearLayout ll_numbersContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_detail);

        // Find all views
        findViews();

        // Instantiate a new DBHelper class
        DBHelper db = new DBHelper(this);

        // Get ID from intent extras
        Intent intent = getIntent();
        long drawId = intent.getLongExtra("DrawId", 0);

        // Create the Draw from the ID
        Draw draw = db.getDrawById(drawId);

        // Populate TextViews with Draw details
        populateTextViews(draw);

        // Create 1 - 59 numbered TextViews
        createNumbersTextViews();
    }

    /**
     * Create 1 - 59 numbered TextViews which will hold the names and payment status of the entrants
     */
    private void createNumbersTextViews() {
        int numbers = 59;
        for (int i = 1; i < numbers + 1; i++) {
            TextView numberSlot = new TextView(this);
            numberSlot.setText(String.valueOf(i));
            ll_numbersContainer.addView(numberSlot);
        }
    }

    /**
     * Populate the TextViews for showing the details of the current Draw from the DB
     *
     * @param draw
     */
    private void populateTextViews(Draw draw) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
        String dateString = simpleDateFormat.format(date);

        tv_drawName.setText(draw.getDrawName());
        tv_startDate.setText(dateString);
        tv_drawValue.setText("£");
        tv_drawValue.append(String.format("%.2f", draw.getDrawValue()));
        tv_ticketValue.setText("£");
        tv_ticketValue.append(String.format("%.2f", draw.getTicketValue()));
    }

    /**
     * Find all views by their ID's
     */
    private void findViews() {
        tv_drawName = findViewById(R.id.drawName);
        tv_startDate = findViewById(R.id.startDate);
        tv_drawValue = findViewById(R.id.drawValue);
        tv_ticketValue = findViewById(R.id.ticketValue);
        ll_numbersContainer = findViewById(R.id.numbersContainer);
    }
}
