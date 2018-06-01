package com.bkic.lymenglong.audiobookbkic.download;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.R;

import java.io.File;

public class MainActivityDownload extends AppCompatActivity implements View.OnClickListener {
    private Button downloadPdf;
    private Button downloadDoc;
    private Button downloadZip;
    private Button downloadVideo;
    private Button downloadMp3;
    private Button openDownloadedFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_download);
        initViews();
        setListeners();
    }

    //Initialize al Views
    private void initViews() {
        downloadPdf = findViewById(R.id.downloadPdf);
        downloadDoc = findViewById(R.id.downloadDoc);
        downloadZip = findViewById(R.id.downloadZip);
        downloadVideo = findViewById(R.id.downloadVideo);
        downloadMp3 = findViewById(R.id.downloadMp3);
        openDownloadedFolder = findViewById(R.id.openDownloadedFolder);

    }

    //Set Listeners to Buttons
    private void setListeners() {
        downloadPdf.setOnClickListener(this);
        downloadDoc.setOnClickListener(this);
        downloadZip.setOnClickListener(this);
        downloadVideo.setOnClickListener(this);
        downloadMp3.setOnClickListener(this);
        openDownloadedFolder.setOnClickListener(this);
    }

    //Module checkIfAlreadyhavePermission() is implemented as :
    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    //Module requestForSpecificPermission() is implemented as :
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions
                (
                        this,
                        new String[]
                                {
//                                        Manifest.permission.GET_ACCOUNTS,
//                                        Manifest.permission.RECEIVE_SMS,
//                                        Manifest.permission.READ_SMS,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                },
                        101
                );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    new DownloadTask(MainActivityDownload.this, downloadMp3, Utils.downloadMp3Url);
                } else {
                    //not granted
                    Toast.makeText(this, "Check", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View view) {
        //Before starting any download check internet connection availability
        switch (view.getId()) {
            case R.id.downloadPdf:
                if (isConnectingToInternet())
                    new DownloadTask(MainActivityDownload.this, downloadPdf, Utils.downloadPdfUrl);
                else
                    Toast.makeText(MainActivityDownload.this, "Oops!! There is no internet connection. Please enable internet connection and try again.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.downloadDoc:
                if (isConnectingToInternet())
                    new DownloadTask(MainActivityDownload.this, downloadDoc, Utils.downloadDocUrl);
                else
                    Toast.makeText(MainActivityDownload.this, "Oops!! There is no internet connection. Please enable internet connection and try again.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.downloadZip:
                if (isConnectingToInternet())
                    new DownloadTask(MainActivityDownload.this, downloadZip, Utils.downloadZipUrl);
                else
                    Toast.makeText(MainActivityDownload.this, "Oops!! There is no internet connection. Please enable internet connection and try again.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.downloadVideo:
                if (isConnectingToInternet())
                    new DownloadTask(MainActivityDownload.this, downloadVideo, Utils.downloadVideoUrl);
                else
                    Toast.makeText(MainActivityDownload.this, "Oops!! There is no internet connection. Please enable internet connection and try again.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.downloadMp3:
                if (isConnectingToInternet()) {
//                    new DownloadTask(MainActivityDownload.this, downloadMp3, Utils.downloadMp3Url);
                    //check if my app has permission and than request if it does not have permission
                    int MyVersion = Build.VERSION.SDK_INT;
                    if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        if (!checkIfAlreadyhavePermission()) {
                            requestForSpecificPermission();
                        }
                    }
                }else
                    Toast.makeText(MainActivityDownload.this, "Oops!! There is no internet connection. Please enable internet connection and try again.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.openDownloadedFolder:
                openDownloadedFolder();
                break;

        }

    }


    //Open downloaded folder
    private void openDownloadedFolder() {
        //First check if SD Card is present or not
        if (new CheckForSDCard().isSDCardPresent()) {

            //Get Download Directory File
            File apkStorage = new File(
                    Environment.getExternalStorageDirectory() + "/"
                            + Utils.downloadDirectory);

            //If file is not present then display Toast
            if (!apkStorage.exists())
                Toast.makeText(MainActivityDownload.this, "Right now there is no directory. Please download some file first.", Toast.LENGTH_SHORT).show();

            else {

                //If directory is present Open Folder

                /*Note: Directory will open only if there is a app to open directory like File Manager, etc.  **/

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                        + "/" + Utils.downloadDirectory);
                intent.setDataAndType(uri, "file/*");
                startActivity(Intent.createChooser(intent, "Open Download Folder"));
            }

        } else
            Toast.makeText(MainActivityDownload.this, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

    }

    //Check if internet is present or not
    private boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


}
