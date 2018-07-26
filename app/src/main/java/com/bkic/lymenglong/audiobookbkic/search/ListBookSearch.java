package com.bkic.lymenglong.audiobookbkic.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private View imMic;
    private Category categoryIntent;
    private int resultCount = 0;
    private CustomActionBar actionBar;
    private TextView txtToolbar;
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
    private TextView TextBarResultFound;
    private boolean isShowingSearchToolbar = false;
    private EditText editTextToolbar;

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
        actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, titleToolbar, false);
        listChapter = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        imSearch = findViewById(R.id.imSearch);
        imSearch.setVisibility(View.VISIBLE);
        imMic = findViewById(R.id.im_micro);
        imMic.setVisibility(View.VISIBLE);
        TextBar = findViewById(R.id.text_bar);
        TextBarResultFound = findViewById(R.id.text_bar_result_found);
        editTextToolbar = activity.findViewById(R.id.edt_toolbar);
        txtToolbar = activity.findViewById(R.id.tvToolbar);
//        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
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
                ShowSearchTextBar(false);
                if(!isShowingSearchToolbar) {
                    actionBar.searchToolbar(ListBookSearch.this, true);
                    isShowingSearchToolbar = true;
                } else {
                    keyWord = editTextToolbar.getText().toString().trim();
                    if(!keyWord.isEmpty()) {
                        SearchUsingEditText(keyWord);
                    } else {
                        actionBar.searchToolbar(ListBookSearch.this, false);
                        isShowingSearchToolbar = false;
                    }
                }
            }
        });
        imMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityReceiver.isConnected()) {
                    actionBar.searchToolbar(ListBookSearch.this, false);
                    isShowingSearchToolbar = false;
                    promptSpeechInput();
                } else {
                    Toast.makeText(activity, R.string.message_please_check_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });
        editTextToolbar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    keyWord = editTextToolbar.getText().toString().trim();
                    SearchUsingEditText(keyWord);
                    return true;
                }
                // Return true if you have consumed the action, else false.
                return false;
            }
        });
        txtToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isShowingSearchToolbar) {
                    actionBar.searchToolbar(ListBookSearch.this, true);
                    isShowingSearchToolbar = true;
                } else {
                    actionBar.searchToolbar(ListBookSearch.this, false);
                    isShowingSearchToolbar = false;
                }
            }
        });
    }

    private void SearchUsingEditText(String keyWord) {
        if(!keyWord.isEmpty()) RequestLoadingData(keyWord);
        else Toast.makeText(activity, R.string.message_no_key_word, Toast.LENGTH_SHORT).show();
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
        if (isShowingSearchToolbar) {
            actionBar.searchToolbar(ListBookSearch.this, false);
            isShowingSearchToolbar = false;
        }
        list.clear();
        if(keyWord.isEmpty()){
            progressBar.setVisibility(View.GONE);
            return;
        }
        //BookId, BookTitle, BookAuthor, BookImage, BookLength, CategoryId
        String SELECT_DATA =
                "SELECT DISTINCT BookId, BookTitle, BookAuthor, BookImage, BookLength, CategoryId "+
                "FROM bookSearch " +
                "WHERE KeyWord = '"+keyWord+"'";
        Cursor cursor = dbHelper.GetData(SELECT_DATA);
        while (cursor.moveToNext()){
            Book bookModel = new Book
                    (
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getInt(4),
                            cursor.getInt(5)
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
    public void SetTableSelectedData(JSONObject jsonObject) throws JSONException {
        Book bookModel = new Book();
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat =
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String insertTime = simpledateformat.format(calendar.getTime());
        String CategoryId = jsonObject.getString("Category");
        if(!CategoryId.toLowerCase().equals("null")) {
            bookModel.setCategoryId(Integer.parseInt(CategoryId));
            bookModel.setCategoryId(Integer.parseInt(jsonObject.getString("Category")));
            bookModel.setId(Integer.parseInt(jsonObject.getString("BookId")));
            bookModel.setTitle(jsonObject.getString("BookTitle"));
            bookModel.setUrlImage(jsonObject.getString("BookImage"));
            bookModel.setLength(Integer.parseInt(jsonObject.getString("BookLength")));
            bookModel.setAuthor(jsonObject.getString("Author"));
            resultCount++;
            Cursor cursor = dbHelper.GetData
                    (
                            "SELECT BookId, KeyWord " +
                                    "FROM BookSearch " +
                                    "WHERE BookId = '"+bookModel.getId()+"' AND KeyWord = '"+keyWord+"'");
            int mCount = 0 ;
            if(cursor.moveToFirst()) mCount = cursor.getCount();
            if(mCount!=0){
                if(!ConnectivityReceiver.isConnected()) {
                    String UPDATE_DATA =
                            "UPDATE " +
                                    "bookSearch " +
                                    "SET " +
                                    "BookTitle = '"+bookModel.getTitle()+"', " +
                                    "BookImage = '"+bookModel.getUrlImage()+"', " +
                                    "BookLength = '"+bookModel.getLength()+"' ," +
                                    "CategoryId = '"+bookModel.getCategoryId()+"', " + //CategoryId
                                    "BookAuthor = '"+bookModel.getAuthor()+"', "+
                                    "InsertTime = '"+insertTime+"' "+ //make history for key search
                                    "WHERE " +
                                    "BookId = '"+bookModel.getId()+"'";
                    try {
                        dbHelper.QueryData(UPDATE_DATA);
                    } catch (Exception e) {
                        String ADD_COLUMN =
                                "ALTER TABLE bookSearch ADD InsertTime VARCHAR(255);"; //update column
                        dbHelper.QueryData(ADD_COLUMN);
                        dbHelper.QueryData(UPDATE_DATA);
                    }
                } else {
                    String DELETE_DATA = "DELETE FROM bookSearch WHERE KeyWord = '"+keyWord+"'";
                    dbHelper.QueryData(DELETE_DATA);
                    String INSERT_DATA = "INSERT INTO bookSearch VALUES(" +
                            "null, "+ // ID auto increment
                            "'"+bookModel.getId()+"', " +
                            "'"+bookModel.getTitle()+"', " +
                            "'"+bookModel.getAuthor()+"', " +
                            "'"+bookModel.getUrlImage() +"', " +
                            "'"+bookModel.getLength()+"', " +
                            "'"+bookModel.getCategoryId()+"', " + //CategoryID
                            "'"+keyWord+"', " +
                            "'"+insertTime+"'"+
                            ")";
                    try {
                        dbHelper.QueryData(INSERT_DATA);
                    } catch (Exception ignored) {
                        String ADD_COLUMN =
                                "ALTER TABLE bookSearch ADD InsertTime VARCHAR(255);"; //update column
                        dbHelper.QueryData(ADD_COLUMN
                        );
                        dbHelper.QueryData(INSERT_DATA);
                    }
                    dbHelper.close();
                }
                dbHelper.close();
                return;
            }
            String INSERT_DATA = "INSERT INTO bookSearch VALUES(" +
                    "null, "+ // ID auto increment
                    "'"+bookModel.getId()+"', " +
                    "'"+bookModel.getTitle()+"', " +
                    "'"+bookModel.getAuthor()+"', " +
                    "'"+bookModel.getUrlImage() +"', " +
                    "'"+bookModel.getLength()+"', " +
                    "'"+bookModel.getCategoryId()+"', " + //CategoryID
                    "'"+keyWord+"', " +
                    "'"+insertTime+"'"+
                    ")";
            try {
                dbHelper.QueryData(INSERT_DATA);
            } catch (Exception ignored) {
                String ADD_COLUMN =
                        "ALTER TABLE bookSearch ADD InsertTime VARCHAR(255);"; //update column
                dbHelper.QueryData(ADD_COLUMN
                );
                dbHelper.QueryData(INSERT_DATA);
            }
            dbHelper.close();
        }
    }

    @Override
    public void ShowListFromSelected() {
        ShowSearchTextBar(true);
        GetCursorData();
        Log.d(TAG, "onPostExecute: "+ categoryIntent.getTitle());
    }

    private void ShowSearchTextBar(boolean bShow) {
        ShowKeySearch(keyWord,bShow);
        ShowResultFound(resultCount,bShow);
        resultCount = 0;
    }

    private void ShowResultFound(int resultCount, boolean bShow){
        String showTxt = String.valueOf(resultCount);
        TextBarResultFound.setText(showTxt);
        if(bShow) TextBarResultFound.setVisibility(View.VISIBLE);
        else TextBarResultFound.setVisibility(View.GONE);
    }

    private void ShowKeySearch(String keyWord, boolean bShow) {
        String showTxt = getResources().getString(R.string.prompt_keyWord,keyWord);
        TextBar.setText(showTxt);
        if (bShow) TextBar.setVisibility(View.VISIBLE);
        else TextBar.setVisibility(View.GONE);
    }

    @Override
    public void LoadListDataFailed(String jsonMessage) {
//        mPAGE--;
//        isFinalPage = true;
        ShowSearchTextBar(true);
        ClearListSearch();
        String ms = getString(R.string.message_not_exists);
        Toast.makeText(activity, ms, Toast.LENGTH_SHORT).show();
    }

    private void ClearListSearch() {
        list.clear();
        bookAdapter.notifyDataSetChanged();
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
                if(!isShowingSearchToolbar) {
                    actionBar.searchToolbar(ListBookSearch.this, true);
                    isShowingSearchToolbar = true;
                } else {
                    actionBar.searchToolbar(ListBookSearch.this, false);
                    isShowingSearchToolbar = false;
                }
                Toast.makeText(getApplicationContext(),
                        getString(R.string.speech_not_supported),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, getString(R.string.message_please_check_internet_connection), Toast.LENGTH_SHORT).show();
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
            keyWord = result.get(0).trim();
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
