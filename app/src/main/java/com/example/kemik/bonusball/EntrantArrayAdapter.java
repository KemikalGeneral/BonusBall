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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kemik.bonusball.Entities.Entrant;

/**
 * Created by kemik on 29/10/2017.
 */

class EntrantArrayAdapter extends ArrayAdapter<Entrant> {

    private long drawId;
    private DBHelper db = new DBHelper(getContext());

    public EntrantArrayAdapter(@NonNull Context context, long drawId) {
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
        final ImageView iv_save = convertView.findViewById(R.id.list_view_save);
        final ImageView iv_paid = convertView.findViewById(R.id.list_view_paid);
        final ImageView iv_unPaid = convertView.findViewById(R.id.list_view_unpaid);

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final int chosenNumber = position + 1;
                Toast.makeText(getContext(), "Clicked: " + String.valueOf(chosenNumber), Toast.LENGTH_SHORT).show();

                tv_name.setVisibility(View.GONE);
                et_name.setVisibility(View.VISIBLE);
                et_name.setText(entrant.getEntrantName());
                System.out.println("entrant name: " + entrant.getEntrantName());
                iv_save.setVisibility(View.VISIBLE);

                showCorrectPaymentIcon(entrant, iv_paid, iv_unPaid);

                iv_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveEntrantToLineNumber(et_name, chosenNumber);
                    }
                });

                iv_paid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changePaymentStatus(chosenNumber, drawId, "paid");

                    }
                });

                iv_unPaid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changePaymentStatus(chosenNumber, drawId, "unpaid");

                    }
                });

                return false;
            }
        });
        return convertView;
    }

    /**
     * If paymentStatus is Paid, show the icon to mark as unpaid.
     * If paymentStatus is unPaid or null, show the icon to mark as Paid.
     *
     * @param entrant
     * @param iv_paid
     * @param iv_unPaid
     */
    private void showCorrectPaymentIcon(Entrant entrant, ImageView iv_paid, ImageView iv_unPaid) {
        System.out.println("payment status: " + entrant.getPaymentStatus());
        if (entrant.getPaymentStatus() == null || entrant.getPaymentStatus().equals("unpaid")) {
            iv_paid.setVisibility(View.VISIBLE);
        } else {
            iv_unPaid.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Save the Entrant name to the chosen LineNumber using the drawId
     *
     * @param et_name
     * @param chosenNumber
     */
    private void saveEntrantToLineNumber(EditText et_name, int chosenNumber) {
        String entrantName = et_name.getText().toString();

        // Save entrant to chosen number
        db.addNameToChosenNumber(entrantName, chosenNumber, drawId);

        refreshThisActivity();
    }

    /**
     * Change the paymentStatus to [status] parameter
     *
     * @param chosenNumber
     * @param drawId
     * @param status
     */
    private void changePaymentStatus(int chosenNumber, long drawId, String status) {
        db.changePaymentStatus(chosenNumber, drawId, status);

        refreshThisActivity();
    }

    /**
     * Reload the current activity passing the drawId as extras to populate the fields
     */
    private void refreshThisActivity() {
        // Go to the DrawDetail activity, sending the drawId as Extras
        Intent intent = new Intent(getContext(), DrawDetail.class);
        intent.putExtra("DrawId", drawId);
        getContext().startActivity(intent);
        ((Activity) getContext()).finish();
    }
}
