package com.example.newsapp;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsAppFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<NewsApp>> {
    private ListView myListView;
    private TextView myEmptyView;
    private ProgressBar myProgressBar;
    private FloatingActionButton myRefreshButton;

    private NewsAppAdapter adapter;
    public final String LOG_TAG = getClass().getName();
    private List<NewsApp> myNews;

    public NewsAppFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        myProgressBar = rootView.findViewById(R.id.progressBar);
        myEmptyView = rootView.findViewById(R.id.empty_view);
        myListView = rootView.findViewById(R.id.list);
        myRefreshButton = rootView.findViewById(R.id.refresh_button);

        myListView.setDividerHeight(0);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        final LoaderManager myLoaderManager = getLoaderManager();

        if (QueryUtils.checkInternetConnection(getActivity())) {
            myLoaderManager.initLoader(Constants.NEWS_LOADER_ID, null, this).forceLoad();
        }
        else {
            myProgressBar.setVisibility(View.GONE);
            myEmptyView.setText(R.string.connection);
        }
        myRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (QueryUtils.checkInternetConnection(getActivity())) {
                    myLoaderManager.restartLoader(Constants.NEWS_LOADER_ID, null, NewsAppFragment.this).forceLoad();
                    myProgressBar.setVisibility(View.VISIBLE);
                    myEmptyView.setText("");
                    myListView.setVisibility(View.INVISIBLE);
                    myRefreshButton.setVisibility(View.INVISIBLE);
                }
                else {
                    myProgressBar.setVisibility(View.GONE);
                    myListView.setVisibility(View.INVISIBLE);
                    myRefreshButton.setVisibility(View.VISIBLE);
                    myEmptyView.setText(R.string.connection);
                }
            }
        });
    }

    @NonNull
    @Override
    public Loader<ArrayList<NewsApp>> onCreateLoader(int id, @Nullable Bundle args) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String minDate = sharedPrefs.getString(
                getString(R.string.min_date),
                getString(R.string.min_default));

        String maxDate = sharedPrefs.getString(
                getString(R.string.max_date),
                getString(R.string.max_default));

        try {
            if (!minDate.equals("")) {
                Date minDateDate = Utility.convertStringToDate(minDate, "dd/MM/yyyy");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                minDate = formatter.format(minDateDate);
            }
        } catch (Exception e) {
            minDate = "";
            Log.e(LOG_TAG, "Error with the minimun publication date");
        }

        try {
            if (!maxDate.equals("")) {
                Date maxDateDate = Utility.convertStringToDate(maxDate, "dd/MM/yyyy");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                maxDate = formatter.format(maxDateDate);
            }
        } catch (Exception e) {
            maxDate = "";
            Log.e(LOG_TAG, "Error with the maximum publication date");
        }
        String orderBy = sharedPrefs.getString(
                getString(R.string.order_by_key),
                getString(R.string.order_by_default)
        );

        String productionOffice = sharedPrefs.getString(
                getString(R.string.production_office_key),
                getString(R.string.production_office_default)
        );

        String section = sharedPrefs.getString(
                getString(R.string.section_key),
                getString(R.string.section_default)
        );

        Uri baseUri = Uri.parse(Constants.GUARDIANAPIURL);

        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("page-size", "50");

        if (!minDate.equals(""))
            uriBuilder.appendQueryParameter("from-date", minDate);
        if (!maxDate.equals(""))
            uriBuilder.appendQueryParameter("to-date", maxDate);

        uriBuilder.appendQueryParameter("order-by", orderBy);

        if (!productionOffice.equals("all"))
            uriBuilder.appendQueryParameter("production-office", productionOffice);

        if (!section.equals("all"))
            uriBuilder.appendQueryParameter("section", section);

        uriBuilder.appendQueryParameter("api-key", Constants.APIKEY);

        Log.e(LOG_TAG, uriBuilder.toString());

        return new NewsAppLoader(getContext(), uriBuilder.toString());
    }


    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<NewsApp>> loader, ArrayList<NewsApp> data) {
        myProgressBar.setVisibility(View.GONE);
        myRefreshButton.setVisibility(View.VISIBLE);
        
        if (myNews == null || myNews.isEmpty()) {
            myEmptyView.setVisibility(View.VISIBLE);
            myEmptyView.setText(R.string.noData);

        } else {
            adapter = new NewsAppAdapter(getActivity(), myNews);
            myListView.setVisibility(View.VISIBLE);
            myListView.setAdapter(adapter);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<NewsApp>> loader) {
        adapter.clear();
    }
}
