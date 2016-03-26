package com.hq.task;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Web Views web service request class with the following tasks
 * 1- Load available Web View items from a web URL as JSON
 * 2- Parse returned JSON data and create an ArrayList of WebViewDesc class
 * 3- Provide Update functionality to reload all Web Views
 *
 * Usage:
 *  1- Implement the WebViewsRequestListener interface
 *  2- Use OnResult() for results
 *  3- Use OnError() to handle errors
 */
public class WebViewsRequest implements Response.Listener<JSONObject>, Response.ErrorListener {


    // Listener interface for results or errors from Web Views web service
    public interface WebViewsRequestListener
    {
        // List of result web views
        void OnResult(ArrayList<WebViewDesc> result);

        // Error result
        void OnError(String msg);

    } // WebViewsRequestListener

    // Construct new WebViewsReqest
    // Don't forget to call Update() to start getting data
    //
    // @param: url - Web Service URL, should return a json data
    // @param: listener - Results listener
    // @param: context - Holder context to run connections
    public WebViewsRequest(String url, WebViewsRequestListener listener, Context context)
    {
        mUrl = url;
        mListener = listener;
        mContext = context;
    }

    // Retrieve the data from the web service
    public void Update()
    {
        // Create json request
        JsonObjectRequest jsonObject = new JsonObjectRequest(Method.GET, mUrl, null, this, this);

        // Create a queue to hold our request
        RequestQueue queue = Volley.newRequestQueue(mContext);

        // Start getting data
        queue.add(jsonObject);

      // RequestsHandler.GetInstance(mContext).AddRequest(jsonObject);
    }

    // Parse result and create the web views list
    private void ParseResult(JSONObject serverResult)
    {
        try {

            // Create web view list, and init it with result capacity
            ArrayList<WebViewDesc> webViewList = new ArrayList<WebViewDesc>(serverResult.length());

            Iterator<?> resultKeys = serverResult.keys();

            // Iterate over the array and fill a new result list of WebViewDesc
            while(resultKeys.hasNext())
            {
                // Retrieve current object by it's key
                String key = (String)resultKeys.next();

                // Validate current key's object
                if ( serverResult.get(key) instanceof JSONObject ) {
                    JSONObject webViewItem = serverResult.getJSONObject(key);

                    // Create the web view desc
                    WebViewDesc wvDesc = new WebViewDesc(
                            key,                                                            // Key
                            FixUrl(webViewItem.getString(Constants.JSON_URL)),              // URL
                            webViewItem.getString(Constants.JSON_FILE_PATH),                // File path
                            webViewItem.getString(Constants.JSON_NAMESPACE),                // Namespace
                            webViewItem.getBoolean(Constants.JSON_CACHE),                   // Is cached
                            ParseParams(webViewItem.getJSONArray(Constants.JSON_PARAMS)),   // web view Params
                            parsePageTitle(webViewItem),                                    // Page title (optional)
                            ParseTemplateUpdate(webViewItem.getJSONObject(                  // Template last update (object)
                                    Constants.JSON_TEMPLATE_UPDATE)));                      // ...

                    // Add web view desc to our result list
                    webViewList.add(wvDesc);

                    // Handle caching, if enabled
                    if (wvDesc.IsCache())
                        PushCacheRequest(wvDesc);
                }

            }

            // Raise onResult on listener
            if (mListener != null)
                mListener.OnResult(webViewList);

        }
        catch (JSONException je)
        {
            // Just raise listener's onError
            if (mListener != null)
                mListener.OnError(je.getMessage());
        }
    }

    // Parsing Web View url params
    private HashMap<String, String> ParseParams(JSONArray params)
    {
        // TODO: We will return the mentioned params by default

        HashMap<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("userId", "276");
        urlParams.put("appSecretKey", "gvx32RFZLNGhmzYrfDCkb9jypTPa8Q");
        urlParams.put("currencyCode", "USD");
        urlParams.put("offerId", "10736598");
        urlParams.put("selectedVouchers", "");

        return urlParams;
    }

    // Parsing pageTitle as optional key
    private String parsePageTitle(JSONObject jsonObject)
    {
        try {
            return jsonObject.has(Constants.JSON_PAGE_TITLE)?
                    jsonObject.getString(Constants.JSON_PAGE_TITLE) : null;
        } catch (JSONException e) {
            //TODO:
            return null;
        }
    }

    // Parsing template last update object
    //
    // @param: jsonTUObject - JsonOjbect for template last update
    private TemplateLastUpdate ParseTemplateUpdate(JSONObject jsonTUObject)
    {
        try {
            return new TemplateLastUpdate(
                    jsonTUObject.getInt(Constants.JSON_TEMPLATE_UPDATE_STAMP),          // Unix timestamp
                    jsonTUObject.getString(Constants.JSON_TEMPLATE_UPDATE_DATATIME));   // Datatime string
        } catch (JSONException e) {
            //TODO:
            return new TemplateLastUpdate(0, "");
        }
    }

    // We do this because we know that urls params are constants
    private String FixUrl(String oldUrl)
    {
        // Replacing userid because it doesn't allow URI parse
        oldUrl = oldUrl.replace("{offerId}", Constants.VALUE_ORDER_ID);
        oldUrl = oldUrl.replace("{userId}", Constants.VALUE_USER_ID);
        oldUrl = oldUrl.replace("{appSecretKey}", Constants.VALUE_APP_ID);
        oldUrl = oldUrl.replace("{currencyCode}", Constants.VALUE_CURRENCY);
        oldUrl = oldUrl.replace("{selectedVouchers}", Constants.VALUE_SELECTED_VOUCHERS);

        return oldUrl;
    }

    private void PushCacheRequest(WebViewDesc desc)
    {
        RequestsHandler.GetInstance(mContext).AddRequest(desc.GetUrl(), desc.GetKey());
    }

    public void onErrorResponse(VolleyError error) {
        if (mListener != null)
            mListener.OnError(error.getMessage());
    }

    // Server results listener
    @Override
    public void onResponse(JSONObject response) {
        // Check if result is OK

        RequestsHandler rh = RequestsHandler.GetInstance(mContext);

        if (response == null)
        {
            // Raise error on listener
            if (mListener != null)
             mListener.OnError("Server response error!");

            return;
        }

        ParseResult(response);
    }

    // JSON service request url
    private String mUrl;

    // Listener to service results
    private WebViewsRequestListener mListener;

    // Context to run connections
    private Context mContext;
}
