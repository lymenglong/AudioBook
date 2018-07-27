package com.bkic.lymenglong.audiobookbkic.account.login;

import android.content.Context;

public interface PresenterLoginImp {
    void Login(String email, String password);

//    void UserDetail(String email);

    void UserDetail(Context context, String email);
}
