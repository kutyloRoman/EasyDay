package com.kutylo.notes.notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import com.kutylo.notes.R;
import com.kutylo.notes.notes.DbNotesHelper;

import java.util.ArrayList;


public class MyAdapterNotes extends ArrayAdapter<String> {

    DbNotesHelper dbNotesHelper;
    int fontText;
    String styleText;
    Boolean showDate;
    Context context;

    public MyAdapterNotes(Context context, ArrayList<String> tasks_list){
        super(context,android.R.layout.simple_list_item_1,tasks_list);
        this.context=context;
    }

    public MyAdapterNotes(Context context, ArrayList<String> tasks_list,int fontText,String styleText,Boolean showDate){
        super(context,android.R.layout.simple_list_item_1,tasks_list);
        this.fontText=fontText;
        this.styleText=styleText;
        this.showDate=showDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        dbNotesHelper =new DbNotesHelper(getContext());
        View view = convertView;

        if (view == null) {
            if(showDate){
                view=LayoutInflater.from(getContext())
                        .inflate(android.R.layout.simple_list_item_2, null);
            }else{
                view=LayoutInflater.from(getContext())
                        .inflate(android.R.layout.simple_list_item_1, null);
            }

        }

        if(showDate){
            String date=dbNotesHelper.getDate(getItem(position));
            ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(position));
            ((TextView) view.findViewById(android.R.id.text2)).setText(date);
            ((TextView) view.findViewById(android.R.id.text1)).setTextSize(fontText);
        }else {
            ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(position));
            ((TextView) view.findViewById(android.R.id.text1)).setTextSize(fontText);
        }

        switch (styleText){
            case "BOLD":
                ((TextView) view.findViewById(android.R.id.text1)).setTypeface(Typeface.DEFAULT_BOLD);
                break;
            case "ITALIC":
                ((TextView) view.findViewById(android.R.id.text1)).setTypeface(null,Typeface.ITALIC);
        }

        return view;
    }


}
