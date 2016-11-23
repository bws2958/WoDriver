package com.wodriver;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.amazonaws.mobile.AWSMobileClient;
/**
 * Created by user on 2016-11-19.
 */

public class Application extends MultiDexApplication {
    private static final String LOG_TAG = Application.class.getSimpleName();

    public void onCreate(){
        Log.d(LOG_TAG, "Application.onCreate - Initializing application...");
        super.onCreate();
        initializeApplication();
        Log.d(LOG_TAG, "Application.onCreate - Application initialized OK");
    }

    private void initializeApplication(){
        AWSMobileClient.initializeMobileClientIfNecessary(getApplicationContext());
    }
}
