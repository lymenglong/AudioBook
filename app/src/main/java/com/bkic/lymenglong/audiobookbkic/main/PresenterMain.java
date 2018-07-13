package com.bkic.lymenglong.audiobookbkic.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.https.HttpServicesClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PresenterMain implements PresenterMainImp {
    private MainActivity mainActivity;
//    private ProgressDialog pDialog;

    public PresenterMain(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void GetHttpResponse(String httpUrl) {
        new GetHttpResponse(mainActivity).execute(httpUrl);
    }

    @Override
    public void ShowDialogExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        //        builder.setTitle("Exit");
        builder.setMessage("Bạn có muốn thoát ứng dụng không?");
        builder.setCancelable(false);
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCompat.finishAffinity(mainActivity);
                System.exit(0);
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //region JSON parse class started from here.
    @SuppressLint("StaticFieldLeak")
    private class GetHttpResponse extends AsyncTask<String, Void, Void> {

        Context context;
        String JSonResult;

        GetHttpResponse(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... httpUrl) {
            // Passing HTTP URL to HttpServicesClass Class.

            HttpServicesClass httpServicesClass = new HttpServicesClass(httpUrl[0]);
            try {
                httpServicesClass.ExecutePostRequest();

                if (httpServicesClass.getResponseCode() == 200) {
                    JSonResult = httpServicesClass.getResponse();

                    if (JSonResult != null) {
                        JSONArray jsonArray;

                        try {
                            jsonArray = new JSONArray(JSonResult);

                            JSONObject jsonObject;

                            for (int i = 0; i < jsonArray.length(); i++) {

                                jsonObject = jsonArray.getJSONObject(i);

                                // Adding data TO IdList Array.
                                mainActivity.SetMenuData(jsonObject);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block0
                Toast.makeText(context, httpServicesClass.getErrorMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = ProgressDialog.show(mainActivity, "Load Data", "Please wait...", true, true);
        }


        @Override
        protected void onPostExecute(Void result) {
//            pDialog.dismiss();
            mainActivity.ShowListMenu();

        }
    }
    //endregion
}
