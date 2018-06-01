package com.bkic.lymenglong.audiobookbkic.reading;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.R;

import static com.bkic.lymenglong.audiobookbkic.utils.Const.HttpUrl_InsertFavorite;
import static com.bkic.lymenglong.audiobookbkic.utils.Const.HttpUrl_InsertHistory;


public class ViewReading extends AppCompatActivity implements ViewReadingImp, View.OnClickListener{
    PresenterViewReading presenterViewReading = new PresenterViewReading(this);
    private int idChapter;
    private String titleChapter, detailReadingHtml, detailReading;
    private ScrollView scrollView;
    private final DisplayMetrics dm = new DisplayMetrics();
    int offset;
    private Button btnFavorite;
    private Activity viewReadingActivity = ViewReading.this;
    private boolean existedContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reading);
        getDataFromIntent();
        setTitle(titleChapter);
        initView();
        initObject();
        initListener();
    }

    private void initListener() {
        btnFavorite.setOnClickListener(this);
    }



    private void initObject() {
        detailReading = String.valueOf(Html.fromHtml(detailReadingHtml));
        TextView tvReading = findViewById(R.id.tvDetailReading);
        if (!detailReadingHtml.trim().isEmpty()) {
            tvReading.setText(detailReading);
            existedContent = true;
        } else {
            tvReading.setText(getString(R.string.no_data));
            existedContent = false;
        }
        Log.d("scroll", String.valueOf(scrollView.getChildAt(0).getHeight()));
        Log.d("heightText", String.valueOf(getTextHeight(tvReading)) + " and "
                + String.valueOf(getScreenHeight())+ " num " + String.valueOf(numPage
                    (getTextHeight(tvReading), getScreenHeight() - 480)));
    }

    /**
     * Lấy dữ liệu từ intent
     */
    private void getDataFromIntent() {
        idChapter = getIntent().getIntExtra("idChapter",-1);
        titleChapter = getIntent().getStringExtra("titleChapter");
        detailReadingHtml = getIntent().getStringExtra("content");
        offset = getIntent().getIntExtra("offset", -1);
    }





    private void initView() {
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar),
                ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        CustomActionBar actionBar = new CustomActionBar();
        detailReading = String.valueOf(Html.fromHtml(detailReadingHtml));
        scrollView = findViewById(R.id.scrollView);
        actionBar.eventToolbar(this, titleChapter, false);
        btnFavorite = findViewById(R.id.btn_add_favorite_book);

    }

    /*
     * Lấy độ cao của textview
     *
     * @param text
     * @return
     */
    private int getTextHeight(TextView text) {
        text.measure(0, 0);
        return text.getMeasuredHeight();
    }

    /*
     * Lấy độ cao của màn hình thiết bị
     *
     * @return
     */
    private int getScreenHeight() {
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /*
     * Lấy chiều rộng của thiết bị
     *
     * @return
     */
/*    private int getScreenWidth() {
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }*/

    /**
     * Số trang của văn bản
     *
     * @param textHeight   chiều cao textview
     * @param screenHeight chiều cao màn hình
     * @return mỗi màn hình là 1 trang, dựa vào height xác định số trang của văn bản
     */
    private int numPage(int textHeight, int screenHeight) {
        int num;
        num = textHeight / screenHeight;
        if (textHeight > screenHeight * num) {
            num += 1;
        }
        return num;
    }

    @Override
    public void PostDataSuccess(String success) {
        Toast.makeText(viewReadingActivity, success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void PostDataFailed(String message) {
        Toast.makeText(viewReadingActivity, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (existedContent) {
            presenterViewReading.postHistoryDataToServer(HttpUrl_InsertHistory,idChapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_favorite_book:
                presenterViewReading.postFavoriteDataToServer(HttpUrl_InsertFavorite,idChapter);
                break;
        }

    }
}
