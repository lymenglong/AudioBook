package com.bkic.lymenglong.audiobookbkic.download;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.utils.Const;
import com.bkic.lymenglong.audiobookbkic.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask{

    private static final String TAG = "Download Task";
    private Context context;
    private Button buttonText;
    private String downloadUrl, downloadFileName, subFolderPath = "";
    private int BookId, ChapterId;


    DownloadTask(Context context, Button buttonText, String downloadUrl) {
        this.context = context;
        this.buttonText = buttonText;
        this.downloadUrl = downloadUrl;
        downloadFileName = downloadUrl.replace(Utils.mainUrl, "");//Create file name by picking download file name from URL
        Log.e(TAG, downloadFileName);

        //Start Downloading Task
        new DownloadingTask().execute();
    }
    public DownloadTask(Context context, Button buttonText, String downloadUrl, String subFolderPath, String fileName, int BookId, int ChapterId) {
        this.context = context;
        this.buttonText = buttonText;
        this.downloadUrl = downloadUrl;
        this.BookId = BookId;
        this.ChapterId = ChapterId;
        this.subFolderPath = subFolderPath; //subFolderPath we use the name of each book

        downloadFileName = fileName.replace(" ","_")+".mp3";
        Log.e(TAG, downloadFileName);

        //Start Downloading Task
        new DownloadingTask().execute();
    }

    @SuppressLint("StaticFieldLeak")
    public class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            buttonText.setEnabled(false);
            buttonText.setText(R.string.downloadStarted);//Set Button Text when download started
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
//                URL url = new URL(downloadUrl);//Create Download URl
//                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                Uri uri = Uri.parse(downloadUrl);
                URL url = new URL(uri.toString());
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());

                }


                //Get File if SD card is present
                if (new CheckForSDCard().isSDCardPresent()) apkStorage = new File(
                        Environment.getExternalStorageDirectory() + "/"
                                + Utils.downloadDirectory + "/" + subFolderPath);
                else
                    Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

                //Check permission for api 24 or higher
                int code = context.getPackageManager().checkPermission(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        context.getPackageName());
                if (code == PackageManager.PERMISSION_GRANTED) {
                    //If File is not present create directory
                    boolean isDirectoryCreated=apkStorage.exists();
                    if (!isDirectoryCreated) {
                        isDirectoryCreated = apkStorage.mkdir();
                        Log.e(TAG, "Directory Created.");
                    }
                    if(isDirectoryCreated) {
                        // do something
                        outputFile = new File(apkStorage, downloadFileName);//Create Output file in Main File
                        Boolean isNewFileCreated;
                        //Create New File if not present
                        if (!outputFile.exists()) {
                            isNewFileCreated = outputFile.createNewFile();
                            Log.e(TAG, "File Created: " +isNewFileCreated);
                        }
                    }

                } else {
                    outputFile = null;
                    return null;
                }
                ////

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                    UpdateChapterTable(BookId, ChapterId);
                    UpdateBookTable(BookId);
                    buttonText.setEnabled(false);
                    buttonText.setText(R.string.downloadCompleted);//If Download completed then change button text
                } else {
                    buttonText.setText(R.string.downloadFailed);//If download failed change button text
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            buttonText.setEnabled(true);
                            buttonText.setText(R.string.downloadAgain);//Change button text again after 3sec
                        }
                    }, 3000);

                    Log.e(TAG, "Download Failed");

                }
            } catch (Exception e) {
                e.printStackTrace();

                //Change button text if exception occurs
                buttonText.setText(R.string.downloadFailed);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonText.setEnabled(true);
                        buttonText.setText(R.string.downloadAgain);
                    }
                }, 3000);
                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());

            }


            super.onPostExecute(result);
        }
    }

    private void UpdateBookTable(int bookId) {
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

    private void UpdateChapterTable(int bookId, int chapterId) {
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
        dbHelper.close();
    }
}

