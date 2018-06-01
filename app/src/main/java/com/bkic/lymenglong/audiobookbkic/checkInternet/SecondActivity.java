package com.bkic.lymenglong.audiobookbkic.checkInternet;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bkic.lymenglong.audiobookbkic.R;

import java.util.Objects;

public class SecondActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Manually checking internet connection
        checkConnection();
        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.coordinator_layout), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
}
