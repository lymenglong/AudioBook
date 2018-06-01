package com.bkic.lymenglong.audiobookbkic.reading;

public interface PresenterViewReadingImp {

    void postHistoryDataToServer(String HttpUrl, int idChapter);

    void postFavoriteDataToServer(String favoriteURL, int idChapter);
}
