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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kemik.bonusball.Entities.Entrant;

/**
 * Created by kemik on 29/10/2017.
 */

class NumberSlotArrayAdapter extends ArrayAdapter<Entrant> {

    private long drawId;

    public NumberSlotArrayAdapter(@NonNull Context context, long drawId) {
        super(context, 0);
        this.drawId = drawId;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.draw_number_slot, parent, false);
        }

        final Entrant entrant = getItem(position);

        TextView tv_slotNumber = convertView.findViewById(R.id.list_view_number);
        tv_slotNumber.setText(String.valueOf(entrant.getLineNumber()));

        final TextView tv_name = convertView.findViewById(R.id.list_view_name_text);
        tv_name.setText(entrant.getEntrantName());

        final EditText et_name = convertView.findViewById(R.id.list_view_name_edit);

        final Button btn_save = convertView.findViewById(R.id.list_view_save);

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final int chosenNumber = position + 1;
                Toast.makeText(getContext(), "Clicked: " + String.valueOf(chosenNumber), Toast.LENGTH_SHORT).show();

                tv_name.setVisibility(View.GONE);
                et_name.setVisibility(View.VISIBLE);
                et_name.setText(entrant.getEntrantName());
                btn_save.setVisibility(View.VISIBLE);

                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String entrantName = et_name.getText().toString();

                        DBHelper db = new DBHelper(getContext());
                        db.addNameToChosenNumber(entrantName, chosenNumber, drawId);

                        btn_save.setVisibility(View.GONE);
                        et_name.setVisibility(View.GONE);
                        tv_name.setVisibility(View.VISIBLE);

                        Intent intent = new Intent(getContext(), DrawDetail.class);
                        intent.putExtra("DrawId", drawId);
                        getContext().startActivity(intent);
                        ((Activity) getContext()).finish();
                    }
                });
                return false;
            }
        });
        return convertView;
    }
}
