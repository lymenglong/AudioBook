package com.bkic.lymenglong.audiobookbkic.download;

import android.content.Context;
import android.widget.Button;

import java.util.HashMap;

interface PresenterDownloadTaskManagerImp {

    void DownloadTaskManager(Context context, Button buttonText, String downloadUrl, String subFolderPath, String fileName, int BookId, int ChapterId);

    HashMap<String,PresenterDownloadTaskManager.DownloadingIndex> DownloadingIndexHashMap();

    Boolean isCurrentChapter(long downloadId, int chapterId);
}
