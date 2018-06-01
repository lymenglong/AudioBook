package com.bkic.lymenglong.audiobookbkic.account.login;

import org.json.JSONException;

public interface ViewLoginImp {
    void LoginSuccess(String message) throws JSONException;
    void LoginFailed(String message);
}
