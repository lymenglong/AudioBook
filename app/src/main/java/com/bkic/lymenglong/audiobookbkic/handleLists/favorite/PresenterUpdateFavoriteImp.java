package com.bkic.lymenglong.audiobookbkic.handleLists.favorite;

import android.content.Context;

public interface PresenterUpdateFavoriteImp {
    //Update Favorite Or History To Server (addHistory, addFavorite)
    void RequestUpdateToServer(String actionRequest, String userId, String bookId, String insertTime);

    //endregion
    void RequestToRemoveBookById(Context context, String userId, String bookId);

    void RequestToRemoveAllBook(String userId);
}
