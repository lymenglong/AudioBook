package com.bkic.lymenglong.audiobookbkic.checkInternet;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bkic.lymenglong.audiobookbkic.R;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class MainActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener {
    IntentFilter intentFilter;
    ConnectivityReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btnCheck = findViewById(R.id.btn_check);
        FloatingActionButton fab = findViewById(R.id.fab);

        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new ConnectivityReceiver();

        // Manually checking internet connection
        checkConnection();

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Manually checking internet connection
                checkConnection();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
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
                .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        registerReceiver(receiver, intentFilter); // android version > 5
        MyApplication.getInstance().setConnectivityListener(this); //for api < 24 , android version < 6
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(receiver);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}
