package com.bkic.lymenglong.audiobookbkic.account.showUserInfo;

import android.content.Context;

/**
 * Created by KHAIMINH2 on 3/28/2018.
 */

public interface PresenterUserInfoImp {

    void ShowAlertLogoutDialog(Context context);

    void ShowDialogUpdateUserInfo(Context context);

    void ShowDialogConfirmPassword(Context context);

    void ShowDialogUpdatePassword(Context context);
}
