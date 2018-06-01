package com.bkic.lymenglong.audiobookbkic.handleLists.history;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.bkic.lymenglong.audiobookbkic.checkInternet.ConnectivityReceiver;
import com.bkic.lymenglong.audiobookbkic.checkInternet.MyApplication;
import com.bkic.lymenglong.audiobookbkic.customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.download.DownloadReceiver;
import com.bkic.lymenglong.audiobookbkic.handleLists.utils.Book;
import com.bkic.lymenglong.audiobookbkic.handleLists.utils.Chapter;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.handleLists.listChapter.ListChapter;

import java.util.ArrayList;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.DB_NAME;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.DB_VERSION;

public class ListHistoryChapter
        extends AppCompatActivity
        implements
        ConnectivityReceiver.ConnectivityReceiverListener,
        DownloadReceiver.DownloadReceiverListener{
//    private static final String TAG = "ListChapter";
    private RecyclerView listChapter;
    private HistoryChapterAdapter historyChapterAdapter;
//    private Activity activity = ListOfflineChapter.this;
    private DBHelper dbHelper;
    private ArrayList<Chapter> list;
    private ProgressBar progressBar;
    private Book bookIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        initIntentFilter();
        initDataFromIntent();
        initView();
        setTitle(bookIntent.getTitle());
        initDatabase();
//        initUpdateBookDownloadStatus();
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
        // Update
//        initUpdateBookDownloadStatus();
        GetCursorData();
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
        /*initUpdateBookDownloadStatus();
        GetCursorData();*/
    }
    //endregion

    /**
     * Lấy dữ liệu thông qua intent
     */
    private void initDataFromIntent() {
        bookIntent = new Book
                (
                        getIntent().getIntExtra("BookId", -1),
                        getIntent().getStringExtra("BookTitle"),
                        getIntent().getStringExtra("BookImage"),
                        getIntent().getIntExtra("BookLength", 0),
                        getIntent().getIntExtra("CategoryId", -1)
                );
    }

    /**
     * Khai báo các view và khởi tạo giá trị
     */
    private void initView() {
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, bookIntent.getTitle(), false);
        listChapter = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    private void initDatabase() {
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
    }

    private void initObject() {
        //Update BookDetail
        //set historyChapterAdapter to list view
        SetAdapterToListView();
        //update list
        GetCursorData();
    }


    private void SetAdapterToListView() {
        list = new ArrayList<>();
        historyChapterAdapter = new HistoryChapterAdapter(ListHistoryChapter.this, list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        listChapter.setAdapter(historyChapterAdapter);
    }

    //region Method to get data for database
    private void GetCursorData() {
        list.clear();
        Cursor cursor = dbHelper.GetData
                (
                        "SELECT * " +
                                "FROM chapter, playHistory " +
                                    "WHERE " +
                                        "chapter.BookId = '"+ bookIntent.getId() +"' " +
                                        "AND " +
                                        "chapter.ChapterId = playHistory.ChapterId" +
                                ";"
                );
        while (cursor.moveToNext()){
            Chapter chapterModel = new Chapter();
            chapterModel.setId(cursor.getInt(0));
            chapterModel.setTitle(cursor.getString(1));
            chapterModel.setFileUrl(cursor.getString(2));
            chapterModel.setLength(cursor.getInt(3));
            chapterModel.setBookId(cursor.getInt(4));
            list.add(chapterModel);
        }
        cursor.close();
        dbHelper.close();
        historyChapterAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        if(ConnectivityReceiver.isConnected() && list.size() == 0){
            Intent intent = new Intent(ListHistoryChapter.this, ListChapter.class);
            intent.putExtra("BookId", bookIntent.getId());
            intent.putExtra("BookTitle", bookIntent.getTitle());
            intent.putExtra("BookImage", bookIntent.getUrlImage());
            intent.putExtra("BookLength", bookIntent.getLength());
            intent.putExtra("BookAuthor", bookIntent.getAuthor());
            startActivity(intent);
            this.finish();
        }
    }
}
