package com.bkic.lymenglong.audiobookbkic.handleLists.history;

import android.content.Context;

public interface PresenterUpdateHistoryImp {
    //Update Favorite Or History To Server (addHistory, addFavorite)
    void RequestUpdateToServer(String actionRequest, String userId, String bookId, String insertTime);

    void RequestToRemoveBookById(Context context, String userId, String bookId);

    void RequestToRemoveAllBook(String userId);
}
