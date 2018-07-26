package com.bkic.lymenglong.audiobookbkic.handleLists.listCategory;

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
import com.bkic.lymenglong.audiobookbkic.checkInternet.ConnectivityReceiver;
import com.bkic.lymenglong.audiobookbkic.checkInternet.MyApplication;
import com.bkic.lymenglong.audiobookbkic.customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.download.DownloadReceiver;
import com.bkic.lymenglong.audiobookbkic.handleLists.adapters.CategoryAdapter;
import com.bkic.lymenglong.audiobookbkic.handleLists.utils.Category;
import com.bkic.lymenglong.audiobookbkic.handleLists.utils.PresenterShowList;
import com.bkic.lymenglong.audiobookbkic.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.DB_NAME;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.DB_VERSION;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.HttpURL_API;

public class ListCategory extends AppCompatActivity
        implements ListCategoryImp, ConnectivityReceiver.ConnectivityReceiverListener, DownloadReceiver.DownloadReceiverListener {
    private static final String TAG = "ListCategory";
    PresenterShowList presenterShowList = new PresenterShowList(this);
    private RecyclerView listChapter;
    private View imRefresh;
    private CategoryAdapter adapter;
    private String title;
    private Activity activity = ListCategory.this;
    private ProgressBar progressBar;
    private DBHelper dbHelper;
    private ArrayList <Category> list = new ArrayList<>();
    private String menuTitle;
/*    private String categoryTitle;
    private int categoryId;
    private String categoryDescription;
    private int categoryParent;
    private int numOfChild;*/
    private Category categoryFromIntent;
    private boolean isLoading = false;
    private ProgressBar pBarBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        initIntentFilter();
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        getDataFromIntent();
        SetToolBarTitle();
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

    private void SetToolBarTitle() {
        if(menuTitle == null){
            title = categoryFromIntent.getTitle();
        } else{
            title = menuTitle;
        }
        setTitle(title);
    }

    /**
     * Lấy dữ liệu thông qua intent
     */
    private void getDataFromIntent() {
        menuTitle = getIntent().getStringExtra("MenuTitle");
        /*categoryTitle = getIntent().getStringExtra("CategoryTitle");
        categoryId = getIntent().getIntExtra("CategoryId", 0);
        categoryDescription = getIntent().getStringExtra("CategoryDescription");
        categoryParent = getIntent().getIntExtra("CategoryParent",0);
        numOfChild = getIntent().getIntExtra("NumOfChild",0);*/
        categoryFromIntent =
                new Category(
                        getIntent().getIntExtra("CategoryId", 0),
                        getIntent().getStringExtra("CategoryTitle"),
                        getIntent().getStringExtra("CategoryDescription"),
                        getIntent().getIntExtra("CategoryParent",0),
                        getIntent().getIntExtra("NumOfChild",0)
                        );
    }

    /**
     * Khai báo các view và khởi tạo giá trị
     */
    private void initView() {
        progressBar = findViewById(R.id.progressBar);
        pBarBottom = findViewById(R.id.pb_bottom);
        imRefresh = findViewById(R.id.imRefresh);
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, title, true);
        listChapter = findViewById(R.id.listView);
    }

    private void SetUpdateTableData(Category arrayModel) {
        String UPDATE = "UPDATE category SET " +
                "CategoryTitle = '" + arrayModel.getTitle() + "', " +
                "CategoryDescription = '" + arrayModel.getDescription() + "', " +
                "CategoryParent = '" + arrayModel.getParentId() + "', " +
                "NumOfChild = '" + arrayModel.getNumOfChild() + "' " +
                "WHERE " +
                "CategoryId = '" + arrayModel.getId() + "';";
        dbHelper.QueryData(UPDATE);
        dbHelper.close();
    }

    private void initDatabase() {
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
    }

    private void GetCursorData(int parentId) {
        Cursor cursor;
        list.clear();
        String SELECT_DATA = "SELECT * FROM category WHERE CategoryParent = '"+parentId+"'";
        cursor = dbHelper.GetData(SELECT_DATA);
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String description = cursor.getString(2);
            int parent = cursor.getInt(3);
            int numOfChild = cursor.getInt(4);
            list.add(new Category(id,title,description,parent,numOfChild));
        }
        cursor.close();
        adapter.notifyDataSetChanged();
        dbHelper.close();
        progressBar.setVisibility(View.GONE);
        isLoading = false;
        pBarBottom.setVisibility(View.GONE);
    }

    private void initObject() {
        imRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConnectivityReceiver.isConnected() && !isLoading) {
                    RefreshCategoryTable();
                    RefreshLoadingData();
                }
                else Toast.makeText(activity, getString(R.string.message_internet_not_connected), Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        adapter = new CategoryAdapter(ListCategory.this, list);
        listChapter.setAdapter(adapter);
        int parentId = categoryFromIntent.getId(); //getIntent
        GetCursorData(parentId);
        //get data from json parsing
        if(list.isEmpty()&& ConnectivityReceiver.isConnected()){
            RefreshCategoryTable();
            RefreshLoadingData();
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void RefreshCategoryTable() {
        String DELETE_DATA =
                "DELETE FROM category";
        dbHelper.QueryData(DELETE_DATA);
        dbHelper.close();
    }

    private void RefreshLoadingData() {
        isLoading = true;
        pBarBottom.setVisibility(View.VISIBLE);
        HashMap<String, String> ResultHash = new HashMap<>();
        String keyPost = "json";
        String postValue = "{\"Action\":\"getListCategory\"}";
        ResultHash.put(keyPost, postValue);
        presenterShowList.GetSelectedResponse(activity,ResultHash, HttpURL_API);
    }

    private void SetInsertTableData(Category arrayModel) {
        String INSERT_DATA =
                "INSERT INTO category VALUES" +
                "(" +
                        "'"+arrayModel.getId()+"', " +
                        "'"+arrayModel.getTitle()+"', " +
                        "'"+arrayModel.getDescription()+"', " +
                        "'"+arrayModel.getParentId()+"', " +
                        "'"+arrayModel.getNumOfChild()+"'" +
                ")";
        dbHelper.QueryData(INSERT_DATA);
    }

    @Override
    public void CompareDataPhoneWithServer(JSONArray jsonArray) {
    }

    @Override
    public void ShowListFromSelected() {
        int parentId = categoryFromIntent.getId(); //getIntent
        GetCursorData(parentId);
        Log.d(TAG, "onPostExecute: "+ title);
    }

    @Override
    public void LoadListDataFailed(String jsonMessage) {
        Toast.makeText(activity, jsonMessage, Toast.LENGTH_SHORT).show();
    }
    //only 7 categories classify supported
    @Override
    public void SetTableSelectedData(JSONArray jsonArray) throws JSONException {
        JSONArray jsonArrayChild;
        JSONArray jsonArrayChild2;
        JSONArray jsonArrayChild3;
        JSONArray jsonArrayChild4;
        JSONArray jsonArrayChild5;
        JSONArray jsonArrayChild6;
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObject = jsonArray.getJSONObject(i);
            jsonArrayChild = new JSONArray(jsonObject.getString("CategoryChildren"));
            SetDataFromJsonObject(jsonObject);
            for (int j = 0; j < jsonArrayChild.length(); j++){
                jsonObject = jsonArrayChild.getJSONObject(j);
                SetDataFromJsonObject(jsonObject);
                jsonArrayChild2 = new JSONArray(jsonObject.getString("CategoryChildren"));
                for (int k = 0; k < jsonArrayChild2.length(); k++){
                    jsonObject = jsonArrayChild2.getJSONObject(k);
                    SetDataFromJsonObject(jsonObject);
                    jsonArrayChild3 = new JSONArray(jsonObject.getString("CategoryChildren"));
                    for (int l = 0; l < jsonArrayChild3.length(); l++){
                        jsonObject = jsonArrayChild3.getJSONObject(l);
                        SetDataFromJsonObject(jsonObject);
                        jsonArrayChild4 = new JSONArray(jsonObject.getString("CategoryChildren"));
                        for (int m = 0; m < jsonArrayChild4.length(); m++){
                            jsonObject = jsonArrayChild4.getJSONObject(m);
                            SetDataFromJsonObject(jsonObject);
                            jsonArrayChild5 = new JSONArray(jsonObject.getString("CategoryChildren"));
                            for (int n = 0; n < jsonArrayChild5.length(); n++){
                                jsonObject = jsonArrayChild5.getJSONObject(n);
                                SetDataFromJsonObject(jsonObject);
                                jsonArrayChild6 = new JSONArray(jsonObject.getString("CategoryChildren"));
                                for (int p = 0; p < jsonArrayChild6.length(); p++){
                                    jsonObject = jsonArrayChild6.getJSONObject(p);
                                    SetDataFromJsonObject(jsonObject);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void SetDataFromJsonObject(JSONObject jsonObject) throws JSONException {
        Category tempModel = new Category();
        tempModel.setId(Integer.parseInt(jsonObject.getString("CategoryId")));
        tempModel.setTitle(jsonObject.getString("CategoryName"));
        tempModel.setDescription(jsonObject.getString("CategoryDescription"));
        tempModel.setParentId(Integer.parseInt(jsonObject.getString("CategoryParent")));
//                tempModel.setNumOfChild(Integer.parseInt(jsonObject.getString("NumOfChild")));
        tempModel.setCategoryChildren(jsonObject.getString("CategoryChildren"));
        tempModel.setNumOfChild(new JSONArray(tempModel.getCategoryChildren()).length());
        try {
            SetInsertTableData(tempModel);
        } catch (Exception e) {
            SetUpdateTableData(tempModel);
        }
    }
}
