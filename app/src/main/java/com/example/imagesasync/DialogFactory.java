package com.example.imagesasync;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.analytics.FirebaseAnalytics;

public class DialogFactory extends DialogFragment {
    private FirebaseAnalytics firebaseAnalytics;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context;
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Welcome! \n Swipe Right for Like \n Swipe Left for Dislike")
                .setPositiveButton("Gotcha Boss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Dialog Box");
                        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Main Dialog");
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
                        // FIRE ZE MISSILES!
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
    public Dialog menuDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Welcome! \n Swipe Right for Like \n Swipe Left for Dislike")
                .setNegativeButton("",null)
                .setPositiveButton("Gotcha Boss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getContext(),DetailScreen.class);
                        // FIRE ZE MISSILES!
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
