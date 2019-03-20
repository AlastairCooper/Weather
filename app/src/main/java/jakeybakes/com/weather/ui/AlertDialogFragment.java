package jakeybakes.com.weather.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import jakeybakes.com.weather.R;

/**
 * Created by repoo on 01/03/2019.
 */

public class AlertDialogFragment extends DialogFragment   {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.dialog_error_title)
                .setMessage(R.string.dialog_error_text)
                .setPositiveButton(R.string.dialog_error_button_text,null);
        return builder.create();
    }
}