package com.bkic.lymenglong.audiobookbkic.handleLists.favorite;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.bkic.lymenglong.audiobookbkic.https.HttpParse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PresenterShowListFavorite implements PresenterShowListFavoriteImp {

    private ProgressDialog pDialog;
    private ListFavorite listFavoriteActivity;
    private static final String TAG = "PresenterShowListFavorite";

    public PresenterShowListFavorite(ListFavorite listFavoriteActivity) {
        this.listFavoriteActivity = listFavoriteActivity;
    }

    @Override
    public void GetSelectedResponse(Activity activity, HashMap<String, String> ResultHash, String HttpHolder) {
        HttpWebCall(activity, ResultHash, HttpHolder);
    }

    //region Method to show current record Current Selected Record
    private String FinalJSonObject;
    private String ParseResult;
    private HttpParse httpParse = new HttpParse();
    private void HttpWebCall(final Activity activity, final HashMap<String,String> ResultHash, final String httpHolder){

        @SuppressLint("StaticFieldLeak")
        class HttpWebCallFunction extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = ProgressDialog.show(activity,"Loading Data","Please wait",true,true);
            }

            @Override
            protected String doInBackground(Void... voids) {

                ParseResult = httpParse.postRequest(ResultHash, httpHolder);

                return ParseResult;
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;
                //Parsing the Stored JSOn String to GetHttpResponse Method.
                pDialog.dismiss();
                new GetHttpResponseFromHttpWebCall(activity).execute();
            }

        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute();
    }

    //region Parsing Complete JSON Object.
    @SuppressLint("StaticFieldLeak")
    private class GetHttpResponseFromHttpWebCall extends AsyncTask<Void, Void, Void>
    {
        public Activity activity;
        private String jsonAction;
        private String jsonResult;
        private String jsonMessage;
        private String jsonLog;
        private Boolean logSuccess = false;

        GetHttpResponseFromHttpWebCall(Activity activity)
        {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            try
            {
                if(FinalJSonObject != null)
                {
                    JSONObject jsonObject = new JSONObject(FinalJSonObject);
                    jsonAction = jsonObject.getString("Action");
                    jsonResult = jsonObject.getString("Result");
                    jsonMessage = jsonObject.getString("Message");
                    jsonLog = jsonObject.getString("Log");
                    logSuccess = jsonLog.equals("Success");
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(Void result)
        {
            if (logSuccess) {
                switch (jsonAction){
                    //region ListFavoriteBook : getFavorite
                    case "getFavourite":
                        try {
                            JSONArray jsonArrayChapter = new JSONArray(jsonResult);
                            if (jsonArrayChapter.length()!=0) {
                                for (int i = 0; i< jsonArrayChapter.length(); i++){
                                    try {
                                        listFavoriteActivity.SetTableSelectedData(jsonArrayChapter.getJSONObject(i));
                                    } catch (JSONException ignored) {
                                        Log.d(TAG, "onPostExecute: "+jsonArrayChapter.getJSONObject(i));
                                    }
                                }
                            } else {
                                listFavoriteActivity.LoadListDataFailed(jsonMessage);
                            }
                        } catch (JSONException ignored) {
                            Log.d(TAG, "onPostExecute: "+ jsonResult);
                        }
                        listFavoriteActivity.ShowListFromSelected();
                        break;
                    //endregion
                }
            } else {
                Log.d(TAG, "onPostExecute:" + jsonLog);
            }

        }
    }


    //endregion
    //endregion
}
