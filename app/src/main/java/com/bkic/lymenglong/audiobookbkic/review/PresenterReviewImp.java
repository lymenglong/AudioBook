package com.bkic.lymenglong.audiobookbkic.review;

import android.app.Activity;
import android.content.Context;

public interface PresenterReviewImp {
    void ReviewBookDialog4(Context context);

    void ReviewBookDialog2(Context context);

    void ReviewBookDialog(Context context);

    void ReviewBookDialog3(Context context);

    void RequestReviewBook(Activity activity, int userId, int bookId, int rateNumber, String review);

    void RequestGetReviewData(Activity activity, int bookId);

    void RequestAddChapterReview(Activity activity, int userId, int bookId, int chapterId, int rateNumber, String review);

    void RequestGetReviewChapter(Activity activity, int bookId, int chapterId);
}
