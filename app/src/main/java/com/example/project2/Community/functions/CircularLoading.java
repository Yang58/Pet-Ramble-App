package com.example.project2.Community.functions;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.example.project2.R;

public class CircularLoading {
    AlertDialog dialog;

    public CircularLoading(View v,LayoutInflater inflater) {
        AlertDialog.Builder dialog_builder = new AlertDialog.Builder(v.getContext());
        LayoutInflater layoutInflater = inflater;
        dialog_builder.setView(layoutInflater.inflate(R.layout.fragment_custom_circular_loading, null));
        dialog_builder.setCancelable(false);
        dialog = dialog_builder.create();
    }

    public void show(){
        dialog.show();
        dialog.getWindow().setLayout(400,400);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void hide(){
        dialog.hide();
    }
}
