package com.bkic.lymenglong.audiobookbkic.download;

public class DownloadedStatus {
    private int bookId;
    private int chapterId;
    private int downloadedState;

    public DownloadedStatus( int chapterId, int bookId, int downloadedState) {
        this.bookId = bookId;
        this.chapterId = chapterId;
        this.downloadedState = downloadedState;
    }

    public int getBookId() {
        return bookId;
    }

    public int getChapterId() {
        return chapterId;
    }

    public int getDownloadedState() {
        return downloadedState;
    }
}