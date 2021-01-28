package com.example.digitrafficinspectorv0;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

class LoadingDialogue {

   private Activity activity;
   private AlertDialog dialog;

    LoadingDialogue(Activity myActivity){
        activity = myActivity;
    }

    void startLoadingDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialogue, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    void dismissDialogue(){
        dialog.dismiss();
    }
}
