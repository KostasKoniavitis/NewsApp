package com.example.newsapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;

public final class QueryUtils {

    private QueryUtils() {
    }

    public static ArrayList<NewsApp> extractNewsApp(String myNews) {

        ArrayList<NewsApp> newsApps = new ArrayList<>();


        try {
            JSONObject baseJsonResponse = new JSONObject(myNews);
            JSONObject newsAppObject = baseJsonResponse.getJSONObject("response");
            JSONArray newsAppArray = newsAppObject.getJSONArray("results");

            for (int i = 0; i < newsAppArray.length(); i++) {
                JSONObject currentNews = newsAppArray.getJSONObject(i);
                String name = currentNews.optString("sectionName");
                String title = currentNews.optString("webTitle");
                String date = currentNews.optString("webPublicationDate").substring(0, 10);
                Date mdate = Utility.convertStringToDate(date, "yyyy-MM-dd");
                String url = currentNews.optString("webUrl");


                NewsApp newsApp = new NewsApp(name, title, mdate, url);
                newsApps.add(newsApp);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the newsApp JSON results", e);
        }

        // Return the list of earthquakes
        return newsApps;
    }

    public static URL createUrl(String stringUrl) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e("Error", "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        int ResponseCode;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            ResponseCode = urlConnection.getResponseCode();
            if (ResponseCode == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                jsonResponse = "";
                Log.e("Error", "Wrong Connection result" + String.valueOf(ResponseCode));
            }

        } catch (IOException e) {
            Log.e("Error", "IOException HTTPrequest" + e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }


    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
