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
    JSONArray lastNewsIds;
    private final String lastNewsUrl = "https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty";
    private final String newsUrl = "https://hacker-news.firebaseio.com/v0/item/";
    private LastNewsIdsTask lastNewsIdsTask;
    private JSONObject lastNews;
    private LastNewsTask lastNewsTask;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lastNewsIdsTask = new LastNewsIdsTask();
        lastNewsTask = new LastNewsTask();
        try {
            lastNewsIds = lastNewsIdsTask.execute(lastNewsUrl).get();
            lastNews = lastNewsTask.execute(lastNewsIds).get();

            if(lastNewsIds != null && lastNewsIds.length() > 0) {
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    private JSONObject getNews(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        JSONObject json = null;
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            json = new JSONObject(jsonText);
        }
        catch(Exception e) {
            Log.i("XX",e.getStackTrace().toString());
        } finally {
            is.close();
            return json;
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

    // Asynchronous task to get  last news
    private class LastNewsTask extends AsyncTask<JSONArray, Integer, JSONObject> {
        @Override
        protected JSONObject doInBackground(JSONArray... ids) {
            JSONObject lastNews = null;
            try {

                for(int i = 0 ; i <= lastNewsIds.length() ; i++){
                    String currentNewsId = "";
                    currentNewsId = lastNewsIds.getString(i);
                    lastNews = getNews(newsUrl + currentNewsId + ".json");
                    Log.i("XX",lastNews.getString("title").toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return lastNews;
        }
    }

    // Asynchronous task to get Ids of last news
    private class LastNewsIdsTask extends AsyncTask<String, Integer, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... strings) {
            JSONArray lastNewsIds = null;
            try {
                lastNewsIds = getLastNewsIds(lastNewsUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return lastNewsIds;
        }
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

}