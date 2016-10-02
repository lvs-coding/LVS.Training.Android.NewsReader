package com.lvsandroid.newsreader;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    private ArrayList<News> lastNewsList;
    static ArrayAdapter<News> myAdapter;
    SQLiteDatabase dbNews;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lastNewsIdsTask = new LastNewsIdsTask();
        lastNewsTask = new LastNewsTask();
        try {
            ListView listView;
            listView = (ListView)findViewById(R.id.lstNews);
            lastNewsList = new ArrayList<>();
            myAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,lastNewsList);
            listView.setAdapter(myAdapter);
            dbNews = this.openOrCreateDatabase("newsreader",MODE_PRIVATE,null);
            dbNews.execSQL("CREATE TABLE IF NOT EXISTS news (id INTEGER PRIMARY KEY,newsId INT(10),time INT(12), title VARCHAR, url VARCHAR, UNIQUE(newsId))");

            //listView.setOnItemClickListener();

            lastNewsIds = lastNewsIdsTask.execute(lastNewsUrl).get();
            lastNews = lastNewsTask.execute(lastNewsIds).get();


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

                if(lastNewsIds != null && lastNewsIds.length() > 0) {

                    for (int i = 0; i <= lastNewsIds.length(); i++) {
                        String currentNewsId = "";
                        currentNewsId = lastNewsIds.getString(i);
                        lastNews = getNews(newsUrl + currentNewsId + ".json");
                        int newsId = Integer.valueOf(lastNews.getString("id").toString());
                        int newsTime = Integer.valueOf(lastNews.getString("time").toString());
                        String newsTitle = lastNews.getString("title");
                        String newsUrl = lastNews.getString("url");
                        lastNewsList.add(new News(newsId,newsTime,newsTitle,newsUrl));
                        Log.i("News",newsTitle);
                        String query = "INSERT OR IGNORE INTO news (newsId, time, title, url) VALUES (" + newsId + "," + newsTime
                                + ",'" + newsTitle.replace("'"," ") + "','" + newsUrl + "')";
                        Log.i("News",query);
                        dbNews.execSQL(query);
                    }
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