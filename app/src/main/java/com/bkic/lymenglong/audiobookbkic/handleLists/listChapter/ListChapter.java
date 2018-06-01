package com.bkic.lymenglong.audiobookbkic.handleLists.listChapter;

import android.app.Activity;
import android.app.DownloadManager;
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

import com.bkic.lymenglong.audiobookbkic.checkInternet.ConnectivityReceiver;
import com.bkic.lymenglong.audiobookbkic.checkInternet.MyApplication;
import com.bkic.lymenglong.audiobookbkic.customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.download.DownloadReceiver;
import com.bkic.lymenglong.audiobookbkic.handleLists.adapters.ChapterAdapter;
import com.bkic.lymenglong.audiobookbkic.handleLists.utils.Book;
import com.bkic.lymenglong.audiobookbkic.handleLists.utils.Chapter;
import com.bkic.lymenglong.audiobookbkic.handleLists.utils.PresenterShowList;
import com.bkic.lymenglong.audiobookbkic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.DB_NAME;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.DB_VERSION;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.HttpURL_API;

public class ListChapter extends AppCompatActivity
        implements ListChapterImp, ConnectivityReceiver.ConnectivityReceiverListener, DownloadReceiver.DownloadReceiverListener{
    private static final String TAG = "ListChapter";
    PresenterShowList presenterShowList = new PresenterShowList(this);
    private RecyclerView listChapter;
    private ChapterAdapter chapterAdapter;
    private Activity activity = ListChapter.this;
    private DBHelper dbHelper;
    private ArrayList<Chapter> list;
    private ProgressBar pBarCenter, pBarBottom;
    private View imRefresh;
    private Book bookIntent;
    private int mPAGE = 1; // Default page load page 1 at the first time
    private int mLastPAGE;
    private Boolean isFinalPage = false;
    private boolean isLoadingData = false;
    private Toast mToast;
    private boolean isShowingToast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        initIntentFilter();
        initDataFromIntent();
        initView();
        setTitle(bookIntent.getTitle());
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
        //Cancel Toast Notification
        if(isShowingToast) mToast.cancel();
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
        actionBar.eventToolbar(this, bookIntent.getTitle(), true);
        listChapter = findViewById(R.id.listView);
        pBarCenter = findViewById(R.id.progressBar);
        pBarBottom = findViewById(R.id.pb_bottom);
        imRefresh = findViewById(R.id.imRefresh);
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    private void initDatabase() {
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
    }

    private void initObject() {
        //Update BookDetail
        //set chapterAdapter to list view
        SetAdapterToListView();
        //update list
        GetCursorData();
        //region get data from json parsing
        if(list.isEmpty()&&ConnectivityReceiver.isConnected()){
            SetRequestUpdateBookDetail();
            RequestLoadList();
        } else {
            pBarCenter.setVisibility(View.GONE);
        }
        //endregion

        //To refresh list when click button refresh
        imRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConnectivityReceiver.isConnected()) {
                    SetRequestUpdateBookDetail();
                    RefreshChapterTable();
                    isFinalPage = false;
                    mPAGE = 1;
                    RequestLoadList();
                } else
                    Toast.makeText(activity, getString(R.string.message_internet_not_connected), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void RefreshChapterTable() {
        String DELETE_DATA =
                "DELETE FROM chapter "+
                "WHERE BookId = '"+bookIntent.getId()+"'";
        dbHelper.QueryData(DELETE_DATA);
        dbHelper.close();
    }

    private void SetRequestUpdateBookDetail() {
        HashMap<String, String> ResultHash = new HashMap<>();
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\":\"getBookDetail\", " +
                        "\"BookId\":"+ bookIntent.getId() +"" +
                "}";
        ResultHash.put(keyPost,valuePost);
        presenterShowList.GetSelectedResponse(activity, ResultHash, HttpURL_API);
    }

    private void RequestLoadList() {
        isLoadingData = true;
        pBarBottom.setVisibility(View.VISIBLE);
        HashMap<String, String> ResultHash = new HashMap<>();
        int BookId = bookIntent.getId();
        String keyPost = "json";
        String postValue =
                "{" +
                        "\"Action\":\"getChapterList\", " +
                        "\"BookId\":\""+BookId+"\", " +
                        "\"Page\":\""+ mPAGE +"\"" +
                        "}";
        ResultHash.put(keyPost,postValue);
        presenterShowList.GetSelectedResponse(activity, ResultHash, HttpURL_API);
    }


    private void SetAdapterToListView() {
        list = new ArrayList<>();
        chapterAdapter = new ChapterAdapter(ListChapter.this, list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        listChapter.setAdapter(chapterAdapter);
        listChapter.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged: " +
                        "\nnewState = "+newState+"" +
                        "\nSCROLL_STATE_IDLE = "+SCROLL_STATE_IDLE+" " +
                        "\nSCROLL_STATE_DRAGGING ="+SCROLL_STATE_DRAGGING+" " +
                        "\nSCROLL_STATE_SETTLING = "+SCROLL_STATE_SETTLING+""
                );
                if (!isLoadingData) {
                    if(ConnectivityReceiver.isConnected()) {
                        if (newState == SCROLL_STATE_DRAGGING && !isFinalPage) {
                            mPAGE++;
                            Cursor cursor = dbHelper.GetData
                                    (
                                            "SELECT MAX(Page) AS LastPage " +
                                                    "FROM chapter " +
                                                    "WHERE BookId = '" + bookIntent.getId() + "';"
                                    );
                            if (cursor.moveToFirst()) mLastPAGE = cursor.getInt(0);
                            if (mPAGE < mLastPAGE) mPAGE = mLastPAGE;
                            RequestLoadList();
                        }
                    }/*else Toast.makeText(activity, getString(R.string.message_internet_not_connected), Toast.LENGTH_SHORT).show();*/
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled: \ndx ="+dx+" \ndy = "+dy+"");
            }
        });
    }

    //region Method to get data for database
    private void GetCursorData() {
        list.clear();
        Cursor cursor = dbHelper.GetData("SELECT * FROM chapter WHERE BookId = '"+ bookIntent.getId() +"'");
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
        chapterAdapter.notifyDataSetChanged();
        dbHelper.close();
        pBarCenter.setVisibility(View.GONE);
        isLoadingData = false;
        pBarBottom.setVisibility(View.GONE);
    }
    //endregion

    @Override
    public void CompareDataPhoneWithServer(JSONArray jsonArray) {

    }

    @Override
    public void SetUpdateBookDetail(JSONObject jsonObject) throws JSONException {
        Book bookModel = new Book();
        bookModel.setId(Integer.parseInt(jsonObject.getString("BookId")));
        bookModel.setTitle(jsonObject.getString("BookTitle"));
        bookModel.setAuthor(jsonObject.getString("Author"));
        bookModel.setPublishDate(jsonObject.getString("PublishDate"));
        bookModel.setUrlImage(jsonObject.getString("BookImage"));
        bookModel.setContent(jsonObject.getString("BookContent"));
        bookModel.setLength(Integer.parseInt(jsonObject.getString("BookLength")));
        bookModel.setFileUrl(jsonObject.getString("BookURL"));
        bookModel.setCategoryList(jsonObject.getString("CategoryList"));
        bookModel.setNumOfChapter(Integer.parseInt(jsonObject.getString("NumOfChapter")));
        Cursor cursor = dbHelper.GetData("SELECT BookId FROM book WHERE BookId = '"+bookModel.getId()+"'");
        cursor.moveToFirst();
        if(cursor.getCount()==0){
            String INSERT_DATA =
                    "INSERT INTO book VALUES" +
                            "(" +
                            "'"+bookModel.getId()+"', " +
                            "'"+bookModel.getTitle()+"', " +
                            "'"+bookModel.getAuthor()+"', " +
                            "'"+bookModel.getPublishDate()+"', " +
                            "'"+bookModel.getUrlImage()+"', " +
                            "'"+bookModel.getContent()+"', " +
                            "'"+bookModel.getLength()+"', " +
                            "'"+bookModel.getFileUrl()+"', " +
                            "'"+bookModel.getCategoryId()+"', " +
                            "'"+bookModel.getNumOfChapter()+"', " +
                            "'"+0+"', " + //book Status default = 0
                            "'null'"+ //page
                            ");";
            dbHelper.QueryData(INSERT_DATA);
        } else{
            String UPDATE_DATA =
                        "UPDATE book SET " +
                                "BookTitle = '"+bookModel.getTitle()+"', " +
                                "BookAuthor = '"+bookModel.getAuthor()+"', " +
                                "BookPublishDate = '"+bookModel.getPublishDate()+"', " +
                                "BookImage = '"+bookModel.getUrlImage()+"', " +
                                "BookContent = '"+bookModel.getContent()+"', " +
                                "BookLength = '"+bookModel.getLength()+"', " +
                                "BookURL = '"+bookModel.getFileUrl()+"', " +
//                            "CategoryId = '"+bookModel.getCategoryId()+"', " +
                                "NumOfChapter = '"+bookModel.getNumOfChapter()+"' " +
                                "WHERE " +
                                "BookId = '"+bookModel.getId()+"'" +
                                ";";
                dbHelper.QueryData(UPDATE_DATA);
            }
        dbHelper.close();
    }

    @Override
    public void SetTableSelectedData(JSONArray jsonArrayChapter) {
        for (int i = 0; i < jsonArrayChapter.length(); i++) {
            try {
                JSONObject jsonObject = jsonArrayChapter.getJSONObject(i);
                Chapter chapterModel = new Chapter();
                chapterModel.setId(Integer.parseInt(jsonObject.getString("ChapterId")));
                chapterModel.setTitle(jsonObject.getString("ChapterTitle"));
                chapterModel.setFileUrl(jsonObject.getString("ChapterURL"));
                chapterModel.setLength(Integer.parseInt(jsonObject.getString("ChapterLength")));
                int BookId = bookIntent.getId();
                String INSERT_DATA;
                try {
                    INSERT_DATA =
                            "INSERT INTO chapter VALUES" +
                                    "(" +
                                    "'"+chapterModel.getId()+"', " +
                                    "'"+chapterModel.getTitle()+"', " +
                                    "'"+chapterModel.getFileUrl() +"', " +
                                    "'"+chapterModel.getLength() +"', " +
                                    "'"+BookId+"', " + //BookId
                                    "'"+0+"', " + // Status Chapter is equal 0 which mean chapter have not downloaded yet
                                    "'"+mPAGE+"'"+
                                    ")";
                    dbHelper.QueryData(INSERT_DATA);
                } catch (Exception e) {
                    String UPDATE_DATA = "UPDATE chapter SET " +
                            "ChapterTitle = '"+chapterModel.getTitle()+"', " +
                            "ChapterUrl = '"+chapterModel.getFileUrl()+"', " +
                            "ChapterLength = '"+chapterModel.getLength()+"', " +
                            "BookId = '"+BookId+"' " + //BookId
                            "WHERE ChapterId = '"+chapterModel.getId()+"'";
                    dbHelper.QueryData(UPDATE_DATA);
                }
            } catch (JSONException ignored) {
                Log.e(TAG, "onPostExecute: " + jsonArrayChapter);
            }
        }
        if(jsonArrayChapter.length()<10) isFinalPage = true;
    }

    //Now I Don't Use It Any More
    @Override
    public void SetTableSelectedData(JSONObject jsonObject) throws JSONException {
        Chapter chapterModel = new Chapter();
        chapterModel.setId(Integer.parseInt(jsonObject.getString("ChapterId")));
        chapterModel.setTitle(jsonObject.getString("ChapterTitle"));
        chapterModel.setFileUrl(jsonObject.getString("ChapterURL"));
        chapterModel.setLength(Integer.parseInt(jsonObject.getString("ChapterLength")));
        int BookId = bookIntent.getId();
        String INSERT_DATA;
        try {
            INSERT_DATA =
                    "INSERT INTO chapter VALUES" +
                            "(" +
                            "'"+chapterModel.getId()+"', " +
                            "'"+chapterModel.getTitle()+"', " +
                            "'"+chapterModel.getFileUrl() +"', " +
                            "'"+chapterModel.getLength() +"', " +
                            "'"+BookId+"', " + //BookId
                            "'"+0+"', " + // Status Chapter is equal 0 which mean chapter have not downloaded yet
                            "'"+mPAGE+"'"+
                            ")";
            dbHelper.QueryData(INSERT_DATA);
        } catch (Exception e) {
            String UPDATE_DATA = "UPDATE chapter SET " +
                    "ChapterTitle = '"+chapterModel.getTitle()+"', " +
                    "ChapterUrl = '"+chapterModel.getFileUrl()+"', " +
                    "ChapterLength = '"+chapterModel.getLength()+"', " +
                    "BookId = '"+BookId+"' " + //BookId
                    "WHERE ChapterId = '"+chapterModel.getId()+"'";
            dbHelper.QueryData(UPDATE_DATA);
        }
    }

    @Override
    public void ShowListFromSelected() {
        GetCursorData();
        Log.d(TAG, "onPostExecute: "+ bookIntent.getTitle());
    }

    @Override
    public void LoadListDataFailed(String jsonMessage) {
        mPAGE--;
        isFinalPage = true;
        isShowingToast = isShowingToastNotification(jsonMessage);
    }

    private boolean isShowingToastNotification(String jsonMessage){
        mToast = Toast.makeText(activity, jsonMessage, Toast.LENGTH_SHORT);
        mToast.show();
        return true;
    }
}
