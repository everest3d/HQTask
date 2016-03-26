package com.hq.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
/**
 * An activity representing a list of WebViews. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link WebViewDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class WebViewListActivity extends AppCompatActivity implements WebViewsRequest.WebViewsRequestListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    // List of web views items
    private ListView listWebViews = null;

    // Adapter for the web views list
    private WebViewsListAdapter webViewsAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.webview_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        // Setup Web views list
        listWebViews = (ListView)findViewById(R.id.listWebViews);
        listWebViews.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // Open web view activity
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Retrieve selected model web view item
                WebViewDesc desc = (WebViewDesc)parent.getAdapter().getItem(position);

                // Fill new intent for web view activity
                Intent i = new Intent(WebViewListActivity.this, WebViewDetailActivity.class);
                i.putExtra(Constants.KEY_URL, desc.GetUrl());
                i.putExtra(Constants.KEY_TAG, desc.GetKey());
                i.putExtra(Constants.KEY_TITLE, desc.GetPageTitle());
                i.putExtra(Constants.KEY_CACHABLE, desc.IsCache());
                // TODO: Rest of required params
                // ...

                // Start the activity
                WebViewListActivity.this.startActivity(i);
            }

        });

        WebViewsRequest request = new WebViewsRequest(Constants.SERVICE_URL, this, this);
        request.Update();
    }

    // On result handler
    @Override
    public void OnResult(ArrayList<WebViewDesc> result) {
        webViewsAdapter = new WebViewsListAdapter(this, result);
        listWebViews.setAdapter(webViewsAdapter);
    }

    @Override
    public void OnError(String message) {
        // Just display error message
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


}
