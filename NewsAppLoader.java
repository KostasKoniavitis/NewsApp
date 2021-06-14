package com.example.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class NewsAppLoader extends AsyncTaskLoader<ArrayList<NewsApp>> {
    private static final String LOG_TAG = NewsAppLoader.class.getName();
    private String myURL;
    public NewsAppLoader(Context context, String url) {
        super(context);
        myURL = url;
    }

    @Override
    public ArrayList<NewsApp> loadInBackground() {

        String myNews = null;
        ArrayList<NewsApp> myNewsArrayList;

        if ((myURL == null) || myURL.equals("")) {
            Log.e(LOG_TAG, "Received null or empty url");
            return null;
        }

        URL GuardianURL = QueryUtils.createUrl(myURL);

        if (GuardianURL == null) {
            return null;
        }

        try {
            myNews = QueryUtils.makeHttpRequest(GuardianURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        myNewsArrayList = QueryUtils.extractNewsApp(myNews);

        return myNewsArrayList;
    }
}
