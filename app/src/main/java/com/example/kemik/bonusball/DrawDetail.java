package com.example.kemik.bonusball;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kemik.bonusball.Entities.Draw;
import com.example.kemik.bonusball.Entities.Entrant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DrawDetail extends AppCompatActivity implements ConfirmationDialog.ConfirmationDialogListener {

    private DBHelper db;
    private ConstraintLayout cl_drawDetailsContainer;
    private TextView tv_drawName;
    private TextView tv_startDate;
    private TextView tv_drawValue;
    private TextView tv_ticketValue;
    private TextView tv_profit;
    private ListView lv_numberSlotListView;
    private FloatingActionButton fab_options;
    private FloatingActionButton fab_randoms;
    private FloatingActionButton fab_numbers;
    private FloatingActionButton fab_names;
    private FloatingActionButton fab_edit;
    private FloatingActionButton fab_delete;
    private boolean isOpen = false;
    private long drawId;

    private EditText et_copyableTextWindow;
    private ImageView iv_copyableTextWindowCloseIcon;

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
        drawId = intent.getLongExtra("DrawId", 0);

        // Create the Draw from the ID
        Draw draw = db.getDrawById(drawId);

        // Populate TextViews with Draw details
        populateDrawDetailsTextViews(draw);

        // Set up and display the ListView
        setupAndDisplayListView(draw);

        // Toggle visibility on FAB click for Edit and Delete
        fab_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpen) {
                    openFabs();
                } else if (isOpen) {
                    closeFabs();
                }
            }
        });

        iv_copyableTextWindowCloseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeCopyableWindow();
            }
        });
    }

    /**
     * Find all views by their ID's
     */
    private void findViews() {
        cl_drawDetailsContainer = findViewById(R.id.drawDetailsContainer);
        tv_drawName = findViewById(R.id.drawName);
        tv_startDate = findViewById(R.id.startDate);
        tv_drawValue = findViewById(R.id.drawValue);
        tv_ticketValue = findViewById(R.id.ticketValue);
        tv_profit = findViewById(R.id.profit);
        lv_numberSlotListView = findViewById(R.id.numberSlotListView);
        fab_options = findViewById(R.id.drawDetailFab);
        fab_randoms = findViewById(R.id.drawDetailRandomsFab);
        fab_numbers = findViewById(R.id.drawDetailNumbersFab);
        fab_names = findViewById(R.id.drawDetailNamesFab);
        fab_edit = findViewById(R.id.drawDetailEditFab);
        fab_delete = findViewById(R.id.drawDetailDeleteFab);

        et_copyableTextWindow = findViewById(R.id.copyableTextWindow);
        iv_copyableTextWindowCloseIcon = findViewById(R.id.copyableTextWindowCloseButton);
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

    private void openFabs() {
        isOpen = true;
        fab_options.setImageResource(R.drawable.ic_close_black_24dp);
        fab_randoms.setVisibility(View.VISIBLE);
        fab_randoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                            generateRandomNumber();
            }
        });
        fab_numbers.setVisibility(View.VISIBLE);
        fab_numbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemainingNumbers();
            }
        });
        fab_names.setVisibility(View.VISIBLE);
        fab_names.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNamesAndNumbers();
            }
        });
        fab_edit.setVisibility(View.VISIBLE);
        fab_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDraw(drawId);
            }
        });
        fab_delete.setVisibility(View.VISIBLE);
        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDraw(drawId);
            }
        });
    }

    private void openCopyableWindow() {
        et_copyableTextWindow.setVisibility(View.VISIBLE);
        iv_copyableTextWindowCloseIcon.setVisibility(View.VISIBLE);
    }

    private void closeFabs() {
        isOpen = false;
        fab_options.setImageResource(R.drawable.ic_more_vert_black_24dp);
        fab_randoms.setVisibility(View.GONE);
        fab_numbers.setVisibility(View.GONE);
        fab_names.setVisibility(View.GONE);
        fab_delete.setVisibility(View.GONE);
        fab_edit.setVisibility(View.GONE);
    }

    private void closeCopyableWindow() {
        et_copyableTextWindow.setVisibility(View.GONE);
        iv_copyableTextWindowCloseIcon.setVisibility(View.GONE);
    }

    /**
     * Populate and make visible a hidden EditText field with the remaining numbers,
     * so that they can be copied and pasted elsewhere
     */
    private void showRemainingNumbers() {
        ArrayList<Integer> remainingNumbers = db.getRemainingNumbers(drawId);
        int size = remainingNumbers.size();

        closeFabs();
        openCopyableWindow();

        et_copyableTextWindow.setText("* ");
        for (int i = 0; i < size; i++) {
            et_copyableTextWindow.append(String.valueOf(remainingNumbers.get(i)));
            et_copyableTextWindow.append(" * ");
        }
    }

    /**
     * Populate and make visible a hidden EditText field with the list of names and numbers,
     * so that they can be copied and pasted elsewhere
     */
    private void showNamesAndNumbers() {
        ArrayList<Entrant> finalNamesAndNumbers = db.getEntrantsByDrawId(drawId);
        System.out.println("finalNamesAndNumbers : " + finalNamesAndNumbers.toString());
        int size = finalNamesAndNumbers.size();
        System.out.println("size: " + size);

        closeFabs();
        openCopyableWindow();

        et_copyableTextWindow.setText("");
        for (int i = 0; i < size; i++) {
            // If the lineNumber is a single digit, add a little spacing before for uniformity
            if (String.valueOf(finalNamesAndNumbers.get(i).getLineNumber()).length() == 1) {
                et_copyableTextWindow.append("  ");
            }
            et_copyableTextWindow.append(String.valueOf(finalNamesAndNumbers.get(i).getLineNumber()));
            et_copyableTextWindow.append(" - ");

            // Append name if one exists
            if (finalNamesAndNumbers.get(i).getEntrantName() != null) {
                et_copyableTextWindow.append(finalNamesAndNumbers.get(i).getEntrantName());
            }

            et_copyableTextWindow.append("\n");
        }
    }

    /**
     * On edit click, update the draw details using the drawId
     *
     * @param drawId
     */
    private void editDraw(long drawId) {
        Intent editIntent = new Intent(DrawDetail.this, EditDraw.class);
        editIntent.putExtra("DrawId", drawId);
        startActivity(editIntent);
    }

    /**
     * Show ConfirmationDialog box to delete a Draw
     *
     * @param drawId
     */
    private void deleteDraw(long drawId) {
        DialogFragment dialogFragment = new ConfirmationDialog();
        dialogFragment.show(getFragmentManager(), "ConfirmationDialog");
    }

    /**
     * On positive click, delete Draw by drawId, which also removes its associated Entrants
     *
     * @param dialogFragment
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {
        Toast.makeText(this, "yes", Toast.LENGTH_SHORT).show();
        db.deleteDraw(drawId);

        startActivity(new Intent(DrawDetail.this, MainActivity.class));
        finish();
    }

    /**
     * On negative click, do nothing
     *
     * @param dialogFragment
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialogFragment) {
        Toast.makeText(this, "no", Toast.LENGTH_SHORT).show();
        // Do nothing
    }
}
