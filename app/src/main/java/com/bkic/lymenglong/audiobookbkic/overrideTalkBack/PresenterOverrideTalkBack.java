package com.bkic.lymenglong.audiobookbkic.overrideTalkBack;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;

import com.bkic.lymenglong.audiobookbkic.R;

import java.util.List;

public class PresenterOverrideTalkBack
        implements View.OnHoverListener, View.OnTouchListener, PresenterOverrideTalkBackInterface {

    private static final String TAG = "OverrideTalkBack";
    private Context context;
    public PresenterOverrideTalkBack(Context context) {
        this.context = context;
    }

/*    @Override
    public boolean onHover(View v, MotionEvent event) {
        //Move AccessibilityManager object to the constructor
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        assert am != null;
        if (am.isTouchExplorationEnabled()) {
            return onTouch(v,event);
        } else {
            return onHover(v,event);
        }
    }*/

/*    @Override
    public boolean onHover(View v, MotionEvent event) {
        //Move AccessibilityManager object to the constructor
        if (talkBackEnable(context)) {
            return onTouch(v,event);
        } else {
            return onHover(v,event);
        }
    }
*/
    @Override
    public boolean onHover(View v, MotionEvent event) {
        //Move AccessibilityManager object to the constructor
        return talkBackEnable(context) && onTouch(v, event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_HOVER_ENTER:
                //Your code to do something
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_HOVER_MOVE:
                //Your code to do something
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_HOVER_EXIT:
                //Your code to do something
                break;

        }
        return true;
    }

    @Override
    public void DisableTouchForTalkBack(View view) {
       view.setOnHoverListener(this);
    }


    private static final String TALKBACK_SETTING_ACTIVITY_NAME = "com.android.talkback.TalkBackPreferencesActivity";

    @SuppressLint("ObsoleteSdkInt")
    public boolean talkBackEnable(Context context) {
        boolean enable = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                AccessibilityManager manager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
                assert manager != null;
                List<AccessibilityServiceInfo> serviceList = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN);
                for (AccessibilityServiceInfo serviceInfo : serviceList) {
                    String name = serviceInfo.getSettingsActivityName();
                    if (!TextUtils.isEmpty(name) && name.equals(TALKBACK_SETTING_ACTIVITY_NAME)) {
                        enable = true;
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "accessibilityEnable: ");
            }
        }
        return enable;
    }


    @Override
    public String getConvertedDuration(long milliseconds) {
        long sec = (milliseconds / 1000) % 60;
        long min = (milliseconds / (60 * 1000)) % 60;
        long hour = milliseconds / (60 * 60 * 1000);

        String s = (sec < 10) ? "0" + sec : "" + sec;
        String m = (min < 10) ? "0" + min : "" + min;
        String h = "" + hour;

        String time;
        if (hour > 0) {
            time = h + ":" + m + ":" + s;
        } else {
            time = m + ":" + s;
        }
        return time;
    }


    @Override
    //fix talk back read incorrectly
    public String DurationContentDescription(long milliseconds){
        long sec = (milliseconds / 1000) % 60;
        long min = (milliseconds / (60 * 1000)) % 60;
        long hour = milliseconds / (60 * 60 * 1000);

        //Format HH:mm:ss
        /*String s = (sec < 10) ? "0" + sec : "" + sec;
        String m = (min < 10) ? "0" + min : "" + min;
        String h = "" + hour;*/

        String time;
        if (hour > 0) time = context.getResources().getString(R.string.last_period_time_hour,hour,min,sec);
        else if (min > 0) time =context.getResources().getString(R.string.last_period_time_min,min,sec);
        else time = context.getResources().getString(R.string.last_period_time_sec,sec);

        return time;
    }

}
