package com.kutylo.notes.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.fragment.app.DialogFragment;

import com.kutylo.notes.notes.DbNotesHelper;
import com.kutylo.notes.notes.NotesActivity;
import com.kutylo.notes.R;

import java.util.ArrayList;

public class ChooseFolderDialog extends DialogFragment {

    ArrayList<String> folders;
    ArrayAdapter<String> adapter;
    DbNotesHelper dbNotesHelper;
    String note;

    public ChooseFolderDialog(String note){
        this.note=note;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dbNotesHelper=new DbNotesHelper(getActivity());
        folders=dbNotesHelper.getFolderList("");
        folders.add("Create new folder");

        adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.select_dialog_singlechoice,folders);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose folder:")
                .setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==(folders.size()-1)){
                            String title=getResources().getString(R.string.title_add_folder);
                            String message=getResources().getString(R.string.massage_add_folder);
                            String positiveButton="Add";

                            DialogFragment createDialog=new CreateDialog(title,message,positiveButton,"Folder",note);
                            createDialog.show(getActivity().getSupportFragmentManager(),"choose dialog");
                            dismiss();
                        }else
                            dbNotesHelper.insertNewNote(note,folders.get(i));
                    }
                })
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                NotesActivity activity=(NotesActivity)getActivity();
                activity.loadList();
            }
        });

        return builder.create();
    }


    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d("sds", "Dialog 2: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d("Sdsd", "Dialog 2: onCancel");
    }



}
