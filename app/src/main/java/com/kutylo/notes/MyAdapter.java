package com.kutylo.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kutylo.notes.notes.DbNotesHelper;

import java.util.ArrayList;


public class MyAdapter extends ArrayAdapter<String> {

    DbNotesHelper dbNotesHelper;

    public MyAdapter(Context context,ArrayList<String> tasks_list){
        super(context,android.R.layout.simple_list_item_2,tasks_list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        dbNotesHelper =new DbNotesHelper(getContext());
        View view = convertView;
        if (view == null) {
            view=LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_list_item_2, null);
        }


        if(getItem(position).equals("AllNotes")){
            ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(position));
            ((TextView) view.findViewById(android.R.id.text2)).setText("");
            //((TextView) view.findViewById(android.R.id.text1)).setText
        }else{
            Integer count_notes = (Integer) dbNotesHelper.countRow(getItem(position));
            ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(position));
            ((TextView) view.findViewById(android.R.id.text2)).setText("Notes:" + count_notes.toString());
        }

        return view;
    }
}
