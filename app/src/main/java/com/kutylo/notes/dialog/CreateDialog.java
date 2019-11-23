package com.kutylo.notes.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.kutylo.notes.notes.DbNotesHelper;

public class CreateDialog extends DialogFragment {
    String mode;

    String name;

    String title;
    String message;
    String note;
    String positiveButton;

    DbNotesHelper dbNotesHelper;
    public CreateDialog(String title,String massege,String positiveButton,String mode,String note){
        this.title=title;
        this.message =massege;
        this.positiveButton=positiveButton;
        this.mode=mode;
        this.note=note;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dbNotesHelper=new DbNotesHelper(getActivity());
        final EditText taskEditText = new EditText(getActivity());

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setView(taskEditText)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = String.valueOf(taskEditText.getText());

                        if(mode.equals("Folder")){
                            dbNotesHelper.createFolder(name);
                            DialogFragment folderDialog=new ChooseFolderDialog(note);
                            folderDialog.show(getActivity().getSupportFragmentManager(),"ChooseFolder dialog");

                        }
                    }
                })
                .setNegativeButton("Cancel", null);
        return dialog.create();
    }

}
