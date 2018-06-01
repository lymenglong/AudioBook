package com.bkic.lymenglong.audiobookbkic.handleLists.favorite;


import android.app.Activity;

import java.util.HashMap;

public interface PresenterShowListFavoriteImp {
    void GetSelectedResponse(Activity activity, HashMap<String,String> ResultHash, String httpUrl);
}
