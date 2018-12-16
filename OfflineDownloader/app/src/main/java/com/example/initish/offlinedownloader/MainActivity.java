package com.example.initish.offlinedownloader;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.LoginFilter;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    Button button;

    private static final int Constt = 1;
    ArrayList<Bitmap> list=new ArrayList<>();
    ArrayList<String> pageurls = new ArrayList<String>();
    ArrayList<String> f = new ArrayList<String>();// list of file paths
    File[] listFile;
    TextView textView;
    ProgressBar fab;
    CardView cardView;
    File myDir=null;
    int page = 0;

    final String[] name = {""};
    final String[] id = {""};
    final String[] auth_name = {""};
    final String[] auth_inst = {""};

    String root = Environment.getExternalStorageDirectory().toString();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case Constt:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }
                break;
        }
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fab.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            fab.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {

            Bitmap myBitmap=null;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void downloadImage(String url,int page){

        ImageDownloader imageTask = new ImageDownloader();

        try {
            Bitmap image = imageTask.execute(url).get();

            makeFiles(image,myDir,page);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result="";
            URL url;
            HttpURLConnection httpURLConnection=null;

            try {
                url = new URL(urls[0]);

                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data=reader.read();

                while(data != -1){

                    char current = (char)data;
                    result+=current;
                    data = reader.read();
                }
               return result;
            }

            catch (Exception e){
                e.printStackTrace();
                return "Failed";
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            fab.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            fab.setVisibility(View.INVISIBLE);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab=findViewById(R.id.fab);
        cardView = findViewById(R.id.cardView);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.text);


        try {

            SQLiteDatabase sqLiteDatabase=MainActivity.this.openOrCreateDatabase("Paper",MODE_PRIVATE,null);

            Cursor c=sqLiteDatabase.rawQuery("SELECT * FROM papers",null);

            int nameIndex=c.getColumnIndex("name");
            final int idIndex=c.getColumnIndex("id");
            int authnameIndex=c.getColumnIndex("authorname");
            int authinstIndex=c.getColumnIndex("authorinst");

            c.moveToFirst();
            while(c!=null){

                final String id=c.getString(idIndex);
                cardView.setVisibility(View.VISIBLE);
                textView.setText(c.getString(nameIndex));
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this,Notes.class);
                        intent.putExtra("dirname",id);
                        startActivity(intent);
                    }
                });
                c.moveToNext();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

//        task();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                task();

//                fab.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Downloaded", Toast.LENGTH_SHORT).show();

                cardView.setVisibility(View.VISIBLE);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this,Notes.class);
                        intent.putExtra("dirname",id[0]);
                        startActivity(intent);
                    }
                });
                }
        });


        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                SQLiteDatabase sqLiteDatabase=MainActivity.this.openOrCreateDatabase("Paper",MODE_PRIVATE,null);
                sqLiteDatabase.execSQL("DELETE FROM papers WHERE name = '"+textView.getText()+"'");

                System.out.print("here it is "+sqLiteDatabase.rawQuery("SELECT COUNT('name') FROM papers",null));

                cardView.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Note deleted.", Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                return false;
            }
        });
    }

    public void task(){

        DownloadTask task = new DownloadTask();

        String result="";
        try {
            result = task.execute("https://raw.githubusercontent.com/initishh/WebDev-Projects/master/file.json").get();

            JSONObject jsonObj = new JSONObject(result);

            name[0] =jsonObj.getString("name");
            id[0] =jsonObj.getString("materialId");
            auth_name[0] = jsonObj.getJSONObject("author").getString("name");
            auth_inst[0] = jsonObj.getJSONObject("author").getString("institute");
            System.out.println("Name of the file is "+name[0]);
            textView.setText(name[0]);
            System.out.println("The file ID is "+id[0]);

            try {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constt);
                    createDirectoryAndSaveFile(id[0]);
                } else {
                    createDirectoryAndSaveFile(id[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Author name is "+ auth_name[0]);
            System.out.println("Author Institute is "+ auth_inst[0]);
            JSONArray pages = jsonObj.getJSONArray("pages");
            for(int j=0;j<pages.length();j++){
                JSONObject pageObj = pages.getJSONObject(j);
                System.out.println("Page No. "+pageObj.getString("page"));
                System.out.println("URL of the image file "+pageObj.getString("url"));
                pageurls.add(pageObj.getString("url"));
            }

            //=================================================================
            //          SAVING TO THE SQL DATABASE

            try{

                SQLiteDatabase sqLiteDatabase=MainActivity.this.openOrCreateDatabase("Paper",MODE_PRIVATE,null);

                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS papers");

                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS papers (name VARCHAR ,id VARCHAR PRIMARY KEY, authorname VARCHAR,authorinst VARCHAR)");

                sqLiteDatabase.execSQL("INSERT INTO papers (name,id,authorname,authorinst) VALUES ('"+name[0]+"','"+id[0]+"','"+auth_name[0]+"','"+auth_inst[0]+"')");

                Cursor c=sqLiteDatabase.rawQuery("SELECT * FROM papers",null);

                int nameIndex=c.getColumnIndex("name");
                int idIndex=c.getColumnIndex("id");
                int authnameIndex=c.getColumnIndex("authorname");
                int authinstIndex=c.getColumnIndex("authorinst");

                c.moveToFirst();
                while(c!=null){
                    System.out.println("mother "+c.getString(nameIndex));
                    System.out.println("mother "+c.getString(idIndex));
                    System.out.println("mother "+c.getString(authnameIndex));
                    System.out.println("mother "+c.getString(authinstIndex));
                    c.moveToNext();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i=0;i<pageurls.size();i++){

            downloadImage(pageurls.get(i),i);
        }
    }

    public void makeFiles(Bitmap imageToSave, File myDir, int page_num){

        String fname = "Image-"+Integer.toString(page_num)+".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDirectoryAndSaveFile(String dir_name) {

        myDir = new File(root + "/"+dir_name);
//        File myDir = getPrivateAlbumStorageDir(MainActivity.this,"Images");
        myDir.mkdirs();
        return;
    }

}
