package com.example.hoangdung.simplelocation.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.hoangdung.simplelocation.R;

/**
 * Created by USER on 11/29/2017.
 */

public class InputPlaceLabelDialog extends DialogFragment
{

    private  DialogListener dialogListener;
    private EditText editText;
    public interface DialogListener
    {
        void onDialogPositiveClick(String input);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            dialogListener = (DialogListener)activity;
        }
        catch (ClassCastException e)
        {
            throw  new ClassCastException(activity.toString() + " must implement interface DialogListener");
        }

    }




    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK);

        // inflate view
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_input_place_label, null);

        // set initial text to edit view
        editText = (EditText)view.findViewById(R.id.editTextPlaceLabel);
        editText.setText(getArguments().getString("placeName"));

        builder.setView(view)
                .setTitle("Place label")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogPositiveClick(editText.getText().toString());
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }
}
