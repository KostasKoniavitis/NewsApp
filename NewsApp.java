package com.example.newsapp;

import java.util.Date;

public class NewsApp {
    private String mSectionName;
    private String mTitle;
    private static Date mDate;
    private String mUrl;


    public NewsApp(String sectionName, String title, Date date, String url){
        mSectionName = sectionName;
        mTitle = title;
        mDate = date;
        mUrl = url;

    }

    public String getmSectionName() {
        return mSectionName;
    }

    public String getmTitle() {
        return mTitle;
    }

    public static Date getmDate() {
        return mDate;
    }

    public String getmUrl() {
        return mUrl;
    }

}
