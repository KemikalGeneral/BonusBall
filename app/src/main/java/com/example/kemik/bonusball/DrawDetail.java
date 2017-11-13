package com.example.kemik.bonusball;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kemik.bonusball.Entities.Draw;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DrawDetail extends AppCompatActivity {

    private DBHelper db;
    private TextView tv_drawName;
    private TextView tv_startDate;
    private TextView tv_drawValue;
    private TextView tv_ticketValue;
    private TextView tv_profit;
    private ListView lv_numberSlotListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_detail);

        // Find all views
        findViews();

        // Instantiate a new DBHelper class
        db = new DBHelper(this);

        // Get ID from intent extras
        Intent intent = getIntent();
        long drawId = intent.getLongExtra("DrawId", 0);

        // Create the Draw from the ID
        Draw draw = db.getDrawById(drawId);

        // Populate TextViews with Draw details
        populateDrawDetailsTextViews(draw);

        // Set up and display the ListView
        setupAndDisplayListView(draw);
    }

    /**
     * Find all views by their ID's
     */
    private void findViews() {
        tv_drawName = findViewById(R.id.drawName);
        tv_startDate = findViewById(R.id.startDate);
        tv_drawValue = findViewById(R.id.drawValue);
        tv_ticketValue = findViewById(R.id.ticketValue);
        tv_profit = findViewById(R.id.profit);
        lv_numberSlotListView = findViewById(R.id.numberSlotListView);
    }

    /**
     * Populate the TextViews for showing the details of the current Draw from the DB
     *
     * @param draw
     */
    private void populateDrawDetailsTextViews(Draw draw) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
        String dateString = simpleDateFormat.format(date);
        double profit = (draw.getTicketValue() * 59) - draw.getDrawValue();

        tv_drawName.setText(draw.getDrawName());
        tv_startDate.setText(dateString);
        tv_drawValue.setText("£");
        tv_drawValue.append(String.format("%.2f", draw.getDrawValue()));
        tv_ticketValue.setText("£");
        tv_ticketValue.append(String.format("%.2f", draw.getTicketValue()));
        tv_profit.setText("£");
        tv_profit.append(String.format("%.2f", profit));
    }

    /**
     * SetUp and display the ListView
     * ...Create a new ArrayAdapter
     * ...AddAll Draws from DB
     * ...Set adapter
     */
    private void setupAndDisplayListView(Draw draw) {
        NumberSlotArrayAdapter numberSlotArrayAdapter = new NumberSlotArrayAdapter(this, draw.getDrawId());
        numberSlotArrayAdapter.addAll(db.getEntrantsByDrawId(draw.getDrawId()));
        lv_numberSlotListView.setAdapter(numberSlotArrayAdapter);
    }
}
