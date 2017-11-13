package com.example.kemik.bonusball;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

public class CreateDraw extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    private EditText et_drawName;
    private String drawName;
    private EditText et_drawValue;
    private double drawValue;
    private EditText et_ticketValue;
    private double ticketValue;
    private TextView tv_startDate;
    private long startDate;
    private Button btn_createDraw;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_draw);

        // Find all views
        findViews();

        // Capture drawName and drawValue, and call to create a new draw.
        // When finished, go to MainActivity on create button click.
        btn_createDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawName = String.valueOf(et_drawName.getText());
                drawValue = Double.valueOf(String.valueOf(et_drawValue.getText()));
                ticketValue = Double.valueOf(String.valueOf(et_ticketValue.getText()));
                createDraw();
                startActivity(new Intent(CreateDraw.this, MainActivity.class));
                finish();
            }
        });

        // Show DatePicker dialog fragment
        tv_startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dateFragment = new DatePickerFragment();
                dateFragment.show(getFragmentManager(), "datePicker");
            }
        });
    }

    /**
     * Use the drawName, drawValue and startDate to create a new Draw
     */
    private void createDraw() {
        DBHelper db = new DBHelper(this);
        db.createNewDraw(drawName, drawValue, ticketValue, startDate);
    }

    /**
     * Receive a DatePicker and use the date selected to populate the tv_startDate TextView
     * @param datePicker
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        startDate = calendar.getTimeInMillis();
        String dateString = DateFormat.getDateInstance().format(startDate);
        tv_startDate.setText(dateString);
    }

    /**
     * Find all views by their ID's
     */
    private void findViews() {
        et_drawName = findViewById(R.id.drawName);
        et_drawValue = findViewById(R.id.drawValue);
        et_ticketValue = findViewById(R.id.ticketValue);
        tv_startDate = findViewById(R.id.startDate);
        btn_createDraw = findViewById(R.id.createDraw);
    }
}
