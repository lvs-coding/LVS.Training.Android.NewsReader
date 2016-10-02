package com.lvsandroid.newsreader;

import java.net.URL;

/**
 * Created by Laurent on 10/1/2016.
 */

public class News {
    int mId;
    String mTitle;
    String mUrl;
    int mTime;

    public News(int id, int time, String title, String url) {
        mId = id;
        mTime = time;
        mTitle = title;
        mUrl = url;
    }

    @Override
    public String toString() {
        return this.mTitle;
    }
}
