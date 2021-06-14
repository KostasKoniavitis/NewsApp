package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.List;

public class NewsAppAdapter extends ArrayAdapter<NewsApp> {
    public NewsAppAdapter(@NonNull Context context, List<NewsApp> newsApp) {
        super(context, 0, newsApp);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.newsapp_list_item, parent, false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the list
        NewsApp currentNewsApp = getItem(position);


        TextView sectionNameTextView = (TextView) listItemView.findViewById(R.id.sectionName);

        sectionNameTextView.setText(currentNewsApp.getmSectionName());


        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title);

        titleTextView.setText(currentNewsApp.getmTitle());


        // Find the TextView with view ID date
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String myDate = formatter.format(NewsApp.getmDate());
        dateView.setText(myDate);

        if (!currentNewsApp.getmUrl().equals(" ")) {
            listItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW);
                    Uri webpage = Uri.parse((String) currentNewsApp.getmUrl());
                    webIntent.setData(webpage);
                    PackageManager packageManager = getContext().getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(webIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    boolean isIntentSafe = activities.size() > 0;

                    // If at least a proper App exists, the Activity is started
                    if (isIntentSafe) {
                        getContext().startActivity(webIntent);
                    }
                }

            });
        }
        return listItemView;
    }
}
