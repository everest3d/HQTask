package com.hq.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Simple adapter for Web Views list
 */
public class WebViewsListAdapter extends ArrayAdapter<WebViewDesc> {

    public WebViewsListAdapter(Context context, List<WebViewDesc> webViews) {
        super(context, 0, webViews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WebViewDesc item = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.webview_list_content, parent, false);

        TextView txtTitle = (TextView) convertView.findViewById(R.id.content);
        txtTitle.setText(item.GetKey());

        return convertView;
    }
}
