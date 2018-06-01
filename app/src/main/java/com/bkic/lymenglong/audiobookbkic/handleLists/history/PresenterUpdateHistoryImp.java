package com.bkic.lymenglong.audiobookbkic.handleLists.history;

public interface PresenterUpdateHistoryImp {
    //Update Favorite Or History To Server (addHistory, addFavorite)
    void RequestUpdateToServer(String actionRequest, String userId, String bookId, String insertTime);

    void RequestToRemoveBookById(String userId, String bookId);

    void RequestToRemoveAllBook(String userId);
}
