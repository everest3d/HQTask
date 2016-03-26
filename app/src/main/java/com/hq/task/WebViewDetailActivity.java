package com.hq.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * An activity representing a single WebView detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link WebViewListActivity}.
 */
public class WebViewDetailActivity extends AppCompatActivity
        implements RequestsHandler.RequestsHandlerListener {

    private WebView mWebView = null;
    private ProgressBar mProgressLoad = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_detail);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
       // setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(WebViewDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(WebViewDetailFragment.ARG_ITEM_ID));
            WebViewDetailFragment fragment = new WebViewDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.webview_detail_container, fragment)
                    .commit();
        }

        // Setting title bar
        String title = getIntent().getStringExtra(Constants.KEY_TITLE);
        setTitle(title);

        // Retrieve the progress bar
        mProgressLoad = (ProgressBar)findViewById(R.id.progressBarLoadPage);

        // Retrieve the web view
        mWebView = (WebView)findViewById(R.id.webView);
        mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                mProgressLoad.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onPause() {
        // Unregister on pause
        if (getIntent().getBooleanExtra(Constants.KEY_CACHABLE, false))
            RequestsHandler.GetInstance(this).RemoveFromWaitList(this);

        // Hiding the progress bar anyway
        mProgressLoad.setVisibility(View.GONE);

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Show the progress bar while loading from cache
        mProgressLoad.setVisibility(View.VISIBLE);

        // Register on resume / start
        if (getIntent().getBooleanExtra(Constants.KEY_CACHABLE, false))
        {
            // Getting tag param
            String tag = getIntent().getStringExtra(Constants.KEY_TAG);

            // Register request
            RequestsHandler.GetInstance(this).RequestPageFromCache(tag, this);
        }
        else
        {
            // Getting URL param
            String url = getIntent().getStringExtra(Constants.KEY_URL);

            // Simply load page from URL
            mWebView.loadUrl(url);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, WebViewListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnResult(String data) {
        // Load web view from contents
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadData(data, "text/html", "UTF-8");

        // Hide the progress bar
        mProgressLoad.setVisibility(View.GONE);
    }

    @Override
    public void OnError(String msg) {
        //TODO: ...
    }
}
