package com.bkic.lymenglong.audiobookbkic.overrideTalkBack;

import android.view.View;

interface PresenterOverrideTalkBackInterface {
    void DisableTouchForTalkBack(View view);

    //fix talk back read incorrectly

    String getConvertedDuration(long milliseconds);

    String DurationContentDescription(long milliseconds);
}
