package com.bkic.lymenglong.audiobookbkic.customizes;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.bkic.lymenglong.audiobookbkic.R;


public class CustomActionBar implements CustomActionBarImp, View.OnClickListener{
    private View imBack;
    private Activity activity;
    private TextView tvToolbar;
    /**
     * Custom actionbar cho các activity với title và right btn
     * @param activity activity cần dùng bar
     * @param text title header
     * @param hasRefresh true nếu muốn có thêm btn refresh
     */

    @Override
    public void eventToolbar(Activity activity, String text, boolean hasRefresh) {
        this.activity = activity;
        imBack = activity.findViewById(R.id.imBack);
        //Không cho phép talk back doc nút back
        ViewCompat.setImportantForAccessibility(activity.getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        View imRefresh = activity.findViewById(R.id.imRefresh);
        tvToolbar = activity.findViewById(R.id.tvToolbar);

        tvToolbar.setText(text);
        if(hasRefresh) {
            imRefresh.setVisibility(View.VISIBLE);
        }else {
            imRefresh.setVisibility(View.GONE);
        }
        imBack.setOnClickListener(this);
    }
    @Override
    public void searchToolbar(Activity activity, boolean bShow){
        this.activity = activity;
        imBack = activity.findViewById(R.id.imBack);
        tvToolbar = activity.findViewById(R.id.tvToolbar);
        EditText edtToolbar = activity.findViewById(R.id.edt_toolbar);
        View imMic = activity.findViewById(R.id.im_micro);
        View imSearch = activity.findViewById(R.id.imSearch);
        if(bShow){
            imBack.setVisibility(View.VISIBLE);
            imMic.setVisibility(View.VISIBLE);
            imSearch.setVisibility(View.VISIBLE);
            tvToolbar.setVisibility(View.GONE);
            edtToolbar.setVisibility(View.VISIBLE);
        } else {
            imBack.setVisibility(View.VISIBLE);
            imMic.setVisibility(View.VISIBLE);
            imSearch.setVisibility(View.VISIBLE);
            tvToolbar.setVisibility(View.VISIBLE);
            edtToolbar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if(view == imBack) {
            activity.onBackPressed();
        }
    }
}
