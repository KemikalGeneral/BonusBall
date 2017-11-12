package com.example.kemik.bonusball;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DBHelper db;
    private FloatingActionButton fab;
    private ListView lv_draws;

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

        // Set ActionBar title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Big Prize Bonus Ball");

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
    }

    /**
     * Find all views bu their ID's
     */
    private void findViews() {
        fab = findViewById(R.id.fab);
        lv_draws = findViewById(R.id.drawsListView);
    }
}
