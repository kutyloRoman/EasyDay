package com.kutylo.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kutylo.notes.notes.NotesActivity;

public class SettingsActivity extends AppCompatActivity  {
    private Toolbar toolbar;
    public static SharedPreferences sharedPref;

    public static TextView task;
    public static String nowUseFolders="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        task=(TextView)findViewById(R.id.task_title);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_frame, new SettingsFragment())
                .commit();

        setContentView(R.layout.activity_settings);

        sharedPref=PreferenceManager.getDefaultSharedPreferences(this);


        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        checkTheme();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SettingsActivity.this, NotesActivity.class);
                intent.putExtra("FolderName",nowUseFolders);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        nowUseFolders=intent.getStringExtra("FolderName");

    }

    public void checkTheme(){
        if (Utils.sTheme==1){
            Drawable back=toolbar.getNavigationIcon();
            back.mutate();
            back.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
            toolbar.setTitleTextColor(Color.WHITE);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener
     {
         SharedPreferences prefs;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings, rootKey);
            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            prefs.registerOnSharedPreferenceChangeListener(this);
        }

         @Override
         public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
             Preference pref=findPreference(key);
             if (pref instanceof ListPreference) {
                 ListPreference listPreference = (ListPreference) pref;
                 int index = listPreference.findIndexOfValue(key);
                 Log.e("fsfsf",listPreference.getValue());
                 //task.setTextSize(Integer.parseInt(listPreference.getValue()));
                 //pref.setSummary(listPreference.getEntry());
             }
        }


         @Override
         public void onPause() {

             prefs.unregisterOnSharedPreferenceChangeListener(this);
             super.onPause();
         }


       }
}

