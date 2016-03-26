package com.hq.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

/**
 * Data class to handle a web view description.
 */
public class WebViewDesc {



    // Init a web view item with it's required data
    //
    // @param: key - Item key
    // @param: url - The web view url
    // @param: filePath - Page's server file path
    // @param: namespace - [Not sure]
    // @param: cache - True if this web view item is cachable
    // @param: params - Web view page's urls
    // @param: pageTitle - Web view page title
    public WebViewDesc(String key, String url, String filePath, String namespace, boolean cache,
                       HashMap<String, String> params, String pageTitle, TemplateLastUpdate templateUpdate)
    {
        mKey = key;
        mUrl = url;
        mFilePath = filePath;
        mNamespace = namespace;
        mCache = cache;
        mParams = params;
        mPageTitle = pageTitle;
        mTemplateUpdate = templateUpdate;
    }

    public String GetKey()
    {
        return mKey;
    }

    public String GetUrl()
    {
        return mUrl;
    }

    public String GetFilePath()
    {
        return mFilePath;
    }

    public String GetNamespace()
    {
        return mNamespace;
    }

    public boolean IsCache()
    {
        return mCache;
    }

    public HashMap<String, String> GetParams()
    {
        return mParams;
    }

    public String GetPageTitle()
    {
        return mPageTitle;
    }

    public TemplateLastUpdate GetTemplateUpdate()
    {
        return mTemplateUpdate;
    }

    private String mKey;
    private String mUrl;
    private String mFilePath;
    private String mNamespace;
    private boolean mCache;
    private HashMap<String, String> mParams; // Autofill
    private String mPageTitle;
    private TemplateLastUpdate mTemplateUpdate;

} // WebViewDesc
