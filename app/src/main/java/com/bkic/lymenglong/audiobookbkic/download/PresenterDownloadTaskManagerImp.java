package com.bkic.lymenglong.audiobookbkic.download;

import android.content.Context;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;

interface PresenterDownloadTaskManagerImp {

    void DownloadTaskManager(Context context, Button buttonText, String downloadUrl, String subFolderPath, String fileName, int BookId, int ChapterId);

    void SaveDownloadMap(Context context, String keyMap, HashMap<String, String> inputMap);

    void RemoveDownloadMap(Context context, String keyMap);

    Map<String,String> LoadDownloadMap(Context context, String keyMap);

    HashMap<String,PresenterDownloadTaskManager.DownloadingIndex> DownloadingIndexHashMap();

    Boolean isCurrentChapter(long downloadId, int chapterId);

    String BookDownloadedTitle(Context context, int bookId);

    String ChapterDownloadedTitle(Context context, int bookId, int chapterId);

    void UpdateBookTable(Context context, int bookId);

    void UpdateChapterTable(Context context, int bookId, int chapterId);

    void UpdateDownloadTable(Context context, int bookId, int chapterId);
}
