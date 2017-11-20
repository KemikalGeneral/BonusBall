package com.example.kemik.bonusball;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kemik.bonusball.Database.DBHelper;
import com.example.kemik.bonusball.Entities.Draw;

import java.util.ArrayList;

/**
 * Created by kemik on 12/11/2017.
 */

public class DrawArrayAdapter extends ArrayAdapter<Draw> {

    DBHelper db = new DBHelper(getContext());

    public DrawArrayAdapter(@NonNull Context context) {
        super(context, 0);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_draw, parent, false);
        }

        final Draw draw = getItem(position);

        // Draw name
        TextView tv_drawName = convertView.findViewById(R.id.drawListItemName);
        tv_drawName.setText(draw.getDrawName());

        // Amount of remaining numbers
        TextView tv_amountOfRemainingNumbers = convertView.findViewById(R.id.drawListItemRemaining);
        tv_amountOfRemainingNumbers.setText(String.valueOf(db.getAvailableAmountOfNumbers(draw.getDrawId())));
        tv_amountOfRemainingNumbers.append(" (left)");

        // Go to DrawDetail on ListItem click
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DrawDetail.class);
                intent.putExtra("DrawId", draw.getDrawId());
                getContext().startActivity(intent);
                ((Activity) getContext()).finish();
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ArrayList<Integer> remainingNumbers = new ArrayList<>();

                remainingNumbers.addAll(db.getRemainingNumbers(draw.getDrawId()));
                for (int i = 0; i < remainingNumbers.size(); i++) {
                    System.out.print(remainingNumbers.get(i) + " * ");
                }

                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra("remainingNumbers", remainingNumbers);
                getContext().startActivity(intent);
                ((Activity) getContext()).finish();

                return false;
            }
        });

        return convertView;
    }
}
