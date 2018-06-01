package com.bkic.lymenglong.audiobookbkic.account.showUserInfo;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.account.login.Session;
import com.bkic.lymenglong.audiobookbkic.account.utils.User;
import com.bkic.lymenglong.audiobookbkic.checkInternet.ConnectivityReceiver;
import com.bkic.lymenglong.audiobookbkic.checkInternet.MyApplication;
import com.bkic.lymenglong.audiobookbkic.customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.download.DownloadReceiver;
import com.bkic.lymenglong.audiobookbkic.utils.Const;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class UserInfoActivity
        extends AppCompatActivity
        implements  UserInfoImp,
                    View.OnClickListener,
                    ConnectivityReceiver.ConnectivityReceiverListener,
                    DownloadReceiver.DownloadReceiverListener{
    private PresenterUserInfo presenterUserInfo = new PresenterUserInfo(this);
    private AppCompatActivity activity = UserInfoActivity.this;
    private AppCompatTextView textViewName;
    private AppCompatButton btnLogout;
    private AppCompatButton btnEdit;
    private AppCompatButton btnChangePassword;
    private RecyclerView recyclerViewUsers;
    private List<User> listUsers;
    private UserInfoRecyclerAdapter userInfoRecyclerAdapter;
    private Session session;
    private String message;
    private String menuTitle;
//    private DBHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initIntentFilter();
        initDataFromIntent();
        initToolbar();
        initViews();
        initObjects();
        initListener();

        DisplayUserDetail();
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

    @Override
    public void DisplayUserDetail(){
        new getDataFromPrefs(this).execute();
        String Name = session.getFullName();
        textViewName.setText(getString(R.string.text_hello)+" "+ Name.toUpperCase());
    }

    private void initDataFromIntent() {
        menuTitle = getIntent().getStringExtra("MenuTitle");
    }

    /**
     * This method is to initialize views
     */
    private void initViews() {
        textViewName = findViewById(R.id.textViewName);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        btnLogout = findViewById(R.id.btn_logout);
        btnEdit = findViewById(R.id.btn_edit);
        btnChangePassword = findViewById(R.id.btn_change_password);
        //make talk don't move to toolbar
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar),
                ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    /**
     * This method is to initialize objects to be used
     */
    @SuppressLint("SetTextI18n")
    private void initObjects() {
//        dbHelper = new DBHelper(this, Const.DB_NAME,null,Const.DB_VERSION);
        session = new Session(this);
        listUsers = new ArrayList<>();
        userInfoRecyclerAdapter = new UserInfoRecyclerAdapter(listUsers);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewUsers.setLayoutManager(mLayoutManager);
        recyclerViewUsers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setAdapter(userInfoRecyclerAdapter);

    }

    private void initListener() {
        btnLogout.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);
    }

    /**
    *This will clear the data and remove your app from memory.
    *It is equivalent to clear data option under Settings --> Application Manager --> Your App --> Clear data
    *todo: clear app data
    **/
/*    private void deleteAppData() {
        try {
            // clearing app data
            String packageName = getApplicationContext().getPackageName();
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear "+packageName);

        } catch (Exception e) {
            e.printStackTrace();
        } }*/

    private void initToolbar() {
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, menuTitle, false);
    }

    /**
     * This method is to fetch all user records from Server
     */
    @SuppressLint("StaticFieldLeak")
    public class getDataFromPrefs extends AsyncTask<Void, Void, Void>
    {
        // AsyncTask is used that SQLite operation not blocks the UI Thread.
        Context context;

        getDataFromPrefs(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            listUsers.clear();
            listUsers.addAll(session.getListUserInfo());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            userInfoRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_logout :
                presenterUserInfo.ShowAlertLogoutDialog(activity);
                break;
            case R.id.btn_edit:
                presenterUserInfo.ShowDialogConfirmPassword(activity);
                break;
            case R.id.btn_change_password:
                presenterUserInfo.ShowDialogUpdatePassword(activity);
        }
    }

    @Override
    public List<User> GetCurrentUserDetail() {
        listUsers.clear();
        listUsers.addAll(session.getListUserInfo());
        return listUsers;
    }

    @Override
    public void UpdatePasswordSuccess(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void UpdatePasswordFailed(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void UpdateDetailSuccess(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void UpdateDetailFailed(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void LogoutFailed() {
        message = getString(R.string.message_logout_failed);
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void LogoutSuccess() {
        session.getClearSession();
        session = new Session(this);
        session.setLoggedin(false);
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// This flag ensures all activities on top of the MainActivity are cleared.
        intent.putExtra("EXIT", true);
        message = getString(R.string.message_logout_success);
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        String TAG = "UserInfoActivity";
        Log.d(TAG, "LogoutSuccess");
        activity.startActivity(intent);
        activity.finish();
        //drop database
        deleteDatabase(Const.DB_NAME);
    }
}
