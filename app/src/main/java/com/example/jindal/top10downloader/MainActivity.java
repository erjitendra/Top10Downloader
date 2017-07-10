package com.example.jindal.top10downloader;

import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
private Button btnparse;
    private ListView listapps;
    private  String mFileContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnparse=(Button)findViewById(R.id.btnparse) ;
        btnparse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Add Parse Activation Code
                parseApplications parseApplications=new parseApplications(mFileContents);
                parseApplications.process();
                ArrayAdapter<Application>arrayAdapter=new ArrayAdapter<Application>(
                        MainActivity.this,R.layout.list_item,parseApplications.getApplications());
                listapps.setAdapter(arrayAdapter);
            }
        });
        listapps=(ListView) findViewById(R.id.xmlListView) ;

        DownloadData downloadData=new DownloadData();
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");
    }
    private class DownloadData extends AsyncTask<String,Void,String>
    {


        @Override
        protected String doInBackground(String... params) {
            mFileContents= downloadXMLFile(params[0]);
            if(mFileContents==null)
            {
                Log.d("Download Data","Error Downloading");
            }
            return mFileContents;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("Download Data","Result Was"+result);

        }

        private String downloadXMLFile(String urlPath)
        {
StringBuilder tempBuffer=new StringBuilder();
            try
            {
                URL url=new URL(urlPath);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                int response=connection.getResponseCode();
                Log.d("Download Data","The Response Code was"+response);
                InputStream is=connection.getInputStream();
                InputStreamReader isr=new InputStreamReader(is);
                int charRead;
                char[] inputBuffer=new char[500];
                while(true)
                {
                    charRead=isr.read(inputBuffer);
                    if(charRead<=0)
                    {
                        break;
                    }
                    tempBuffer.append(String.copyValueOf(inputBuffer,0,charRead));
                }
                return tempBuffer.toString();
            }
            catch(IOException e)
            {
                Log.d("Download Data","IO Exception Reading Data"+e.getMessage());
                //e.printStackTrace();

            }
            catch (SecurityException e)
            {
                Log.d("Download Data","Security Exception. Needs Permissions?"+e.getMessage());
            }
            return null;
        }

    }
}
