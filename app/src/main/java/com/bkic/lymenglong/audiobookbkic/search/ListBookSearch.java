package com.bkic.lymenglong.audiobookbkic.search;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.checkInternet.ConnectivityReceiver;
import com.bkic.lymenglong.audiobookbkic.checkInternet.MyApplication;
import com.bkic.lymenglong.audiobookbkic.customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.download.DownloadReceiver;
import com.bkic.lymenglong.audiobookbkic.handleLists.adapters.BookAdapter;
import com.bkic.lymenglong.audiobookbkic.handleLists.utils.Book;
import com.bkic.lymenglong.audiobookbkic.handleLists.utils.Category;
import com.bkic.lymenglong.audiobookbkic.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.DB_NAME;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.DB_VERSION;

public class ListBookSearch
        extends AppCompatActivity
        implements  ListBookSearchImp,
                    ConnectivityReceiver.ConnectivityReceiverListener,
                    DownloadReceiver.DownloadReceiverListener{
    private static final String TAG = "ListBookSearch";
    private static final int REQ_CODE_SPEECH_INPUT = 101;
    private PresenterSearchBook presenterSearchBook = new PresenterSearchBook(this);
    private RecyclerView listChapter;
    private BookAdapter bookAdapter;
    private Activity activity = ListBookSearch.this;
    private DBHelper dbHelper;
    private ArrayList<Book> list;
    private ProgressBar progressBar;
    private View imSearch;
    private Category categoryIntent;
/*    private String categoryTitle;
    private int categoryId;
    private String categoryDescription;
    private int categoryParent;
    private int numOfChild;*/
//    private int mPAGE = 1; //page from server
//    private Boolean isFinalPage = false;
    private String keyWord = "";
    private String menuTitle;
    private TextView TextBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        initIntentFilter();
        getDataFromIntent();
        initView();
        initDatabase();
        initObject();
        promptSpeechInput();
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
        categoryIntent = new Category
                (
                        getIntent().getIntExtra("CategoryId", -1),
                        getIntent().getStringExtra("CategoryTitle"),
                        getIntent().getStringExtra("CategoryDescription"),
                        getIntent().getIntExtra("CategoryParent",0),
                        getIntent().getIntExtra("NumOfChild",0)
                );
        menuTitle = getIntent().getStringExtra("MenuTitle");
        /*categoryTitle = getIntent().getStringExtra("CategoryTitle");
        categoryId = getIntent().getIntExtra("CategoryId", -1);
        categoryDescription = getIntent().getStringExtra("CategoryDescription");
        categoryParent = getIntent().getIntExtra("CategoryParent",0);
        numOfChild = getIntent().getIntExtra("NumOfChild",0);*/
    }

    /**
     * Khai báo các view và khởi tạo giá trị
     */
    private void initView() {
        String titleToolbar = categoryIntent.getTitle()==null? menuTitle :categoryIntent.getTitle();
        setTitle(titleToolbar);
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, titleToolbar, false);
        listChapter = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        imSearch = findViewById(R.id.imSearch);
        imSearch.setVisibility(View.VISIBLE);
        TextBar = findViewById(R.id.text_bar);
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    private void initDatabase() {
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
    }

    private void initObject() {
        //set bookAdapter to list view
        SetAdapterToListView();
        GetCursorData();
        imSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityReceiver.isConnected()) {
//                    RefreshBookTable();
//                    mPAGE = 1;
                    promptSpeechInput();
                } else {
                    Toast.makeText(activity, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*private void RefreshBookTable() {
        String DELETE_DATA =
                "UPDATE book " +
                "SET " +
//                        "BookId = NULL, "+
                        "BookTitle = NULL, " +
                        "BookAuthor = NULL, " +
                        "BookPublishDate= NULL, " +
                        "BookImage = NULL, " +
                        "BookContent = NULL, " +
                        "BookLength = NULL, " +
                        "BookURL = NULL, " +
                        "NumOfChapter = NULL " +
                "WHERE CategoryId = '"+categoryIntent.getId()+"'";
        dbHelper.QueryData(DELETE_DATA);
        dbHelper.close();
    }*/

    private void RequestLoadingData(String keyWord) {
        presenterSearchBook.SearchBook(keyWord);
    }

    private void SetAdapterToListView() {
        list = new ArrayList<>();
        bookAdapter = new BookAdapter(ListBookSearch.this, list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        listChapter.setAdapter(bookAdapter);
        /*listChapter.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == SCROLL_STATE_DRAGGING && !isFinalPage){
//                        mPAGE++;
                        RequestLoadingData();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });*/
    }

    //region Method to get data for database
    private void GetCursorData() {
        list.clear();
        if(keyWord.isEmpty()){
            progressBar.setVisibility(View.GONE);
            return;
        }
        String SELECT_DATA = "SELECT * FROM bookSearch WHERE KeyWord = '"+keyWord+"'";
        Cursor cursor = dbHelper.GetData(SELECT_DATA);
        while (cursor.moveToNext()){
            Book bookModel = new Book
                    (
                            cursor.getInt(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getInt(5),
                            cursor.getInt(6)
                    );
            list.add(bookModel);
        }
        cursor.close();
        bookAdapter.notifyDataSetChanged();
        dbHelper.close();
        progressBar.setVisibility(View.GONE);
    }
    //endregion

    @Override
    public void CompareDataPhoneWithServer(JSONArray jsonArray) {

    }

    @Override
    public void SetTableSelectedData(JSONObject jsonObject) throws JSONException {
        Book bookModel = new Book();
        String CategoryId = jsonObject.getString("Category");
        if(!CategoryId.toLowerCase().equals("null")) {
            bookModel.setCategoryId(Integer.parseInt(CategoryId));
            bookModel.setCategoryId(Integer.parseInt(jsonObject.getString("Category")));
            bookModel.setId(Integer.parseInt(jsonObject.getString("BookId")));
            bookModel.setTitle(jsonObject.getString("BookTitle"));
            bookModel.setUrlImage(jsonObject.getString("BookImage"));
            bookModel.setLength(Integer.parseInt(jsonObject.getString("BookLength")));
            bookModel.setAuthor(jsonObject.getString("Author"));
            Cursor cursor = dbHelper.GetData
                    (
                            "SELECT BookId, KeyWord " +
                                    "FROM BookSearch " +
                                    "WHERE BookId = '"+bookModel.getId()+"' AND KeyWord = '"+keyWord+"'");
            int mCount = 0 ;
            if(cursor.moveToFirst()){
                mCount = cursor.getCount();
            }
            if(mCount!=0)return;
            String INSERT_DATA;
            try {
                INSERT_DATA =
                        "INSERT INTO bookSearch VALUES(" +
                                "null, "+ // ID auto increment
                                "'"+bookModel.getId()+"', " +
                                "'"+bookModel.getTitle()+"', " +
                                "'"+bookModel.getAuthor()+"', " +
                                "'"+bookModel.getUrlImage() +"', " +
                                "'"+bookModel.getLength()+"', " +
                                "'"+bookModel.getCategoryId()+"', " + //CategoryID
                                "'"+keyWord+"'"+
                                ")";
                dbHelper.QueryData(INSERT_DATA);
            } catch (Exception e) {
                String UPDATE_DATA =
                        "UPDATE " +
                                "bookSearch " +
                                "SET " +
                                "BookTitle = '"+bookModel.getTitle()+"', " +
                                "BookImage = '"+bookModel.getUrlImage()+"', " +
                                "BookLength = '"+bookModel.getLength()+"' ," +
                                "CategoryId = '"+bookModel.getCategoryId()+"', " + //CategoryId
                                "BookAuthor = '"+bookModel.getAuthor()+"'"+
                                "WHERE " +
                                "BookId = '"+bookModel.getId()+"'";
                dbHelper.QueryData(UPDATE_DATA);
            }
        }
    }

    @Override
    public void ShowListFromSelected() {
        ShowKeySearch(keyWord);
        GetCursorData();
        Log.d(TAG, "onPostExecute: "+ categoryIntent.getTitle());
    }

    private void ShowKeySearch(String keyWord) {
        if(TextBar.getVisibility() == View.GONE)
            TextBar.setVisibility(View.VISIBLE);
        String showTxt = getString(R.string.prompt_keyWord) +" "+ keyWord;
        TextBar.setText(showTxt);
    }

    @Override
    public void LoadListDataFailed(String jsonMessage) {
//        mPAGE--;
//        isFinalPage = true;
        ShowKeySearch(keyWord);
        String ms = "Không tồn tại";
        Toast.makeText(activity, ms, Toast.LENGTH_SHORT).show();
    }

    private void promptSpeechInput() {
        if(ConnectivityReceiver.isConnected()) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());// "vi" for Vietnamese
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    getString(R.string.speech_prompt));
            try {
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
            } catch (ActivityNotFoundException a) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.speech_not_supported),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "Check Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //For speech input
        if(requestCode == REQ_CODE_SPEECH_INPUT )
            if (resultCode == RESULT_OK && null != data) {
            ArrayList<String> result = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            keyWord = result.get(0);
            Log.d(TAG, "onActivityResult: KeyWord: " + keyWord);
            RequestLoadingData(keyWord);
        }
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
}
