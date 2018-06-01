package com.bkic.lymenglong.audiobookbkic.services;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.bkic.lymenglong.audiobookbkic.download.DownloadReceiver;

public class MyDownloadService extends Service {

    private static final String TAG = "MyDownloadService";
    private DownloadReceiver downloadReceiver;
    IntentFilter filter;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return null;
    }

    @Override
    public void onCreate() {
//        Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onCreate");
        filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        downloadReceiver = new DownloadReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onStartCommand");
        registerReceiver(downloadReceiver, filter);
        /*If you start your Service using START_NOT_STICKY,
        then your app will kill your service once your entire application is closed from background
        i.e. you cleaned your app from home screen.*/
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
//        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onDestroy");
        unregisterReceiver(downloadReceiver);
    }
}
