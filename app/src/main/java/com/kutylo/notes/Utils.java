package com.kutylo.notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class Utils
{
    public Activity activity;

    private ImageButton btn;
    private ImageView psnel__btn;
    private TextView title_panel_btn;

    public static int sTheme;
    public final static int THEME_WHITE = 0;
    public final static int THEME_DARK = 1;

    public Utils(Activity _activity){
        this.activity = _activity;
    }

    //-----THEME----------------------------------------------------------------------------------//

    public static void changeToTheme(Activity activity, int theme)
    {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    public static void onActivityCreateSetTheme(Activity activity)
    {
        switch (sTheme)
        {
            default:
            case THEME_WHITE:
                activity.setTheme(R.style.WhiteTheme);
                break;
            case THEME_DARK:
                activity.setTheme(R.style.DarkTheme);
                break;
        }
    }

    public void changeThemeComponents(){
//        if(sTheme==1) {
            btn = (ImageButton) this.activity.findViewById(R.id.btnUpdate);
            btn.setColorFilter(Color.argb(255, 255, 255, 255));
            //btn = (ImageButton) this.activity.findViewById(R.id.btnUnCheck);
            btn.setColorFilter(Color.argb(255, 255, 255, 255));

            psnel__btn=(ImageView) this.activity.findViewById(R.id.image_edit);
            title_panel_btn=(TextView)this.activity.findViewById(R.id.title_image_edit);
            psnel__btn.setColorFilter(Color.argb(255, 255, 255, 255));
            title_panel_btn.setTextColor(Color.WHITE);

//        }else {
//            btn = (ImageButton) this.activity.findViewById(R.id.btnUpdate);
//            btn.setColorFilter(Color.argb(0, 0, 0, 0));
//            btn = (ImageButton) this.activity.findViewById(R.id.btnUnCheck);
//            btn.setColorFilter(Color.argb(0, 0, 0, 0));
//        }
    }


    //--------LIST--------------------------------------------------------------------------------//
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    //-----TASK-----------------------------------------------------------------------------------//
    public static void changeColor(final View view,final Context contex){
        AlertDialog.Builder builder = new AlertDialog.Builder(contex);
        View parent = (View) view.getParent();
        final TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String[] color=contex.getResources().getStringArray(R.array.color_text);

        builder.setTitle("Choose color your task")
                .setSingleChoiceItems(color, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case 0:
                                            taskTextView.setTextColor(Color.BLACK);
                                            break;
                                        case 1:
                                            taskTextView.setTextColor(Color.WHITE);
                                            break;
                                        case 2:
                                            taskTextView.setTextColor(Color.RED);
                                            break;
                                        case 3:
                                            taskTextView.setTextColor(Color.GREEN);
                                            break;
                                        case 4:
                                            taskTextView.setTextColor(Color.BLUE);
                                            break;
                                    }

                            }

                        })
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        taskTextView.setTextColor(Color.BLACK);
                    }
                })
                .create();
        builder.show();
    }

    public static void changeStyle(final View view,final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View parent = (View) view.getParent();
        final TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String[] style=context.getResources().getStringArray(R.array.style_text);

        builder.setTitle("Choose style your task")
                .setSingleChoiceItems(style, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case 0:
                                            taskTextView.setTypeface(Typeface.DEFAULT_BOLD);
                                            break;
                                        case 1:
                                            taskTextView.setTypeface(Typeface.MONOSPACE);
                                            break;

                                    }
                            }

                        })
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        taskTextView.setTypeface(Typeface.MONOSPACE);
                    }
                })
                .create();
        builder.show();
    }


    //--------IMAGE--------------------------------------------------------------------------------//
    public static File saveImage(Context contex,Bitmap finalBitmap) {

        File file = new File(contex.getExternalFilesDir(null), "task.jpeg");

        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static void send(Context context,File myFile) {
        try {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("image/jpg");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(myFile));
            context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap getBitmapFromView(View view) {

        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight() , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

}



