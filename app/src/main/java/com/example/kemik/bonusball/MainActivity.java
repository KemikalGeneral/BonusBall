package com.example.kemik.bonusball;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DBHelper db;
    private ConstraintLayout cl_mainActivityContainer;
    private ListView lv_draws;
    private EditText et_remainingNumbers;
    private ImageView iv_remainingNumbersClose;
    private FloatingActionButton fab;

    private Boolean exit = false;

    // On back press show a confirmation Toast and wait for a response within three seconds to exit
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHelper(this);
        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(
                db.getDatabaseName(), MODE_PRIVATE, null, null
        );
//        db.dropTables(sqLiteDatabase); // For testing purposes
        db.onCreate(sqLiteDatabase);

        // Find all views
        findViews();

        // Got to CreateDraw activity on click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateDraw.class));
            }
        });

        // Setup and display draw names as a ListView
        DrawArrayAdapter drawArrayAdapter = new DrawArrayAdapter(this);
        drawArrayAdapter.addAll(db.getDraws());
        lv_draws.setAdapter(drawArrayAdapter);

        // On longClick of draw list item,
        // show window with copyable remaining numbers for the respective draw
        Intent intent = getIntent();
        if (intent.hasExtra("remainingNumbers")) {
            ArrayList<Integer> remainingNumbers = intent.getIntegerArrayListExtra("remainingNumbers");
            System.out.println("remainingNumbers: " + remainingNumbers.size());

            et_remainingNumbers.setVisibility(View.VISIBLE);
            iv_remainingNumbersClose.setVisibility(View.VISIBLE);
            et_remainingNumbers.setText("Available...\n\n* ");
            for (int i = 0; i < remainingNumbers.size(); i++) {
                et_remainingNumbers.append(String.valueOf(remainingNumbers.get(i)));
                et_remainingNumbers.append(" * ");
            }
        }

        // On close button click, hide the remaining numbers window
        iv_remainingNumbersClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_remainingNumbers.setVisibility(View.GONE);
                iv_remainingNumbersClose.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Find all views bu their ID's
     */
    private void findViews() {
        cl_mainActivityContainer = findViewById(R.id.mainActivityContainer);
        lv_draws = findViewById(R.id.drawsListView);
        et_remainingNumbers = findViewById(R.id.remainingNumbers);
        iv_remainingNumbersClose = findViewById(R.id.remainingNumbersCloseButton);
        fab = findViewById(R.id.fab);
    }
}
