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
import android.widget.Toast;

import com.example.kemik.bonusball.Entities.Draw;

import java.text.DateFormat;
import java.util.Calendar;

public class EditDraw extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    private EditText et_drawName;
    private String drawName;
    private EditText et_drawValue;
    private double drawValue;
    private EditText et_ticketValue;
    private double ticketValue;
    private TextView tv_startDate;
    private long startDate;
    private Button btn_editDraw;
    private long drawId;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_draw);

        // Find all views
        findViews();

        // Get drawId from intent to use for populating the draw details
        Intent intent = getIntent();
        if (intent.hasExtra("DrawId")) {
            drawId = intent.getLongExtra("DrawId", 0);
        }

        // Populate views with existing details
        populateDetails();

        // Show DatePicker dialog fragment
        showDatePicker();


        // Capture drawName and drawValue, and call to create a new draw.
        // When finished, go to MainActivity on create button click.
        btn_editDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidated()) {
                    captureDrawDetails();
                    updateDraw();

                    startActivity(new Intent(EditDraw.this, MainActivity.class));
                    finish();
                }
            }
        });
    }

    /**
     * Find all views by their ID's
     */
    private void findViews() {
        et_drawName = findViewById(R.id.drawName);
        et_drawValue = findViewById(R.id.drawValue);
        et_ticketValue = findViewById(R.id.ticketValue);
        tv_startDate = findViewById(R.id.startDate);
        btn_editDraw = findViewById(R.id.editDraw);
    }

    /**
     * Populate fields with the details of the current Draw
     */
    private void populateDetails() {
        DBHelper db = new DBHelper(this);
        Draw draw = db.getDrawById(drawId);

        et_drawName.setText(draw.getDrawName());
        et_drawValue.setText(String.format("%.2f", draw.getDrawValue()));
        et_ticketValue.setText(String.format("%.2f", draw.getTicketValue()));

        String dateString = DateFormat.getDateInstance().format(draw.getStartDate());
        tv_startDate.setText(dateString);
    }

    /**
     * Return a dialog fragment containing a datePicker for the Draw startDate
     */
    private void showDatePicker() {
        tv_startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dateFragment = new DatePickerFragment();
                dateFragment.show(getFragmentManager(), "datePicker");
            }
        });
    }

    /**
     * Receive a DatePicker and use the date selected to populate the tv_startDate TextView
     *
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
     * Validate inputs against null entries
     *
     * @return
     */
    private boolean isValidated() {
        if (et_drawName.getText().toString().trim().equals("")) {
            Toast.makeText(this, "You must enter a Draw Name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (et_drawValue.getText().toString().trim().equals("")) {
            Toast.makeText(this, "You must enter a Draw Value", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (et_ticketValue.getText().toString().trim().equals("")) {
            Toast.makeText(this, "You must enter a Ticket Value", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Capture and assign input details for drawName, drawValue and ticketValue
     */
    private void captureDrawDetails() {
        drawName = String.valueOf(et_drawName.getText());
        drawValue = Double.valueOf(String.valueOf(et_drawValue.getText()));
        ticketValue = Double.valueOf(String.valueOf(et_ticketValue.getText()));
    }

    /**
     * Use the drawName, drawValue and startDate to update this Draw by drawId
     */
    private void updateDraw() {
        DBHelper db = new DBHelper(this);
        db.updateDraw(drawName, drawValue, ticketValue, startDate, drawId);
    }
}
