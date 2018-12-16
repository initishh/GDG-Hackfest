package com.example.initish.offlinedownloader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.util.ArrayList;

public class Notes extends AppCompatActivity {

    RecyclerView recyclerView;
    NotesAdapter adapter;
    EditText pgnum;
    Button submit;
    ArrayList<Bitmap> list=new ArrayList<>();
    ArrayList<String> f = new ArrayList<String>();// list of file paths
    File[] listFile;
    int page = 0;
    File myDir=null;
    String root = Environment.getExternalStorageDirectory().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        recyclerView=findViewById(R.id.rcv);
        pgnum = findViewById(R.id.pgnum);
        submit = findViewById(R.id.submit);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotesAdapter(Notes.this,list);
        recyclerView.setAdapter(adapter);

        String dir_name = getIntent().getStringExtra("dirname");

        createDirectoryAndSaveFile(dir_name);
        readFiles();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pgnum.getText().toString().isEmpty()&& Integer.valueOf(pgnum.getText().toString())>0)
                    recyclerView.smoothScrollToPosition(Integer.valueOf(pgnum.getText().toString())-1);
                pgnum.setText(null);
                hideSoftKeyboard(Notes.this, view); // MainActivity is the name of the class and v is the View parameter used in the button listener method onClick.
            }
        });

    }

    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }



    private void createDirectoryAndSaveFile(String dir_name) {

//        myDir = new File(root + "/"+dir_name);
        myDir = getPrivateAlbumStorageDir(getApplicationContext(),"Images/"+dir_name);
        myDir.mkdirs();
        return;
    }

    public File getPrivateAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("fdsf", "Directory not created");
        }
        return file;
    }

    private void readFiles() {
        if (myDir.isDirectory())
        {
            listFile = myDir.listFiles();
            System.out.print("length is : "+listFile.length);
            for (int i = 0; i < listFile.length; i++){

                f.add(listFile[i].getAbsolutePath());
                Bitmap myBitmap = BitmapFactory.decodeFile(f.get(i));
                list.add(myBitmap);
//                img.setImageBitmap(list.get(i));
            }
            adapter.notifyDataSetChanged();
//            img.setImageBitmap(list.get(0));
        }

    }
}
