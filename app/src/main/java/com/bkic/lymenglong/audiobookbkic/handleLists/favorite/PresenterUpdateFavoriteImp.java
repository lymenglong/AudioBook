package com.bkic.lymenglong.audiobookbkic.handleLists.favorite;

public interface PresenterUpdateFavoriteImp {
    //Update Favorite Or History To Server (addHistory, addFavorite)
    void RequestUpdateToServer(String actionRequest, String userId, String bookId, String insertTime);

    void RequestToRemoveBookById(String userId, String bookId);

    void RequestToRemoveAllBook(String userId);
}
