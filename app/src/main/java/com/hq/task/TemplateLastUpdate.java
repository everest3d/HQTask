package com.hq.task;

/**
 * Created by Amr on 3/26/2016.
 */
// Template last update data/time
public class TemplateLastUpdate
{

    TemplateLastUpdate(int stamp, String dataTime)
    {
        mStamp = stamp;
        mDataTime = dataTime;
    }

    int GetStamp()
    {
        return mStamp;
    }

    String GetDataTime()
    {
        return mDataTime;
    }

    String ToString()
    {
        return GetDataTime();
    }

    private int mStamp;
    private String mDataTime;

} // TemplateLastUpdate
