package com.bkic.lymenglong.audiobookbkic.help;

import android.app.DownloadManager;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.checkInternet.ConnectivityReceiver;
import com.bkic.lymenglong.audiobookbkic.checkInternet.MyApplication;
import com.bkic.lymenglong.audiobookbkic.customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.download.DownloadReceiver;
import com.bkic.lymenglong.audiobookbkic.R;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;


public class HelpActivity
        extends AppCompatActivity
        implements  HelpImp,
                    ConnectivityReceiver.ConnectivityReceiverListener,
                    DownloadReceiver.DownloadReceiverListener{
    private PresenterHelp presenterHelp = new PresenterHelp(this);
    private TextView tvReadFile;
    private String menuTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        initIntentFilter();
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
        getDataFromIntent();
        initView();
        initObject();

    }


    //region BroadCasting
    //connectionReceiver
    private IntentFilter intentFilter;
    private ConnectivityReceiver receiver;
    //downloadReceiver
    private IntentFilter filter;
    private DownloadReceiver downloadReceiver;

    private void initIntentFilter() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new ConnectivityReceiver();
        //set filter to only when download is complete and register broadcast receiver
        filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        downloadReceiver = new DownloadReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register receiver
        registerReceiver(receiver, intentFilter);
        registerReceiver(downloadReceiver, filter);
        // register status listener
        MyApplication.getInstance().setConnectivityListener(this);
        MyApplication.getInstance().setDownloadListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregister receiver
        unregisterReceiver(receiver);
        unregisterReceiver(downloadReceiver);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
    }

    @Override
    public void onDownloadCompleted(long downloadId) {

    }
    //endregion


    /**
     * Lấy dữ liệu thông qua intent
     */
    private void getDataFromIntent() {
        menuTitle = getIntent().getStringExtra("MenuTitle");
//        int idHome = getIntent().getIntExtra("idHome", -1);
    }


    private void initObject() {
        presenterHelp.ShowHelp(tvReadFile);
    }


    /**
     * Khai báo các view và khởi tạo giá trị
     */
    private void initView() {
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, menuTitle, false );
        tvReadFile = findViewById(R.id.tv_read_file);
    }

    @Override
    public void ShowHelpDone() {
        String TAG = "HelpActivity";
        Log.d(TAG, "ShowHelpDone");
    }

    @Override
    public void ShowHelpFailed() {
        Toast.makeText(this, "Reading file failed", Toast.LENGTH_SHORT).show();
    }
}
