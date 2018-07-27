package com.bkic.lymenglong.audiobookbkic.download;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.utils.Const;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.content.Context.DOWNLOAD_SERVICE;

public class PresenterDownloadTaskManager implements PresenterDownloadTaskManagerImp {

    private static final String TAG = "Download Task";
    private Context context;
    private String downloadUrl;
    private String subFolderPath;
    private int BookId, ChapterId;
    private Button buttonText;
    private static HashMap<String,DownloadingIndex> downloadingIndexHashMap = new HashMap<>();
    public static String KEY_CHAPTER_ID = "CHAPTER_ID";
    public static String KEY_BOOK_ID = "BOOK_ID";
    private static String NAME_SHARED_PREFERENCES = "DOWNLOAD_TASK";

    public class DownloadingIndex {
        private int bookId;
        private int chapterId;

        DownloadingIndex(int bookId, int chapterId) {
            this.bookId = bookId;
            this.chapterId = chapterId;
        }

        public int getBookId() {
            return bookId;
        }

        public int getChapterId() {
            return chapterId;
        }
    }

    @Override
    public void DownloadTaskManager(Context context, Button buttonText, String downloadUrl, String subFolderPath, String fileName, int BookId, int ChapterId) {
        this.context = context;
        this.downloadUrl = downloadUrl;
        this.BookId = BookId;
        this.ChapterId = ChapterId;
        this.buttonText = buttonText;
        this.subFolderPath = subFolderPath; //subFolderPath we use the name of each book

        String downloadFileName = fileName.replace(" ", "_") + ".mp3";
        Log.d(TAG, downloadFileName);

        //Start Downloading Task
        new DownloadingTask().execute();
    }


    @SuppressLint("StaticFieldLeak")
    public class DownloadingTask extends AsyncTask<Void, Void, Boolean> {

        File apkStorage = null;
        File apkSubStorage = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            buttonText.setEnabled(false);
            buttonText.setText(R.string.downloadStarted);//Set Button Text when download started
            //Start Background Service
            if(!isMyServiceRunning(MyDownloadService.class))
                context.startService(new Intent(context, MyDownloadService.class));
        }

        @Override
        protected void onPostExecute(Boolean bDownloaded) {
            super.onPostExecute(bDownloaded);
            if(bDownloaded){
                buttonText.setEnabled(false);
                buttonText.setText(context.getString(R.string.downloadCompleted));//If Download completed then change button text
                String ms =
                        ChapterDownloadedTitle(context,BookId,ChapterId)+
                                " "+ BookDownloadedTitle(context,BookId)+
                                " "+context.getString(R.string.message_download_complete);
                Toast.makeText(context, ms, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
                //Get File if SD card is present
                /*if (new CheckForSDCard().isSDCardPresent()) apkStorage = new File(
                        Environment.getExternalStorageDirectory() + "/"
                                + Utils.downloadDirectory + "/" + subFolderPath);*/
            if (new CheckForSDCard().isSDCardPresent())
                apkStorage = new File(
                    Environment.getExternalStorageDirectory() + "/"
                            + Utils.downloadDirectory);
            else
                Toast.makeText(context, R.string.error_message_no_sd_card, Toast.LENGTH_SHORT).show();

            //Check permission for api 24 or higher
            int code = context.getPackageManager().checkPermission(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    context.getPackageName());
            if (code == PackageManager.PERMISSION_GRANTED) {
                //If File is not present create directory
                boolean isDirectoryCreated=apkStorage.exists();
                if (!isDirectoryCreated) {
                    isDirectoryCreated = apkStorage.mkdir();
                    Log.d(TAG, "Directory Created.");
                }
                if(isDirectoryCreated) {
                    File _nomedia = new File(apkStorage+ "/"+".nomedia");
                    Boolean _isNoMediaCreated = false;
                    if(!_nomedia.exists()){
                        try {
                            _isNoMediaCreated = _nomedia.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(_isNoMediaCreated) Log.d(TAG, ".nomedia is created");
                    }
                    //Create subdirectory
                    apkSubStorage = new File(
                            Environment.getExternalStorageDirectory() + "/"
                                    + Utils.downloadDirectory + "/" + subFolderPath);
                    boolean isSubDirectoryCreated = apkSubStorage.exists();
                    if(!isSubDirectoryCreated) {
                        isSubDirectoryCreated = apkSubStorage.mkdir();
                        Log.d(TAG, "Sub Directory: " + subFolderPath + " is created");
                    }
                    if(isSubDirectoryCreated){

                        //Check if file exists old file
                        String filePath = Environment.getExternalStorageDirectory()+"/"+Utils.downloadDirectory+"/"+BookId+"/"+ChapterId+".mp3"; //i.e. /sdcard/AudioBookBKIC/2162/2168.mp3
                        File file = new File(filePath);
                        //delete old file
                        if(file.exists()){
                            UpdateDownloadTable(context,BookId,ChapterId);
                            UpdateBookTable(context,BookId);
                            UpdateChapterTable(context,BookId,ChapterId);
                            return true;
                        }
                        //download file using download manager
                        long downloadId = DownloadData(Uri.parse(downloadUrl), BookId, ChapterId);
                        DownloadingIndex index = new DownloadingIndex
                                (
                                        BookId,
                                        ChapterId
                                );
                        downloadingIndexHashMap.put(String.valueOf(downloadId),index);
                        HashMap <String, String> inputMap = new HashMap<>();
                        inputMap.put(KEY_BOOK_ID, String.valueOf(BookId));
                        inputMap.put(KEY_CHAPTER_ID, String.valueOf(ChapterId));
                        String keyMap = String.valueOf(downloadId);
                        SaveDownloadMap(context, keyMap, inputMap);
                    }
                }
            }
            return false;

        }
    }

    @Override
    public void SaveDownloadMap(Context context, String keyMap, HashMap<String, String> inputMap){
        SharedPreferences pSharedPref = context.getSharedPreferences(NAME_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (pSharedPref != null){
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
//            editor.remove("My_map").apply();
            editor.putString(keyMap, jsonString);
            editor.apply();
        }
    }
    @Override
    public void RemoveDownloadMap(Context context, String keyMap){
        SharedPreferences pSharedPref = context.getSharedPreferences(NAME_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (pSharedPref != null){
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove(keyMap).apply();
            editor.commit();
        }
    }
    @Override
    public Map<String,String> LoadDownloadMap(Context context, String keyMap){
        Map<String,String> outputMap = new HashMap<>();
        SharedPreferences pSharedPref = context.getSharedPreferences(NAME_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString(keyMap, (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String key = keysItr.next();
                    String value = (String) jsonObject.get(key);
                    outputMap.put(key, value);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return outputMap;
    }

    private String GetDownloadTitle(int bookId, int chapterId){
        DBHelper dbHelper = new DBHelper(context, Const.DB_NAME, null, Const.DB_VERSION);
        Cursor cursor = dbHelper.GetData(
                "SELECT " +
                        "chapter.ChapterTitle, book.bookTitle " +
                        "FROM chapter, book " +
                        "WHERE " +
                        "book.BookId = chapter.bookId " +
                        "AND " +
                        "chapter.BookId = '"+bookId+"' " +
                        "AND " +
                        "chapter.ChapterId = '"+chapterId+"' " +
                        ";"
        );
        if(cursor.moveToFirst())
            return cursor.getString(0)+" - "+cursor.getString(1);
        return null;
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public HashMap<String,DownloadingIndex> DownloadingIndexHashMap(){
        return downloadingIndexHashMap;
    }

    @Override
    public Boolean isCurrentChapter(long downloadId, int chapterId){
        return downloadingIndexHashMap.get(String.valueOf(downloadId)).getChapterId()==chapterId;
    }

    @Override
    public String BookDownloadedTitle(Context context, int bookId){
        String bookTitle = null;
        DBHelper dbHelper = new DBHelper(context, Const.DB_NAME,null, Const.DB_VERSION);
        String SELECT =
                "SELECT BookTitle " +
                        "From book " +
                        "WHERE BookId = '"+bookId+"'" +
                        ";";
        Cursor cursor = dbHelper.GetData(SELECT);
        if(cursor.moveToFirst()) bookTitle = cursor.getString(0);
        return bookTitle;
    }

    @Override
    public String ChapterDownloadedTitle(Context context, int bookId, int chapterId){
        String bookTitle = null;
        DBHelper dbHelper = new DBHelper(context, Const.DB_NAME,null, Const.DB_VERSION);
        String SELECT =
                "SELECT ChapterTitle " +
                        "From chapter " +
                        "WHERE " +
                        "BookId = '"+bookId+"' " +
                        "AND " +
                        "ChapterId = '"+chapterId+"'"+
                        ";";
        Cursor cursor = dbHelper.GetData(SELECT);
        if(cursor.moveToFirst()) bookTitle = cursor.getString(0);
        return bookTitle;
    }

    @Override
    public void UpdateBookTable(Context context, int bookId) {
        DBHelper dbHelper = new DBHelper(context, Const.DB_NAME,null, Const.DB_VERSION);
        String UPDATE_STATUS =
                "UPDATE " +
                        "book " +
                        "SET " +
                        "BookStatus = '1' " +
                        "WHERE " +
                        "BookId = '"+bookId+"'" +
                        ";"
                ;
        dbHelper.QueryData(UPDATE_STATUS);
        dbHelper.close();
    }

    @Override
    public void UpdateChapterTable(Context context, int bookId, int chapterId){
        DBHelper dbHelper = new DBHelper(context, Const.DB_NAME,null, Const.DB_VERSION);
        String UPDATE_STATUS =
                "UPDATE " +
                        "chapter " +
                        "SET " +
                        "ChapterStatus = '1' " +
                        "WHERE " +
                        "BookId = '"+bookId+"' " +
                        "AND " +
                        "ChapterId = '"+chapterId+"'"
                ;
        dbHelper.QueryData(UPDATE_STATUS);
    }
    @Override
    public void UpdateDownloadTable(Context context, int bookId, int chapterId) {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat =
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String insertTime = simpledateformat.format(calendar.getTime());
        DBHelper dbHelper = new DBHelper(context, Const.DB_NAME,null, Const.DB_VERSION);
        try {
            String INSERT_STATUS =
                    "INSERT INTO downloadStatus " +
                            "VALUES " +
                            "(" +
                            "'"+chapterId+"', "+
                            "'"+bookId+"', "+
                            "'"+1+"', "+ //downloaded
                            "'"+insertTime+"'" +
                            ")" +
                            ";" ;
            dbHelper.QueryData(INSERT_STATUS);
        } catch (Exception e) {
            Log.e(TAG, "UpdateDownloadTable: " +e.getMessage());
            String UPDATE_STATUS =
                    "UPDATE downloadStatus " +
                            "SET " +
                            "DownloadedStatus = '1'," +
                            "InsertTime = '"+insertTime+"' " +
                            "WHERE " +
                            "ChapterId = '"+chapterId+"' " +
                            "AND " +
                            "BookId = '"+bookId+"'" +
                            ";" ;
            dbHelper.QueryData(UPDATE_STATUS);
        }
        dbHelper.close();
    }

    private long DownloadData (Uri uri, int bookId, int chapterId ) {

        long downloadReference;

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(uri);

        String sTitle = GetDownloadTitle(bookId, chapterId);
        String sDescription = "Sách Đang Tải Xuống";

        //Setting title of request
        request.setTitle(sTitle);

        //Setting description of request
        request.setDescription(sDescription);

        //Check if file exists delete old file
        String filePath = Environment.getExternalStorageDirectory()+"/"+Utils.downloadDirectory+"/"+bookId+"/"+chapterId+".mp3"; //i.e. /sdcard/AudioBookBKIC/2162/2168.mp3
        boolean isFileDeleted = false;
        File file = new File(filePath);
        //delete old file
        if(file.exists()) isFileDeleted = file.delete();
        Log.e(TAG, filePath+" Deleted: "+ isFileDeleted);

        //Set the local destination for the downloaded file to a path within the application's external files directory
//          request.setDestinationInExternalFilesDir(MainActivityDownloadManager.this, Environment.DIRECTORY_DOWNLOADS,"AndroidTutorialPoint.mp3");
        request.setDestinationInExternalPublicDir(Utils.downloadDirectory+"/"+bookId,chapterId+".mp3");

        //Enqueue download and save the referenceId
        assert downloadManager != null;
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }

/*    private void RemoveAllDownloading(){
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById (DownloadManager.STATUS_FAILED|DownloadManager.STATUS_PENDING|DownloadManager.STATUS_RUNNING);
        DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        assert dm != null;
        Cursor cursor = dm.query(query);
        while(cursor.moveToNext()) {
            // Here you have all the downloades list which are running, failed, pending
            // and for abort your downloads you can call the `dm.remove(downloadsID)` to cancel and delete them from download manager.
            dm.remove(cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID)));
            Toast.makeText(context, cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID))+"\n", Toast.LENGTH_SHORT).show();
        }
    }*/

}

