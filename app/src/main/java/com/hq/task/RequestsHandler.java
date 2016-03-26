package com.hq.task;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;

/**
 * Created by Amr on 3/26/2016.
 */
public class RequestsHandler {

    // Listener for offline viewers to enter the wait list
    public interface RequestsHandlerListener
    {
        // List of result web views
        void OnResult(String data);

        // Error result
        void OnError(String msg);

    } // RequestsHandlerListener

    // Singleton instance
    private static RequestsHandler mInstance;

    // Local request queue object
    private RequestQueue mRequestQueue;

    // Caller context
    private static Context mContext;

    // Tag, Page contents map
    private HashMap<String, String> mCachesData = null;

    private HashMap<String, RequestsHandlerListener> mWaitList = null;

    // Private constructor
    private RequestsHandler(Context context)
    {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    // Get RequestQueue object used for this session
    public RequestQueue getRequestQueue() {
        // Init mRequestQueue for the first time
        if (mRequestQueue == null) {
            // Init cache map
            mCachesData = new HashMap<>();

            // Init wait list
            mWaitList = new HashMap<>();

            // Init disk cache object
            Cache Cache = new DiskBasedCache(mContext.getCacheDir(), 1024 * 1024 * 4);

            // Create network interface
            Network network = new BasicNetwork(new HurlStack());

            // Create the request queue object
            mRequestQueue = new RequestQueue(Cache, network);

            // Setting request finish listener
            //mRequestQueue.addRequestFinishedListener(this);

            // Start processing
            mRequestQueue.start();

        }

        return mRequestQueue;
    }

    public void AddRequest(final String url, final String tag)
    {
        final StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Store response for url with tag as key
                mCachesData.put(tag, response);

                // Check for completed pages that have listeners and update them
                if (mWaitList.containsKey(tag))
                {
                    RequestsHandlerListener listener = mWaitList.get(tag);
                    listener.OnResult(response);
                    mWaitList.remove(listener);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        request.setTag(tag);
        mRequestQueue.add(request);
    }

    // Request a page by it's tag (key) from the cache
    // The listener is entering a wait list of page still loading
    //
    // @param: tag - Unique key of the page
    // @param: listener - Viewer listener
    public void RequestPageFromCache(String tag, RequestsHandlerListener listener)
    {
        // Check for this tag in the current cached pages
        if (mCachesData.containsKey(tag))
        {
            // Raise listener's OnResult
            listener.OnResult(mCachesData.get(tag));
        }
        else
        {
            // Register the listener in the wait list
            mWaitList.put(tag, listener);
        }
    }

    // Unregister a listener from the list
    public void RemoveFromWaitList(RequestsHandlerListener listener)
    {
        if (mWaitList.containsValue(listener))
            mWaitList.remove(listener);
    }

    // Base singleton get instance
    public static synchronized RequestsHandler GetInstance(Context context)
    {
        if (mInstance == null)
            mInstance = new RequestsHandler(context);

        return mInstance;
    }

    public void Dospose()
    {
        mRequestQueue.stop();
    }

}
