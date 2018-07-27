package com.bkic.lymenglong.audiobookbkic.handleLists.history;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.account.login.Session;
import com.bkic.lymenglong.audiobookbkic.checkInternet.ConnectivityReceiver;
import com.bkic.lymenglong.audiobookbkic.checkInternet.MyApplication;
import com.bkic.lymenglong.audiobookbkic.customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.download.DownloadReceiver;
import com.bkic.lymenglong.audiobookbkic.handleLists.utils.Book;
import com.bkic.lymenglong.audiobookbkic.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.DB_NAME;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.DB_VERSION;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.HttpURL_API;

public class ListHistory
        extends AppCompatActivity
        implements
                ListHistoryImp,
                ConnectivityReceiver.ConnectivityReceiverListener,
                DownloadReceiver.DownloadReceiverListener{
    private static final String TAG = "ListHistory";
    private PresenterShowListHistory presenterShowList = new PresenterShowListHistory(this);
    private PresenterUpdateHistory presenterUpdateHistory = new PresenterUpdateHistory(this);
    private RecyclerView listChapter;
    private View imRefresh;
    private HistoryAdapter historyAdapter;
    private String menuTitle;
    private Activity activity = ListHistory.this;
    private Session session;
    private ProgressBar progressBar;
    private DBHelper dbHelper;
    private ArrayList <Book> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        initIntentFilter();
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        getDataFromIntent();
        setTitle(menuTitle);
        initView();
        initDatabase();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here
        if(requestCode == Const.REQUEST_CODE_BACK_HOME)
            if (data != null)
                if (data.getBooleanExtra(Const.STRING_BACK_HOME, false)) {
                    Intent intent = new Intent();
                    intent.putExtra(Const.STRING_BACK_HOME, true);
                    setResult(Const.REQUEST_CODE_BACK_HOME, intent);
                    finish();//finishing activity
                }
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
//        int idMenu = getIntent().getIntExtra("idHome", -1);
    }

    /**
     * Khai báo các view và khởi tạo giá trị
     */
    private void initView() {
        session = new Session(activity);
        progressBar = findViewById(R.id.progressBar);
        imRefresh = findViewById(R.id.imRefresh);
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, menuTitle, true);
        listChapter = findViewById(R.id.listView);
    }
    private void SetUpdateTableData(Book arrayModel) {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat =
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String insertTime = simpledateformat.format(calendar.getTime());
        String UPDATE_DATA =
                "UPDATE " +
                        "history " +
                "SET " +
                        "BookTitle = '"+arrayModel.getTitle()+"', " +
                        "BookImage = '"+arrayModel.getUrlImage()+"', " +
                        "BookLength = '"+arrayModel.getLength()+"', " +
                        "BookAuthor = '"+arrayModel.getAuthor()+"', " +
                        "BookSync = '"+Const.BOOK_SYNCED_WITH_SERVER+"'," +
                        "InsertTime = '"+insertTime+"' " +
                "WHERE " +
                        "BookId = '"+arrayModel.getId()+"'; ";
        dbHelper.QueryData(UPDATE_DATA);
        dbHelper.close();
    }

    private void initDatabase() {
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
    }

    private void GetCursorData() {
        Cursor cursor;
        list.clear();
        cursor = dbHelper.GetData("SELECT * FROM history");
        while (cursor.moveToNext()) {
            int bookId = cursor.getInt(0);
            String bookTitle = cursor.getString(1);
            String bookImage = cursor.getString(2);
            int bookLength = cursor.getInt(3);
            String bookAuthor = cursor.getString(4);

            list.add(new Book(bookId,bookTitle,bookImage,bookLength,bookAuthor));
        }
        cursor.close();
        historyAdapter.notifyDataSetChanged();
        dbHelper.close();
    }

    private void initObject() {
        imRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityReceiver.isConnected()) {
                    RemoveHistoryDataInSQLite();
                    SyncRemoveBooks();
                    RequestLoadingData();
                } else Toast.makeText(activity, getString(R.string.message_internet_not_connected), Toast.LENGTH_SHORT).show();
//                presenterUpdateHistory.RequestToRemoveBookById("16","2162");
            }
        });
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        historyAdapter = new HistoryAdapter(activity, list);
        listChapter.setAdapter(historyAdapter);
        GetCursorData();
        //get data from json parsing
        if(list.isEmpty()) {
            RequestLoadingData();
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void SyncRemoveBooks() {
        Cursor cursor = dbHelper.GetData(
                "SELECT BookId FROM bookHistorySyncs WHERE BookRemoved = '"+Const.BOOK_REQUEST_REMOVE_WITH_SERVER+"'"
        );
        while (cursor.moveToNext()){
            //calling the method to save the unsynced books to MySQL server
            presenterUpdateHistory.RequestToRemoveBookById
                    (
                            String.valueOf(session.getUserIdLoggedIn()),
                            String.valueOf(cursor.getInt(0))
                    );
        }
        cursor.close();
    }

    private void RequestLoadingData() {
        int userId =session.getUserIdLoggedIn();
        HashMap<String,String> ResultHash = new HashMap<>();
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\":\"getHistory\", " +
                        "\"UserId\":\""+userId+"\"" +
                        "}";
        ResultHash.put(keyPost,valuePost);
        presenterShowList.GetSelectedResponse(activity, ResultHash, HttpURL_API);
    }

    private void SetInsertTableData(Book arrayModel) {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat =
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String insertTime = simpledateformat.format(calendar.getTime());
        String INSERT_DATA =
                "INSERT INTO history " +
                        "(" +
                        "BookId, BookTitle, BookImage, BookLength, BookAuthor, BookSync, BookRemoved, InsertTime" +
                        ") " +
                        "VALUES" +
                        "(" +
                                "'"+arrayModel.getId()+"', " +
                                "'"+arrayModel.getTitle()+"', " +
                                "'"+arrayModel.getUrlImage()+"', " +
                                "'"+arrayModel.getLength()+"', " +
                                "'"+arrayModel.getAuthor()+"', " +
                                "'"+Const.BOOK_SYNCED_WITH_SERVER+"', " +//BookSync has already store in server
                                "'"+Const.BOOK_NOT_REQUEST_REMOVE_SYNCED_WITH_SERVER+"', " +
                                "'"+insertTime+"'"+
                        ");";
        dbHelper.QueryData(INSERT_DATA);
        dbHelper.close();
    }

    @Override
    public void CompareDataPhoneWithServer(JSONArray jsonArray) {

    }

    @Override
    public void SetTableSelectedData(JSONObject jsonObject) throws JSONException {

        Book tempModel = new Book();
        tempModel.setId(Integer.parseInt(jsonObject.getString("BookId")));
        tempModel.setTitle(jsonObject.getString("BookTitle"));
        tempModel.setUrlImage(jsonObject.getString("BookImage"));
        tempModel.setLength(Integer.parseInt(jsonObject.getString("BookLength")));
        tempModel.setAuthor(jsonObject.getString("Author"));

        try {
            SetInsertTableData(tempModel);
        } catch (Exception e) {
            SetUpdateTableData(tempModel);
        }
    }

    @Override
    public void ShowListFromSelected() {
        progressBar.setVisibility(View.GONE);
        GetCursorData();
        Log.d(TAG, "onPostExecute: "+ menuTitle);
    }

    @Override
    public void LoadListDataFailed(String jsonMessage) {
        GetCursorData();
        Log.d(TAG, "LoadListDataFailed: "+ jsonMessage);
    }

    private void RemoveHistoryDataInSQLite() {
        String DELETE_DATA = "DELETE FROM history";
        dbHelper.QueryData(DELETE_DATA);
        dbHelper.close();
    }

    @Override
    public void RemoveHistorySuccess(String message) {
        Log.d(TAG, "RemoveHistorySuccess: "+message);
    }

    @Override
    public void RemoveHistoryFailed(String message) {
        Log.e(TAG, "RemoveHistoryFailed: "+message);
    }

    @Override
    public void RemoveAllHistorySuccess(String message) {
        Log.d(TAG, "RemoveAllHistorySuccess: "+message);
    }

    @Override
    public void RemoveAllHistoryFailed(String message) {
        Log.e(TAG, "RemoveAllHistoryFailed: "+message);
    }
}
