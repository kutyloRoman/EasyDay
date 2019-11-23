package com.kutylo.notes.notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.kutylo.notes.SettingsActivity;
import com.kutylo.notes.dialog.ChooseFolderDialog;
import com.kutylo.notes.R;
import com.kutylo.notes.Utils;

import java.util.ArrayList;

public class NotesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

//--------VARIABLES---------------------------------------------------------------------------------
    //---Constants--------------------------------------------
    private static final int IDM_RESTORE =1 ;
    private static final int IDM_DELETE =2 ;

    //-------------Settings------------------------------
    public static SharedPreferences sharedPref;
    private int sizeText;
    private String styleText;
    private Boolean showDate;

    //------Menu---------------------------------------
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    //---View----------------------------
    private View editNowView;
    private Toolbar toolbar;
    private EditText search;
    private ListView notes;
    private LinearLayout panelEdit;
    private FloatingActionButton btn_addNote;

    DbNotesHelper dbNotesHelper;
    MyAdapterNotes myAdapter;

    public static String nowUseFolders="AllNotes";
    private Boolean isChecked = false;
    private Boolean isSearchMode=false;

    DialogFragment folderDialog;
    String textNote;

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)  {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        dbNotesHelper = new DbNotesHelper(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar); //set toolbar
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawer,toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        panelEdit = (LinearLayout)findViewById(R.id.panel);  //hide edit bottom panel
        panelEdit.setVisibility(View.INVISIBLE);

        btn_addNote=(FloatingActionButton)findViewById(R.id.action_add_note);
        notes = (ListView) findViewById(R.id.notes);
        registerForContextMenu(notes);

        notes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(nowUseFolders.equals("RemovedNotes")){
                    editNowView=view;
                    openContextMenu(notes);
                    return true;
                }else{
                    btn_addNote.hide();
                    panelEdit.setVisibility(View.VISIBLE);
                    editNowView = view;
                    return true;
                }
            }
        }); //set listener for task,show edit panel

        loadSettings();
        Intent intent = getIntent();
        nowUseFolders=intent.getStringExtra("FolderName");
        loadList();

    }

    @Override
    public void onBackPressed() {
        if(isSearchMode) {
            isSearchMode = false;
            search.setText(getResources().getString(R.string.title_search));
            NotesActivity.this.myAdapter.getFilter().filter(null);
            notes.setTextFilterEnabled(false);
            search.setCursorVisible(false);
            myAdapter.notifyDataSetChanged();
        }else if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            panelEdit.setVisibility(View.INVISIBLE);
            btn_addNote.show();
        }
    }


    //-------------MENU-----------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        Drawable sort = menu.getItem(0).getIcon();
        Drawable folders = menu.getItem(1).getIcon();
        //Drawable folders=toolbar.getNavigationIcon();
        sort.mutate();
        //setting.mutate();
        folders.mutate();

            toolbar.setTitleTextColor(Color.BLACK);
            sort.setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_IN);
            //setting.setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_IN);
            folders.setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_IN);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String orderBy = null;
        switch (item.getItemId()) {
            //sort
            case R.id.action_sort:
                ArrayList<String> taskList=new ArrayList<>();
                if (!isChecked) {
                    isChecked = true;
                    orderBy = "Note";
                    switch (nowUseFolders){
                        case "AllNotes":
                            taskList = dbNotesHelper.getAllNotesList(orderBy);
                            break;
                        case "RemovedNotes":
                             taskList = dbNotesHelper.getRemovedNotesList(orderBy);
                            break;
                         default:
                             taskList = dbNotesHelper.getNoteList(nowUseFolders,orderBy);
                             break;
                    }
                    myAdapter.clear();
                    myAdapter.addAll(taskList);
                    myAdapter.notifyDataSetChanged();
                    break;
                } else {
                    isChecked = false;
                    orderBy = "Note" + " DESC";
                    switch (nowUseFolders){
                        case "AllNotes":
                            taskList = dbNotesHelper.getAllNotesList(orderBy);
                            break;
                        case "RemovedNotes":
                            taskList = dbNotesHelper.getRemovedNotesList(orderBy);
                            break;
                        default:
                            taskList = dbNotesHelper.getNoteList(nowUseFolders,orderBy);
                            break;
                    }
                    myAdapter.clear();
                    myAdapter.addAll(taskList);
                    myAdapter.notifyDataSetChanged();
                    break;
                }
            //settings
            case R.id.action_setting:
                Intent intent=new Intent(NotesActivity.this, FoldersActivity.class);
                intent.putExtra("FolderName",nowUseFolders);
                startActivity(intent);


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, IDM_RESTORE, Menu.NONE, "Restore");
        menu.add(Menu.NONE, IDM_DELETE, Menu.NONE, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case IDM_RESTORE:
                restoreNote(editNowView);
                break;
            case IDM_DELETE:
                deleteFromRemoved(editNowView);
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_notes:
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this,SettingsActivity.class));
                break;
        }
        drawer.closeDrawers();
        return true;
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu){
//        if (Utils.sTheme == 1) {
//            Drawable add = menu.getItem(0).getIcon();
//            Drawable sort = menu.getItem(1).getIcon();
//            //Drawable theme = menu.getItem(2).getIcon();
//            Drawable setting = menu.getItem(3).getIcon();
//            Drawable folders=toolbar.getNavigationIcon();
//            add.mutate();
//            sort.mutate();
//            //theme.mutate();
//            setting.mutate();
//            folders.mutate();
//
//            toolbar.setTitleTextColor(Color.WHITE);
//            //menu.getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_white_theme));
//            //theme = menu.getItem(2).getIcon();
//            add.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
//            sort.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
//            //theme.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
//            setting.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
//            folders.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
//            return true;
//        }
//
//        return super.onPrepareOptionsMenu(menu);
//    }

    //------


    //-------LOAD-----------------------------------------------------------------------------------

    //-----LOAD-------------------------------------------------------------------------------------

    public void loadList(){
        btn_addNote.show();
        if(nowUseFolders==null||nowUseFolders.equals("AllNotes")){
            nowUseFolders="AllNotes";
            toolbar.setTitle("All Notes");
            loadAllNotesList();
        }else if(nowUseFolders.equals("RemovedNotes")) {
            btn_addNote.hide();
            toolbar.setTitle(nowUseFolders);
            loadRemovedNotesList();
        }else{
            toolbar.setTitle(nowUseFolders);
            loadNotesList(nowUseFolders);
        }
    }

    private void loadAllNotesList() {
        ArrayList<String> taskList = dbNotesHelper.getAllNotesList("null");
        if (myAdapter == null) {
//            myAdapter = new MyAdapterNotes(this,taskList);
            myAdapter = new MyAdapterNotes(this,taskList,sizeText,styleText,showDate);
            notes.setAdapter(myAdapter);
        } else {
            myAdapter.clear();
            myAdapter.addAll(taskList);
            myAdapter.notifyDataSetChanged();
        }
    }

    private void loadRemovedNotesList() {
        ArrayList<String> removedNotesList = dbNotesHelper.getRemovedNotesList("");
        if (myAdapter == null) {
            myAdapter = new MyAdapterNotes(this,removedNotesList,sizeText,styleText,false);
            notes.setAdapter(myAdapter);
        } else {
            myAdapter.clear();
            myAdapter.addAll(removedNotesList);
            myAdapter.notifyDataSetChanged();
        }
    }

    private void loadNotesList(String folderName) {
        ArrayList<String> taskList = dbNotesHelper.getNoteList(folderName,"null");
        if (myAdapter == null) {
            myAdapter=new MyAdapterNotes(this,taskList,sizeText,styleText,showDate);
            notes.setAdapter(myAdapter);
        } else {
            myAdapter.clear();
            myAdapter.addAll(taskList);
            myAdapter.notifyDataSetChanged();
        }
    }

    public void setSearchTasks(View v) {
        isSearchMode=true;

        search = (EditText) findViewById(R.id.search);
        search.setText("");
        search.setCursorVisible(true);
        notes.setTextFilterEnabled(true);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                NotesActivity.this.myAdapter.getFilter().filter(charSequence);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }


    //------------WORK-WITH-NOTE--------------------------------------------------------------------

    public void createNote(View view){
        final EditText noteEditText = new EditText(this);

        if(nowUseFolders.equals("AllNotes")){

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Add new note")
                    .setMessage("Write your note:")
                    .setView(noteEditText)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            textNote = String.valueOf(noteEditText.getText());
                            folderDialog=new ChooseFolderDialog(textNote);
                            folderDialog.show(getSupportFragmentManager(),"ChooseFolder dialog");
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.show();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


        }else {

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Add new note")
                    .setMessage("Write your note")
                    .setView(noteEditText)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String note = String.valueOf(noteEditText.getText());
                            dbNotesHelper.insertNewNote(note, nowUseFolders);
                            loadNotesList(nowUseFolders);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.show();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void deleteNote(View view) {

        panelEdit.setVisibility(View.INVISIBLE);
        TextView taskTextView = (TextView) editNowView.findViewById(android.R.id.text1);
        String note = String.valueOf(taskTextView.getText());
        dbNotesHelper.deleteNote(note,nowUseFolders);
        loadList();
    }

    public void updateNote(View view) {
        TextView taskTextView = (TextView) editNowView.findViewById(android.R.id.text1);
        final String taskOld = String.valueOf(taskTextView.getText());
        final EditText taskEditText = new EditText(this);

        int position = taskOld.length();
        taskEditText.setText(taskOld);
        taskEditText.setSelection(position);
        panelEdit.setVisibility(View.INVISIBLE);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New Task")
                .setMessage("What do you want to do next?")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String taskNew = String.valueOf(taskEditText.getText());
                        dbNotesHelper.updateNote(taskNew, taskOld,nowUseFolders);
                        loadAllNotesList();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void checkNote(final View view) {
        panelEdit.setVisibility(View.INVISIBLE);
        View parent = (View) editNowView.getParent();
        TextView taskTextView = (TextView) editNowView.findViewById(android.R.id.text1);
        final String task = String.valueOf(taskTextView.getText());
        Log.e("dfd",task);
        dbNotesHelper.checkNote(task, nowUseFolders);
        loadList();
    }

    public void sendNote(final View view) {
        TextView taskTextView = (TextView) editNowView.findViewById(android.R.id.text1);

        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.send_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                //SendText
                                switch (item.getItemId()) {
                                    case R.id.sendText:
                                        final String task = String.valueOf(taskTextView.getText());
                                        Intent sendIntent = new Intent();
                                        sendIntent.setAction(Intent.ACTION_SEND);
                                        sendIntent.putExtra(Intent.EXTRA_TEXT, task);
                                        sendIntent.setType("text/plain");
                                        startActivity(sendIntent);
                                        panelEdit.setVisibility(View.INVISIBLE);
                                        return true;
                                    //SendImage
                                    case R.id.sendImage:
                                        Bitmap imageTask=Utils.getBitmapFromView(taskTextView);
                                        Utils.send(getBaseContext(), Utils.saveImage(getBaseContext(), imageTask));
                                        panelEdit.setVisibility(View.INVISIBLE);
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
        popupMenu.show();

    }

    public void moveNote(View view) {
        ArrayList<String> listFolders= dbNotesHelper.getFolderList("");
        TextView noteTextView = (TextView) editNowView.findViewById(android.R.id.text1);
        String moveNote =String.valueOf(noteTextView.getText());

        PopupMenu moveMenu = new PopupMenu(this, view);
        moveMenu.inflate(R.menu.move_menu);
        for(int i=1;i<listFolders.size();i++){
            if(!listFolders.get(i).equals("AllNotes")&&!listFolders.get(i).equals(dbNotesHelper.getFolder(moveNote))){
                moveMenu.getMenu().add(Menu.NONE, i, Menu.NONE,listFolders.get(i));
            }
        }
        moveMenu.show();

        moveMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
               String folderOld=nowUseFolders;
               String folderNew=listFolders.get(item.getItemId());
               dbNotesHelper.moveNote(moveNote,folderNew,folderOld);
               loadList();
               return true;
            }
        });

    }

    public void restoreNote(View view){
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Restore note");
            adb.setMessage("Do you want restore this note?");
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    TextView noteTextView = (TextView) editNowView.findViewById(android.R.id.text1);
                    String restoreNote = String.valueOf(noteTextView.getText());
                    dbNotesHelper.restoreNote(restoreNote);
                    loadList();
                }
            });
            adb.setNegativeButton("No",null);
            adb.create();
            adb.show();
    }

    public void deleteFromRemoved(View view){
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Delete note");
        adb.setMessage("Do you want delete this note?");
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TextView noteTextView = (TextView) editNowView.findViewById(android.R.id.text1);
                String deleteNote = String.valueOf(noteTextView.getText());
                dbNotesHelper.deleteFromRemoved(deleteNote);
                loadList();
            }
        });
        adb.setNegativeButton("No",null);
        adb.create();
        adb.show();
    }

    public void makeNotificationFromNote(View view){
        TextView taskTextView = (TextView) editNowView.findViewById(android.R.id.text1);
        String noteToNotification = String.valueOf(taskTextView.getText());
        NotificationNote notificationNote=new NotificationNote(noteToNotification,this);
    }

    //--------SETTINGS------------------------------------------------------------------------------

    public void loadSettings(){
        sharedPref=PreferenceManager.getDefaultSharedPreferences(this);

        sizeText=Integer.valueOf(sharedPref.getString("Size","16"));
        styleText=sharedPref.getString("Style","NORMAL");
        showDate=sharedPref.getBoolean("Date",false);

    }
}