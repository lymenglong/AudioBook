package com.bkic.lymenglong.audiobookbkic.player;

public interface PlayerImp {

    Boolean initCheckChapterDownloadStatus();

    void PrepareChapter();

    void NextMedia();

    void AddReviewBookToServer();

    void AddReviewChapterToServer();

    void UpdateHistoryData();

    void UpdateHistorySuccess(String message);

    void UpdateHistoryFailed(String message);

    void UpdateFavoriteFailed(String message);

    void UpdateFavoriteSuccess(String message);

    void UpdateReviewSuccess(String message);

    void UpdateReviewFailed(String message);

    void UpdateChapterStatus();

    void UpdateChapterReviewSuccess(String message);

    void UpdateReviewTable();

    void UpdateChapterReviewFailed(String message);

    void MediaPlayerOnCompletion();
}
