package com.kutylo.notes.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kutylo.notes.MyAdapter;
import com.kutylo.notes.R;
import com.kutylo.notes.Utils;

import java.util.ArrayList;

public class FoldersActivity extends AppCompatActivity {

    DbNotesHelper dbNotesHelper;
    MyAdapter myAdapter;

    private Toolbar toolbar;
    private EditText search_folder;
    private ListView folders;
    private LinearLayout actionPanel;
    private FloatingActionButton btn_addFolder;

    private View editNowView;

    private Boolean isSearchMode=false;
    private Boolean isChecked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbNotesHelper = new DbNotesHelper(this);
        //Utils.sTheme=dbNotesHelper.nowUseTheme();
        //Utils.onActivityCreateSetTheme(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_folders);

        toolbar = (Toolbar) findViewById(R.id.toolbar_folder);
        search_folder=(EditText)findViewById(R.id.search_folder);
        folders = (ListView) findViewById(R.id.folders);
        btn_addFolder=(FloatingActionButton)findViewById(R.id.action_add_folder);
        actionPanel = (LinearLayout) findViewById(R.id.action_folders);  //hide edit bottom panel


        actionPanel.setVisibility(View.INVISIBLE);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_notes);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentFolder = getIntent();
                String nowUseFolders=intentFolder.getStringExtra("FolderName");
                Intent intent=new Intent(FoldersActivity.this, NotesActivity.class);
                intent.putExtra("FolderName",nowUseFolders);
                startActivity(intent);
            }
        });

        loadFolderList();

        folders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                TextView folderTextView = (TextView)view.findViewById(android.R.id.text1);
                String folderName=String.valueOf(folderTextView.getText());
                Intent intent=new Intent(FoldersActivity.this, NotesActivity.class);
                intent.putExtra("FolderName",folderName);
                startActivity(intent);
            }
        });

        folders.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView folderTextView = (TextView) view.findViewById(android.R.id.text1);
                String check=String.valueOf(folderTextView.getText());
                if(check.equals("AllNotes")||check.equals("RemovedNotes")){
                    return true;
                }else {
                    btn_addFolder.hide();
                    actionPanel.setVisibility(View.VISIBLE);
                    editNowView = view;
                    return true;
                }
            }
        }); //set listener for task,show edit panel
    }

    @Override
    public void onBackPressed() {
        if(isSearchMode) {
            isSearchMode = false;
            search_folder.setText(getResources().getString(R.string.title_search));
            FoldersActivity.this.myAdapter.getFilter().filter(null);
            folders.setTextFilterEnabled(false);
            search_folder.setCursorVisible(false);
            myAdapter.notifyDataSetChanged();
        }else{
            actionPanel.setVisibility(View.INVISIBLE);
            btn_addFolder.show();
        }
    }


    //----MENU--------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.folder_menu, menu);

        Drawable sort = menu.getItem(0).getIcon();
        Drawable removed = menu.getItem(1).getIcon();
        Drawable folders=toolbar.getNavigationIcon();
        sort.mutate();
        removed.mutate();
        folders.mutate();

        toolbar.setTitleTextColor(Color.BLACK);
        sort.setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_IN);
        removed.setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_IN);
        folders.setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_IN);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String orderBy = null;
        switch (item.getItemId()) {
            //sort
            case R.id.action_sort_folders:
                if (!isChecked) {
                    isChecked = true;
                    Log.e("Sort",isChecked.toString());
                    orderBy = "FolderName";
                    ArrayList<String> folderList = dbNotesHelper.getFolderList(orderBy);
                    myAdapter.clear();
                    myAdapter.addAll(folderList);
                    myAdapter.notifyDataSetChanged();
                    break;
                } else {
                    isChecked = false;
                    Log.e("Sort",isChecked.toString());
                    orderBy = "FolderName" + " DESC";
                    ArrayList<String> folderList2 = dbNotesHelper.getFolderList(orderBy);
                    myAdapter.clear();
                    myAdapter.addAll(folderList2);
                    myAdapter.notifyDataSetChanged();
                    break;
                }
                //removedNotes
            case R.id.action_removed_notes:
                Intent intent=new Intent(FoldersActivity.this, NotesActivity.class);
                intent.putExtra("FolderName","RemovedNotes");
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if (Utils.sTheme == 1) {
            Drawable add = menu.getItem(0).getIcon();
            Drawable sort = menu.getItem(1).getIcon();
            Drawable delete = menu.getItem(2).getIcon();
            Drawable folders=toolbar.getNavigationIcon();
            add.mutate();
            sort.mutate();
            delete.mutate();
            folders.mutate();

            toolbar.setTitleTextColor(Color.WHITE);
            add.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
            sort.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
            delete.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
            folders.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
            return true;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public void setSearchFolders(View v) {
        isSearchMode=true;

        search_folder = (EditText) findViewById(R.id.search_folder);
        search_folder.setText("");
        search_folder.setCursorVisible(true);
        folders.setTextFilterEnabled(true);

        search_folder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                FoldersActivity.this.myAdapter.getFilter().filter(charSequence);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    //---WORK-WITH-FOLDERS--------------------------------------------------------------------------
    public void loadFolderList() {
        toolbar.setTitle(String.format("Folders: %d", dbNotesHelper.countFolders()));
        ArrayList<String> folderList = dbNotesHelper.getFolderList("");

        if (myAdapter == null) {
            myAdapter = new MyAdapter(this,folderList);
            folders.setAdapter(myAdapter);
        } else {
            myAdapter.clear();
            myAdapter.addAll(folderList);
            myAdapter.notifyDataSetChanged();
        }
    }

    public void createFolder(View view) {
        final EditText taskEditText = new EditText(this);
        btn_addFolder.hide();
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New Folder")
                .setMessage("Enter name your folder:")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String folder = String.valueOf(taskEditText.getText());
                        if(dbNotesHelper.createFolder(folder)){
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            btn_addFolder.show();
                            loadFolderList();
                        }else{
                            btn_addFolder.show();
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            Toast.makeText(FoldersActivity.this,"Folder already exist!!",Toast.LENGTH_LONG).show();
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        btn_addFolder.show();
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    }
                })
                .create();
        dialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void deleteFolder(View view) {

        actionPanel.setVisibility(View.INVISIBLE);
        TextView folderView = (TextView) editNowView.findViewById(android.R.id.text1);
        String folderName = String.valueOf(folderView.getText());
        dbNotesHelper.deleteFolder(folderName);
        loadFolderList();
        btn_addFolder.show();
    }

    public void renameFolder(View view) {
        TextView folderTextView = (TextView) editNowView.findViewById(android.R.id.text1);
        final String folderOld = String.valueOf(folderTextView.getText());
        final EditText taskEditText = new EditText(this);

        int position = folderOld.length();
        taskEditText.setText(folderOld);
        taskEditText.setSelection(position);
        actionPanel.setVisibility(View.INVISIBLE);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Rename folder")
                .setMessage("Enter new name:")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        String folderNew = String.valueOf(taskEditText.getText());
                        dbNotesHelper.renameFolder(folderNew,folderOld);
                        loadFolderList();
                        btn_addFolder.show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

}
