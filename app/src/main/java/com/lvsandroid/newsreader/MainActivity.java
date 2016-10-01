package com.lvsandroid.newsreader;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    String[] lastNewsIds;
    private final String lastNewsUrl = "https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty";
    private final String newsUrl = "https://hacker-news.firebaseio.com/v0/item/{newsId}.json?print=pretty";
    private LastNewsIdsTask lastNewsIdsTask;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lastNewsIdsTask = new LastNewsIdsTask();
        try {
            lastNewsIds = lastNewsIdsTask.execute(lastNewsUrl).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class LastNewsIdsTask extends AsyncTask<String, Integer, String[]> {
        private String[] lastNewsIds;
        @Override
        protected String[] doInBackground(String... strings) {
            JSONArray json;
            String[] lastNewsIds = null;
            try {
                json = getLastNewsIds(lastNewsUrl);
                lastNewsIds = json.toString().split(",");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return lastNewsIds;
        }
    }

    private JSONArray getLastNewsIds(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONArray json = new JSONArray(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

//    private JSONObject getLastNewsDetails(String url) throws IOException, JSONException {
//        InputStream is = new URL(url).openStream();
//        try {
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//            String jsonText = readAll(rd);
//            JSONObject json = new JSONObject(jsonText);
//            return json;
//        } finally {
//            is.close();
//        }
//    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

}
