package com.example.kemik.bonusball;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_entrant, parent, false);
        }

        final Entrant entrant = getItem(position);

        TextView tv_slotNumber = convertView.findViewById(R.id.list_view_number);
        tv_slotNumber.setText(String.valueOf(entrant.getLineNumber()));

        final TextView tv_name = convertView.findViewById(R.id.list_view_name_text);
        tv_name.setText(entrant.getEntrantName());

        final EditText et_name = convertView.findViewById(R.id.list_view_name_edit);
        final ImageView iv_save = convertView.findViewById(R.id.list_view_save);
        final ImageView iv_delete = convertView.findViewById(R.id.list_view_delete);
        final ImageView iv_paid = convertView.findViewById(R.id.list_view_paid);
        final ImageView iv_unPaid = convertView.findViewById(R.id.list_view_unpaid);

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final int chosenNumber = position + 1;

                tv_name.setVisibility(View.GONE);
                et_name.setVisibility(View.VISIBLE);
                et_name.setText(entrant.getEntrantName());


                showCorrectSaveDeleteIcon(et_name, iv_save, iv_delete);
                showCorrectPaymentIcon(entrant, et_name, iv_paid, iv_unPaid);

                iv_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveEntrantToLineNumber(et_name, chosenNumber);
                    }
                });

                iv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteEntrant(chosenNumber, drawId);
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

                return true;
            }
        });

        // Change colour of line numbers to reflect the payment status (green=paid, red=unpaid)
        setNumberColourOnPaymentChange(entrant, tv_slotNumber);

        return convertView;
    }

    /**
     * If there is no name present, show the tick icon to save, if not, show the bin to delete.
     *
     * @param et_name
     * @param iv_save
     * @param iv_delete
     */
    private void showCorrectSaveDeleteIcon(EditText et_name, ImageView iv_save, ImageView iv_delete) {
        if (et_name.getText().toString().trim().length() < 1) {
            iv_save.setVisibility(View.VISIBLE);
        } else {
            iv_delete.setVisibility(View.VISIBLE);
        }
    }

    /**
     * If paymentStatus is Paid, show the icon to mark as unpaid.
     * If paymentStatus is unPaid or null, show the icon to mark as Paid.
     *
     * @param entrant
     * @param iv_paid
     * @param iv_unPaid
     */
    private void showCorrectPaymentIcon(Entrant entrant, EditText et_name, ImageView iv_paid, ImageView iv_unPaid) {
        if (et_name.getText().toString().trim().length() > 0) {
            if (entrant.getPaymentStatus() == null || entrant.getPaymentStatus().equals("unpaid")) {
                iv_paid.setVisibility(View.VISIBLE);
            } else {
                iv_unPaid.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Save the Entrant name to the chosen LineNumber using the drawId,
     * only if a name is present
     *
     * @param et_name
     * @param chosenNumber
     */
    private void saveEntrantToLineNumber(EditText et_name, int chosenNumber) {
        String entrantName = et_name.getText().toString().trim();

        if (entrantName.equals("")) {
            Toast.makeText(getContext(), "You must enter a name!", Toast.LENGTH_SHORT).show();
        } else {
            // Save entrant to chosen number
            db.updateNameToChosenNumber(entrantName, chosenNumber, drawId);

            refreshThisActivity();
        }
    }

    /**
     * Upon deleting an Entrant, the name will be updated to 'null' and the paymentStatus will be set to unpaid.
     *
     * @param lineNumber
     * @param drawId
     */
    private void deleteEntrant(int lineNumber, long drawId) {
        db.updateNameToChosenNumber(null, lineNumber, drawId);
        db.changePaymentStatus(lineNumber, drawId, "unpaid");

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
     * Change the colour of the line number to reflect the payment status (green=paid, red=unpaid)
     *
     * @param entrant
     * @param tv_slotNumber
     */
    private void setNumberColourOnPaymentChange(Entrant entrant, TextView tv_slotNumber) {
        if (entrant.getPaymentStatus() == null || entrant.getPaymentStatus().equals("unpaid")) {
            tv_slotNumber.setTextColor(Color.RED);
        } else {
            tv_slotNumber.setTextColor(Color.GREEN);
        }
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
