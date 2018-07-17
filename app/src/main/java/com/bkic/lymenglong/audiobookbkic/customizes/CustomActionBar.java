package com.bkic.lymenglong.audiobookbkic.customizes;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.bkic.lymenglong.audiobookbkic.R;


public class CustomActionBar implements CustomActionBarImp, View.OnClickListener{
    private View imBack;
    private Activity activity;
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
        View imRefresh = activity.findViewById(R.id.imRefresh);
        TextView tvToolbar = activity.findViewById(R.id.tvToolbar);

        tvToolbar.setText(text);
        if(hasRefresh) {
            imRefresh.setVisibility(View.VISIBLE);
        }else {
            imRefresh.setVisibility(View.GONE);
        }
        imBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == imBack) {
            activity.onBackPressed();
        }
    }
}
