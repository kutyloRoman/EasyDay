package com.kutylo.notes.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
//перенести в інший потік

public class DbNotesHelper extends SQLiteOpenHelper {


    private static final String DB_NAME="Notes";

    private static final String DB_TABLE_REMOVED_NOTES ="RemovedNotes";
    private static final String DB_COLUMN_REMOVED_NOTE ="RemovedNote";
    private static final String DB_COLUMN_REMOVED_FOLDER ="RemovedFolder";

    private static final String DB_TABLE_FOLDERS="Folders";
    private static final String DB_COLUMN_FOLDER_NAME ="FolderName";

    private static final String DB_TABLE_ALL_NOTES ="AllNotes";
    private static final String DB_COLUMN_NOTE ="Note";
    private static final String DB_COLUMN_FOLDER ="Folder";
    private static final String DB_COLUMN_DATE ="DATE";
    private static final String DB_COLUMN_ISCHECKED ="IsChecked";

    private static final int DB_VERSION=1;

    public DbNotesHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query_taskTable = String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT NOT NULL,%s TEXT NOT NULL,%s TEXT NOT NULL, %s INTEGER NOT NULL);", DB_TABLE_ALL_NOTES, DB_COLUMN_NOTE,DB_COLUMN_FOLDER,DB_COLUMN_DATE, DB_COLUMN_ISCHECKED);
        db.execSQL(query_taskTable);
        String query_removedNotesTable = String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT NOT NULL,%s TEXT NOT NULL);", DB_TABLE_REMOVED_NOTES, DB_COLUMN_REMOVED_NOTE,DB_COLUMN_REMOVED_FOLDER);
        db.execSQL(query_removedNotesTable);
        String query_foldersTable = String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT NOT NULL);", DB_TABLE_FOLDERS, DB_COLUMN_FOLDER_NAME);
        db.execSQL(query_foldersTable);

        String query_default=String.format("INSERT INTO %s (%s) VALUES('%s');",DB_TABLE_FOLDERS,DB_COLUMN_FOLDER_NAME,DB_TABLE_ALL_NOTES);
        db.execSQL(query_default);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query_taskTable = String.format("DELETE TABLE IF EXISTS %s", DB_TABLE_ALL_NOTES);
        db.execSQL(query_taskTable);
        String query_foldersTable = String.format("DELETE TABLE IF EXISTS %s", DB_TABLE_FOLDERS);
        db.execSQL(query_foldersTable);
        String query_removedTable = String.format("DELETE TABLE IF EXISTS %s", DB_TABLE_REMOVED_NOTES);
        db.execSQL(query_removedTable);

        onCreate(db);
    }

//    @Override
//    protected void finalize() throws Throwable {
//        super.finalize();
//        db.close();
//    }

    //-----HELPFUL----------------------------------------------------------------------------------

    public boolean checkNameFolder(String nameFolder){
        SQLiteDatabase db=this.getReadableDatabase();
        String selection=String.format("%s LIKE",DB_COLUMN_FOLDER_NAME);
        Cursor cursor = db.query(DB_TABLE_FOLDERS,null,selection+"?",new String[]{nameFolder},null,null,null);
        int count=cursor.getCount();
        cursor.close();
        return (count>0);
    }

    public int countFolders() {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE_FOLDERS,null,null,null,null,null,null);
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public int countRow(String folderName){
        Cursor cursor;
        SQLiteDatabase db=this.getReadableDatabase();

        if(folderName.equals(DB_TABLE_REMOVED_NOTES)){
            String COUNT_ROW=String.format("SELECT * FROM %s", DB_TABLE_REMOVED_NOTES);
            cursor=db.rawQuery(COUNT_ROW,null);
        }else{
            cursor=db.query(DB_TABLE_ALL_NOTES,null,DB_COLUMN_FOLDER+"=?",new String[]{folderName},null,null,null,null);
        }
        int count=cursor.getCount();
        cursor.close();

        db.close();
        return count;
    }

    public String getDate(String note){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE_ALL_NOTES,null,DB_COLUMN_NOTE+"=?",new String[]{note},null,null,null);
        cursor.moveToFirst();
        String date=cursor.getString(cursor.getColumnIndex(DB_COLUMN_DATE));
        return date;
    }

    //---
    public int countALLTablesDb() {
        SQLiteDatabase db=this.getReadableDatabase();
        String SQL_GET_ALL_TABLES = "SELECT name FROM sqlite_master WHERE type = 'table' AND name != 'android_metadata' AND name != 'sqlite_sequence';";
        //String SQL_GET_ALL_TABLES = "SELECT name FROM sqlite_master WHERE type = 'table';";
        Cursor cursor = db.rawQuery(SQL_GET_ALL_TABLES, null);

        while (cursor.moveToNext()){
            int index=cursor.getColumnIndex("name");
            Log.e("Table: ",cursor.getString(index));
        }

        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public void deleteAllTable(){

        SQLiteDatabase db=this.getWritableDatabase();
        String query_taskTable = String.format("DELETE TABLE IF EXISTS %s", DB_TABLE_ALL_NOTES);
        db.execSQL(query_taskTable);
        String query_foldersTable = String.format("DELETE TABLE IF EXISTS %s", DB_TABLE_FOLDERS);
        db.execSQL(query_foldersTable);
        String query_removedTable = String.format("DELETE TABLE IF EXISTS %s", DB_TABLE_REMOVED_NOTES);
        db.execSQL(query_removedTable);
        onCreate(db);
    }

    //-----NOTE--------------------------------------------------------------------------------

    public void checkNote(String note,String folderName){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        Cursor cursor=db.query(DB_TABLE_ALL_NOTES,null,DB_COLUMN_NOTE+"=?",new String[]{note},null,null,null);
        cursor.moveToFirst();

        int isCheck=cursor.getInt(cursor.getColumnIndex(DB_COLUMN_ISCHECKED));
        if(isCheck==1){
            value.put(DB_COLUMN_ISCHECKED,0);
            db.update(DB_TABLE_ALL_NOTES,value,DB_COLUMN_NOTE+"=?",new String[]{note});
        }else{
            value.put(DB_COLUMN_ISCHECKED,1);
            db.update(DB_TABLE_ALL_NOTES,value,DB_COLUMN_NOTE+"=?",new String[]{note});
        }

        db.close();
    } //Check note in top of list

    public void insertNewNote(String note, String folderName){ //Insert note in folder and in allNotes table
        SQLiteDatabase db= this.getWritableDatabase();//відкритя бд
        ContentValues values = new ContentValues(); //створ класу для занес рядка
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        values.put(DB_COLUMN_NOTE,note);
        values.put(DB_COLUMN_FOLDER,folderName);
        values.put(DB_COLUMN_DATE,currentDateTimeString);
        values.put(DB_COLUMN_ISCHECKED,0);
        db.insertWithOnConflict(DB_TABLE_ALL_NOTES,null,values,SQLiteDatabase.CONFLICT_REPLACE);

        db.close();
    }

    public void updateNote(String noteNew, String noteOld, String folderName){ //Update note in folder and allNotes table
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues(); //створ класу для занес рядка
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        values.put(DB_COLUMN_NOTE, noteNew);
        values.put(DB_COLUMN_DATE,currentDateTimeString);
        db.update(DB_TABLE_ALL_NOTES,values, DB_COLUMN_NOTE +"=?",new String[]{noteOld});
        db.close();
    }

    public void deleteNote(String note, String folderName){ //Delete note from folderName and allNotes table
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values=new ContentValues();
        String folder=folderName;

       if(folderName.equals("AllNotes")) {
           Cursor cursor = db.query(DB_TABLE_ALL_NOTES, null, DB_COLUMN_NOTE + "=?", new String[]{note}, null, null, null);
           cursor.moveToFirst();
           folder = cursor.getString(cursor.getColumnIndex(DB_COLUMN_FOLDER));
       }

        values.put(DB_COLUMN_REMOVED_NOTE,note);
        values.put(DB_COLUMN_REMOVED_FOLDER,folder);
        db.insertWithOnConflict(DB_TABLE_REMOVED_NOTES,null,values,SQLiteDatabase.CONFLICT_REPLACE);

        db.delete(DB_TABLE_ALL_NOTES, DB_COLUMN_NOTE + " = ?",new String[]{note});

        db.close();
    }

    public void moveNote(String note,String folderNew, String folderOld){ //Update note in folder and allNotes table
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues(); //створ класу для занес рядка

//        String folder=folderOld;
//
//        if(folderOld.equals("AllNotes")) {
//            Cursor cursor = db.query(DB_TABLE_ALL_NOTES, null, DB_COLUMN_NOTE + "=?", new String[]{note}, null, null, null);
//            cursor.moveToFirst();
//            folder = cursor.getString(cursor.getColumnIndex(DB_COLUMN_FOLDER));
//        }

        values.put(DB_COLUMN_FOLDER, folderNew);
        db.update(DB_TABLE_ALL_NOTES,values, DB_COLUMN_NOTE +"=?",new String[]{note});
        db.close();
    }

    public void restoreNote(String note){
        SQLiteDatabase db= this.getWritableDatabase();//відкритя бд
        ContentValues values = new ContentValues(); //створ класу для занес рядка
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        Cursor cursor = db.query(DB_TABLE_REMOVED_NOTES, null, DB_COLUMN_REMOVED_NOTE + "=?", new String[]{note}, null, null, null);
        cursor.moveToFirst();
        values.put(DB_COLUMN_NOTE,cursor.getString(cursor.getColumnIndex(DB_COLUMN_REMOVED_NOTE)));
        values.put(DB_COLUMN_FOLDER,cursor.getString(cursor.getColumnIndex(DB_COLUMN_REMOVED_FOLDER)));
        values.put(DB_COLUMN_DATE,currentDateTimeString);
        values.put(DB_COLUMN_ISCHECKED,0);

        db.insertWithOnConflict(DB_TABLE_ALL_NOTES,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        db.delete(DB_TABLE_REMOVED_NOTES, DB_COLUMN_REMOVED_NOTE + " = ?",new String[]{note});
        cursor.close();
        db.close();


    }

    public void deleteFromRemoved(String note){ //Delete note from folderName and allNotes table
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(DB_TABLE_REMOVED_NOTES, DB_COLUMN_REMOVED_NOTE + " = ?",new String[]{note});

        db.close();
    }

    public String getFolder(String note){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor=db.query(DB_TABLE_ALL_NOTES,null,DB_COLUMN_NOTE+ "=?",new String[]{note},null,null,null);
        cursor.moveToFirst();
        String folder=cursor.getString(cursor.getColumnIndex(DB_COLUMN_FOLDER));
        cursor.close();
        db.close();

        return folder;
    }

    public void deleteAllNoteInFolder(String useFolder){
        ArrayList<String> notes= getNoteList(useFolder,"");

        SQLiteDatabase db=getWritableDatabase();

        if(!notes.isEmpty()){
            ContentValues values=new ContentValues();
            for(int i=0;i<notes.size();i++){
                values.put(DB_COLUMN_REMOVED_NOTE,notes.get(i));
                values.put(DB_COLUMN_REMOVED_FOLDER,useFolder);
                db.insertWithOnConflict(DB_TABLE_REMOVED_NOTES,null,values,SQLiteDatabase.CONFLICT_REPLACE);
            }

        }

        db.delete(DB_TABLE_ALL_NOTES,DB_COLUMN_FOLDER+"=?",new String[]{useFolder});

        db.close();

    } //Delete all notes in folder

    public void deleteAllNotes(){
        ArrayList<String> notes= getAllNotesList("");

        SQLiteDatabase db=getWritableDatabase();

        if(!notes.isEmpty()){
            ContentValues values=new ContentValues();
            for(int i=0;i<notes.size();i++){
                values.put(DB_COLUMN_REMOVED_NOTE,notes.get(i));
                values.put(DB_COLUMN_REMOVED_FOLDER,DB_TABLE_ALL_NOTES);
                db.insertWithOnConflict(DB_TABLE_REMOVED_NOTES,null,values,SQLiteDatabase.CONFLICT_REPLACE);
            }

        }

        String DELETE_ALL=String.format("DELETE FROM %s",DB_TABLE_ALL_NOTES);
        db.execSQL(DELETE_ALL);
        db.close();

    } //Delete all notes TODO:folder

    public void clearRemovedNotes(){
        SQLiteDatabase db=getWritableDatabase();

        String DELETE_ALL=String.format("DELETE FROM %s",DB_TABLE_REMOVED_NOTES);
        db.execSQL(DELETE_ALL);
        db.close();

    } //Delete all notes from Removed Notes table


    //----Get--List------------

    public ArrayList<String> getAllNotesList(String orderBy){
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;


            cursor = db.query(DB_TABLE_ALL_NOTES,new String[]{DB_COLUMN_NOTE},DB_COLUMN_ISCHECKED+"=?",new String[]{"1"},null,null,null);
            while(cursor.moveToNext()){
                int index = cursor.getColumnIndex(DB_COLUMN_NOTE);
                taskList.add(cursor.getString(index));
            }
            cursor = db.query(DB_TABLE_ALL_NOTES,new String[]{DB_COLUMN_NOTE},DB_COLUMN_ISCHECKED+"=?",new String[]{"0"},null,null,orderBy);


        if(cursor.getCount()!=0){
            cursor.moveToLast();
            do{
                int index = cursor.getColumnIndex(DB_COLUMN_NOTE);
                taskList.add(cursor.getString(index));
            }while(cursor.moveToPrevious());
        }

        cursor.close();
        db.close();
        return taskList;
    }

    public ArrayList<String> getNoteList(String folderName,String orderBy){
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;


            String selectionCheck=String.format("%s LIKE ? AND %s = ?",DB_COLUMN_FOLDER,DB_COLUMN_ISCHECKED);
            cursor = db.query(DB_TABLE_ALL_NOTES,new String[]{DB_COLUMN_NOTE},selectionCheck,new String[]{folderName,"1"},null,null,null);
            while(cursor.moveToNext()){
                int index = cursor.getColumnIndex(DB_COLUMN_NOTE);
                taskList.add(cursor.getString(index));
            }
            String selectionNotCheck=String.format("%s LIKE ? AND %s = ?",DB_COLUMN_FOLDER,DB_COLUMN_ISCHECKED);
            cursor = db.query(DB_TABLE_ALL_NOTES,new String[]{DB_COLUMN_NOTE},selectionCheck,new String[]{folderName,"0"},null,null,orderBy);


        if (cursor.getCount() != 0) {
            cursor.moveToLast();
            do{
                int index = cursor.getColumnIndex(DB_COLUMN_NOTE);
                taskList.add(cursor.getString(index));
            }while(cursor.moveToPrevious());
        }

        cursor.close();
        db.close();
        return taskList;
    }

    public ArrayList<String> getRemovedNotesList(String orderBy){

        ArrayList<String> removedList=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor;
        if(orderBy.equals("")){
            cursor=db.query(DB_TABLE_REMOVED_NOTES,new String[]{DB_COLUMN_REMOVED_NOTE},null,null,null,null,null);
        }else{
            cursor=db.query(DB_TABLE_REMOVED_NOTES,new String[]{DB_COLUMN_REMOVED_NOTE},null,null,null,null,orderBy);
        }

        if(cursor.getCount()!=0){
            cursor.moveToLast();
            do{
                int index = cursor.getColumnIndex(DB_COLUMN_REMOVED_NOTE);
                removedList.add(cursor.getString(index));
            }while(cursor.moveToPrevious());
        }

        cursor.close();
        db.close();
        return removedList;
    }

    public ArrayList<String> getFolderList(String orderBy){

        ArrayList<String> folderList=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor;

        if(orderBy.equals("")){
            cursor=db.query(DB_TABLE_FOLDERS,new String[]{DB_COLUMN_FOLDER_NAME},null,null,null,null,null);
            while(cursor.moveToNext()){
                int index = cursor.getColumnIndex(DB_COLUMN_FOLDER_NAME);
                folderList.add(cursor.getString(index));
            }
        }else {
            folderList.add(DB_TABLE_ALL_NOTES);

            cursor=db.query(DB_TABLE_FOLDERS,new String[]{DB_COLUMN_FOLDER_NAME},null,null,null,null,orderBy);

            while(cursor.moveToNext()){
                int index = cursor.getColumnIndex(DB_COLUMN_FOLDER_NAME);
                if(!cursor.getString(index).equals(DB_TABLE_ALL_NOTES)){
                    folderList.add(cursor.getString(index));
                    Log.e("Folder",cursor.getString(index));
                }

            }
        }

        cursor.close();
        db.close();
        return folderList;
    }

    //-----FOLDER-----------------------------------------------------------------------------------

    public Boolean createFolder(String folderName){
        SQLiteDatabase db=this.getWritableDatabase();

            if(!checkNameFolder(folderName)){
                ContentValues value = new ContentValues();
                value.put(DB_COLUMN_FOLDER_NAME, folderName);
                db.insertWithOnConflict(DB_TABLE_FOLDERS, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                db.close();
                return true;
            }else{
                db.close();
                return false;
            }
    } //Create new folder and check if folder already exist

    public void deleteFolder(String folderName){
        ArrayList<String> notes= getNoteList(folderName,"");

        SQLiteDatabase db=this.getWritableDatabase();

        if(!notes.isEmpty()){
            ContentValues values=new ContentValues();
            for(int i=0;i<notes.size();i++){
                values.put(DB_COLUMN_REMOVED_NOTE,notes.get(i));
                values.put(DB_COLUMN_REMOVED_FOLDER,folderName);
                db.insertWithOnConflict(DB_TABLE_REMOVED_NOTES,null,values,SQLiteDatabase.CONFLICT_REPLACE);
            }
        }

        db.delete(DB_TABLE_ALL_NOTES, DB_COLUMN_FOLDER + " = ?",new String[]{folderName});
        db.delete(DB_TABLE_FOLDERS, DB_COLUMN_FOLDER_NAME + " = ?",new String[]{folderName});

        db.close();

    } //Delete folder and copy notes to RemovedNotes

    public void renameFolder(String folderNameNew,String folderNameOld){
        SQLiteDatabase db = this.getWritableDatabase();

        String newNameFolder=folderNameNew;
        String newNameColumn=newNameFolder+"Note";

        ContentValues values=new ContentValues();
        values.put(DB_COLUMN_FOLDER_NAME,folderNameNew);
        ContentValues value=new ContentValues();
        value.put(DB_COLUMN_FOLDER,folderNameNew);
        db.update(DB_TABLE_FOLDERS,values,DB_COLUMN_FOLDER_NAME+"=?",new String[]{folderNameOld});
        db.update(DB_TABLE_ALL_NOTES,value,DB_COLUMN_FOLDER+"=?",new String[]{folderNameOld});
        //db.delete(DB_TABLE_FOLDERS, DB_COLUMN_FOLDER_NAME + " = ?",new String[]{folderNameOld});
        //createFolder(newNameFolder);
        //replaceFolder(folderNameOld,newNameFolder);

        db.close();
    } //Rename folders

    public void deleteAllFolders(){
        SQLiteDatabase db=getWritableDatabase();
        String selection=String.format("%s NOT LIKE ? AND %s NOT LIKE ?",DB_COLUMN_FOLDER_NAME,DB_COLUMN_FOLDER_NAME);

        Cursor cursor = db.query(DB_TABLE_FOLDERS,null,selection,new String[]{DB_TABLE_REMOVED_NOTES,DB_TABLE_ALL_NOTES},null,null,null);
        while(cursor.moveToNext()){
            Log.e("Drop",cursor.getString(cursor.getColumnIndex(DB_COLUMN_FOLDER_NAME)));
            deleteFolder(cursor.getString(cursor.getColumnIndex(DB_COLUMN_FOLDER_NAME)));
        }
        cursor.close();
        db.close();
    } //Delete all created user folders and cope notes to RemovedNotes


    //--OLD----------------------------
//    public int nowUseTheme(){
//        SQLiteDatabase db=getWritableDatabase();
//        Integer theme;
//
//        Cursor cursor=db.query(DB_TABLE_SETTINGS,new String[]{DB_COLUMN_THEME},null,null,null,null,null);
//        cursor.moveToFirst();
//        int index = cursor.getCount();
//
//        if(index==0){
//            ContentValues value=new ContentValues();
//            value.put(DB_COLUMN_THEME,0);
//            db.insertWithOnConflict(DB_TABLE_SETTINGS,null,value,SQLiteDatabase.CONFLICT_REPLACE);
//            theme=0;
//        }else{
//            theme= cursor.getInt(cursor.getColumnIndex(DB_COLUMN_THEME));
//        }
//        cursor.close();
//        db.close();
//        return theme;
//
//    }
//
//    public void setNowUseTheme(int theme){
//        SQLiteDatabase db=getWritableDatabase();
//        ContentValues value=new ContentValues();
//        value.put(DB_COLUMN_THEME,theme);
//        db.update(DB_TABLE_SETTINGS,value, "ID" +"=?",new String[]{"1"});
//        db.close();
//
//    }

    //    public void replaceFolder(String oldFolder, String newFolder){
//        ArrayList<String> notes= getNoteList(oldFolder);
//
//        SQLiteDatabase db=this.getWritableDatabase();
//
//        if(!notes.isEmpty()){
//            ContentValues values=new ContentValues();
//            for(int i=0;i<notes.size();i++){
//                values.put(newFolder+"Note",notes.get(i));
//                db.insertWithOnConflict(newFolder,null,values,SQLiteDatabase.CONFLICT_REPLACE);
//            }
//
//            String query_deleteFolder = String.format("DROP TABLE IF EXISTS %s", oldFolder);
//            db.execSQL(query_deleteFolder);
//
//        }else{
//            String query_deleteFolder = String.format("DROP TABLE IF EXISTS %s", oldFolder);
//            db.execSQL(query_deleteFolder);
//        }
//
//        db.close();
//
//    } //Replace 2 folders


//    public ArrayList<String> getAllNotes(){
//
//        ArrayList<String> allNotesList = new ArrayList<>();
//        SQLiteDatabase db=this.getReadableDatabase();
//
//        String SQL_GET_ALL_NOTES = String.format("SELECT %s FROM %s WHERE %s NOT LIKE '%s' AND %s NOT LIKE '%s' ;",DB_COLUMN_FOLDER_NAME,DB_TABLE_FOLDERS,DB_COLUMN_FOLDER_NAME,DB_TABLE_ALL_NOTES,DB_COLUMN_FOLDER_NAME,DB_TABLE_REMOVED_NOTES);
//        Cursor cursor=db.rawQuery(SQL_GET_ALL_NOTES,null);
//
//        while(cursor.moveToNext()){
//            int index=cursor.getColumnIndex(DB_COLUMN_FOLDER_NAME);
//            String columnName=cursor.getString(index)+"Note";
//            String query=String.format("SELECT %s FROM %s",columnName,cursor.getString(index));
//            Cursor note=db.rawQuery(query,null);
//            while (note.moveToNext()){
//                int index2=note.getColumnIndex(columnName);
//                allNotesList.add(note.getString(index2));
//            }
//        }
//
//        cursor.close();
//        db.close();
//        return allNotesList;
//    }
}
