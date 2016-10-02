package com.lvsandroid.newsreader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NewsActivity extends AppCompatActivity {
    int newsId = -1;
    SQLiteDatabase dbNews;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //saveNote();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        newsId = i.getIntExtra("newsId",-1);

        if(newsId != -1) {
            dbNews = this.openOrCreateDatabase("newsreader",MODE_PRIVATE,null);
            String query = "SELECT newsId, url FROM news WHERE newsId = " + newsId;

            Cursor c = dbNews.rawQuery(query,null);
            int urlIdx = c.getColumnIndex("url");

            c.moveToFirst();
           if(c != null) {
               String url = c.getString(urlIdx);
               WebView webView = (WebView)findViewById(R.id.newsWebView);
               webView.setWebViewClient(new WebViewClient());
               webView.getSettings().setJavaScriptEnabled(true);

               webView.loadUrl(url);

            }



            Log.i("XX",String.valueOf(newsId));

        }


    }

}
