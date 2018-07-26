package com.bkic.lymenglong.audiobookbkic.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.R;

public class DownloadReceiver
        extends BroadcastReceiver{

//    private static final String TAG = "Download Receiver";
    public static DownloadReceiverListener downloadReceiverListener;
    private PresenterDownloadTaskManager presenterDownloadTaskManager = new PresenterDownloadTaskManager();

    public DownloadReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //check if the broadcast message is for our Enqueued download
        long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        String downloadId = String.valueOf(referenceId);
        String keyBookId = PresenterDownloadTaskManager.KEY_BOOK_ID;
        String keyChapterId = PresenterDownloadTaskManager.KEY_CHAPTER_ID;
        if(presenterDownloadTaskManager.LoadDownloadMap(context, downloadId).isEmpty()) return;
        int bookId = Integer.parseInt(presenterDownloadTaskManager.LoadDownloadMap(context, downloadId).get(keyBookId));
        int chapterId = Integer.parseInt(presenterDownloadTaskManager.LoadDownloadMap(context, downloadId).get(keyChapterId));
//        bookId = presenterDownloadTaskManager.DownloadingIndexHashMap().get(downloadId).getBookId();
//        chapterId = presenterDownloadTaskManager.DownloadingIndexHashMap().get(downloadId).getChapterId();
        //Update data
        presenterDownloadTaskManager.UpdateDownloadTable(context, bookId, chapterId);
        presenterDownloadTaskManager.UpdateBookTable(context, bookId);
        presenterDownloadTaskManager.UpdateChapterTable(context, bookId, chapterId);
        //Remove map from shared preferences
        presenterDownloadTaskManager.RemoveDownloadMap(context, downloadId);
        //Toast Message
        String message =
                presenterDownloadTaskManager.ChapterDownloadedTitle(context, bookId, chapterId)+" "+
                presenterDownloadTaskManager.BookDownloadedTitle(context, bookId)+" "+
                context.getString(R.string.message_download_complete);
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.TOP, 25, 400);
        toast.show();
        if (downloadReceiverListener != null) {
            downloadReceiverListener.onDownloadCompleted(referenceId);
        }
    }

    public interface DownloadReceiverListener {
        void onDownloadCompleted(long downloadId);
    }
}