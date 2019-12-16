package com.bhavaniprasad.downloadmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button download;
    List smartagentlist;
    private String filename;
    private String id,name,type,path;
    private int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        download = (Button) findViewById(R.id.Download);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smartagentlist = new ArrayList<>();
                Uri uri = Uri.parse("content://com.bhavaniprasad.smartagent/dependencies4");
                String aa=uri.getAuthority();
                ContentResolver Resolver =getContentResolver();
                Cursor cursor = Resolver.query(uri,null,null,null,null);
                if (cursor.moveToFirst()) {
                    do {
                         id =  cursor.getString(0);
                         name =  cursor.getString(1);
                         type =  cursor.getString(2);
                         size =  cursor.getInt(3);
                         path =  cursor.getString(4);
                        if(!fileExist(name)){
                            new DownloadFileFromURL().execute(path);
                            Toast.makeText(getApplicationContext(),"File Downloaded",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Already Downloaded",Toast.LENGTH_SHORT).show();
                        }
                    } while (cursor.moveToNext());

                }
            }
        });
    }


    public boolean fileExist(String fname){
        File file = new File(Environment.getExternalStorageDirectory(),fname);
        boolean exist= file.exists();
        return exist;
    }


    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);

                Uri uri = Uri.parse(f_url[0]);
//                String protocol = uri.getScheme();
//                String server = uri.getAuthority();
                String path = uri.getPath();
                File theFile = new File(path);
                String[] filewithtype=theFile.getName().split("\\.");
                String filename=filewithtype[0];

                String filetyp=filewithtype[1];
                URLConnection conection = url.openConnection();
                conection.connect();
                File mediaStorageDir,mediaStorageDirp;

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);


                if(filetyp.equals("mp4")){
                    mediaStorageDir = new File(Environment.getExternalStorageDirectory(), filename);
                    mediaStorageDirp = new File(Environment.getExternalStorageDirectory().toString()+"/"+filename.concat(".mp4"));
                }
                else{
                    mediaStorageDir = new File(Environment.getExternalStorageDirectory(), filename);
                    mediaStorageDirp = new File(Environment.getExternalStorageDirectory().toString()+"/"+filename.concat(".svg"));
                }

                // Output stream
                OutputStream output = new FileOutputStream(mediaStorageDirp);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }
    }

}
