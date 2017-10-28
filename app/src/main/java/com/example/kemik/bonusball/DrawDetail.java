package com.example.kemik.bonusball;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DrawDetail extends AppCompatActivity {

    private TextView tv_drawName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_detail);

        // Find all views
        findViews();
    }

    /**
     * Find all views by their ID's
     */
    private void findViews() {
        tv_drawName = findViewById(R.id.drawName);
    }
}
