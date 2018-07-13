package com.bkic.lymenglong.audiobookbkic.main;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.account.login.ViewLoginActivity;
import com.bkic.lymenglong.audiobookbkic.checkInternet.ConnectivityReceiver;
import com.bkic.lymenglong.audiobookbkic.checkInternet.MyApplication;
import com.bkic.lymenglong.audiobookbkic.database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.download.DownloadReceiver;
import com.bkic.lymenglong.audiobookbkic.overrideTalkBack.PresenterOverrideTalkBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.DB_NAME;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.DB_VERSION;

public class MainActivity extends AppCompatActivity
        implements  MainImp,
                    ConnectivityReceiver.ConnectivityReceiverListener,
                    DownloadReceiver.DownloadReceiverListener{
//    PresenterMain presenterMain = new PresenterMain(this);
    private PresenterOverrideTalkBack presenterOverrideTalkBack = new PresenterOverrideTalkBack(this);
    private PresenterMain presenterMain = new PresenterMain(this);
    private static final String TAG = "MainActivity";
    private RecyclerView homeList;
    private MainAdapter mainAdapter;
    DBHelper dbHelper;
    private static ArrayList<Menu> menuList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initIntentFilter();
        getDataFromIntent();
        initView();
        initDatabase();
        initObject();
        GetCursorData();
        //get data from json parsing
//        presenterMain.GetHttpResponse(HttpUrl_ALLMenuData);
    }

    private void CheckTalkBackAccessibility() {
        Boolean enable = presenterOverrideTalkBack.talkBackEnable(this);
        if (!enable) findViewById(R.id.txt_bar).setVisibility(View.VISIBLE);
        else findViewById(R.id.txt_bar).setVisibility(View.GONE);
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

        CheckTalkBackAccessibility();

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
//        ToastConnectionMessage(isConnected);
    }

    @Override
    public void onDownloadCompleted(long downloadId) {

    }
    //endregion

/*    private void ToastConnectionMessage(boolean isConnected) {
        String message;
        if (isConnected) {
            message = getString(R.string.message_internet_connected);
        } else {
            message = getString(R.string.message_internet_not_connected);
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }*/

    // to make application remember pass LoginActivity in to MainActivity
    private void getDataFromIntent() {
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            Intent intent = new Intent(this, ViewLoginActivity.class);
            startActivity(intent);
        }
    }

    private void initView() {
        homeList = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.label_name),
                ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    private void initDatabase() {
        dbHelper = new DBHelper(this, DB_NAME, null, DB_VERSION);
    }

    private void initObject() {
//        menus = databaseHelper.getHomeList();
        menuList = new ArrayList<>();
        mainAdapter = new MainAdapter(MainActivity.this, menuList);
//        mainAdapter = new MainAdapter(MainActivity.this, menus);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        homeList.setLayoutManager(mLinearLayoutManager);
        homeList.setAdapter(mainAdapter);
    }

    private void GetCursorData() {
        menuList.clear();
        String SELECT_MENU_DATA = "SELECT * FROM menu";
        Cursor cursor = dbHelper.GetData(SELECT_MENU_DATA);
        while (cursor.moveToNext()) {
            String title = cursor.getString(1);
            int id = cursor.getInt(0);
            menuList.add(new Menu(id, title));
        }
        cursor.close();
        mainAdapter.notifyDataSetChanged();
        dbHelper.close();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void ShowListMenu() {
//        progressBar.setVisibility(View.GONE);
        GetCursorData();
        Log.d(TAG, "onPostExecute: " + getTitle());
    }

    @Override
    public void SetMenuData(JSONObject jsonObject) throws JSONException {
        Menu menuModel = new Menu();
        menuModel.setId(Integer.parseInt(jsonObject.getString("Id")));
        menuModel.setTitle(jsonObject.getString("Name"));
        int Id = menuModel.getId();
        String Name = menuModel.getTitle();
        try {
            String INSERT_DATA = "INSERT INTO menu VALUES('"+Id+"','"+Name+"')";
            dbHelper.QueryData(INSERT_DATA);
        } catch (Exception e) {
            String UPDATE_DATA = "UPDATE menu SET Name = '" + Name + "' WHERE Id = '" + Id + "'";
            dbHelper.QueryData(UPDATE_DATA);
        }
        Log.d(TAG, "SetMenuData");
    }

/*    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    private Toast backToast;*/
    @Override
    public void onBackPressed()
    {
        //Press again to exit
        /*if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), R.string.message_exit, Toast.LENGTH_SHORT);
            backToast.show();
        }
        mBackPressed = System.currentTimeMillis();*/

        presenterMain.ShowDialogExit();
    }
}