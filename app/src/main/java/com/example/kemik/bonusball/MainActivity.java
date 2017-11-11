package com.example.kemik.bonusball;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kemik.bonusball.Entities.Draw;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DBHelper db;
    private FloatingActionButton fab;
    private LinearLayout ll_drawsContainer;

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

        // Display draw names by creating a TextView for each record returned from the DB
        displayDraws();
    }

    /**
     * Display draw names by
     * creating a TextView for each record returned from the DB
     * adding the new TextView to the ll_drawsContainer
     */
    private void displayDraws() {
        ArrayList<Draw> draws = db.getDraws();

        for (final Draw draw : draws) {
            TextView tv_draw = new TextView(this);
            tv_draw.setText(draw.getDrawName());
            ll_drawsContainer.addView(tv_draw);

            tv_draw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, DrawDetail.class);
                    intent.putExtra("DrawId", draw.getDrawId());
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * Find all views bu their ID's
     */
    private void findViews() {
        fab = findViewById(R.id.fab);
        ll_drawsContainer = findViewById(R.id.drawsContainer);
    }
}
