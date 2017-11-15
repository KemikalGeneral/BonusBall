package com.example.kemik.bonusball;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by kemik on 13/11/2017.
 */

public class ConfirmationDialog extends DialogFragment {

    public interface ConfirmationDialogListener {
        public void onDialogPositiveClick(DialogFragment dialogFragment);

        public void onDialogNegativeClick(DialogFragment dialogFragment);
    }

    // Instance of Interface
    ConfirmationDialogListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (ConfirmationDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " implement confirmation dialog");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Delete this Draw?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogPositiveClick(ConfirmationDialog.this);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogNegativeClick(ConfirmationDialog.this);
                    }
                });
        return builder.create();
    }
}
