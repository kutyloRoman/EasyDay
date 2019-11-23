package com.kutylo.notes.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.kutylo.notes.notes.DbNotesHelper;
import com.kutylo.notes.notes.NotesActivity;

public class WarningDialog extends DialogFragment  {
    private DbNotesHelper dbNotesHelper;
    private String title;
    private String message;
    private String note;

    public WarningDialog(String title, String message, String note) {
        this.title = title;
        this.message = message;
        this.note = note;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dbNotesHelper=new DbNotesHelper(getActivity());
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Cancel", null);
        return dialog.create();
    }


}
