package com.bkic.lymenglong.audiobookbkic.handleLists.history;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.bkic.lymenglong.audiobookbkic.utils.Const.HttpURL_API;

public class PresenterShowListHistory implements PresenterShowListHistoryImp {

    private ListHistory listHistoryActivity;
    private ProgressDialog pDialog;
    private static final String TAG = "PreShowListHistory";

    PresenterShowListHistory(ListHistory listHistoryActivity) {
        this.listHistoryActivity = listHistoryActivity;
    }

    @Override
    public void GetSelectedResponse(Activity activity, HashMap<String, String> ResultHash, String HttpHolder) {
//        HttpWebCall(activity, ResultHash, HttpHolder);
        RequestJSON(activity,ResultHash);
    }
    private String jsonAction, jsonResult, jsonMessage, jsonLog;
    private Boolean LogSuccess;
    private void RequestJSON(final Context context, final HashMap<String,String> hashMap){
        pDialog = ProgressDialog.show(context,null,context.getString(R.string.message_please_wait),true,true);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, HttpURL_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    jsonAction = jsonObject.getString(Const.JSON_KEY_ACTION);
                    jsonResult = jsonObject.getString(Const.JSON_KEY_RESULT);
                    jsonMessage = jsonObject.getString(Const.JSON_KEY_MESSAGE);
                    jsonLog = jsonObject.getString(Const.JSON_KEY_LOG);
                    LogSuccess = jsonLog.equals(Const.JSON_KEY_LOG_SUCCESS);
                    if (LogSuccess) {
                        switch (jsonAction){
                            //region ListHistoryBook : getHistory
                            case "getHistory":
                                try {
                                    JSONArray jsonArrayChapter = new JSONArray(jsonResult);
                                    if (jsonArrayChapter.length()!=0) {
                                        for (int i = 0; i< jsonArrayChapter.length(); i++){
                                            try {
                                                listHistoryActivity.SetTableSelectedData(jsonArrayChapter.getJSONObject(i));
                                            } catch (JSONException ignored) {
                                                Log.d(TAG, "onPostExecute: "+jsonArrayChapter.getJSONObject(i));
                                            }
                                        }
                                    } else {
                                        listHistoryActivity.LoadListDataFailed(jsonMessage);
                                    }
                                } catch (JSONException ignored) {
                                    Log.d(TAG, "onPostExecute: "+ jsonResult);
                                }
                                listHistoryActivity.ShowListFromSelected();
                                break;
                            //endregion
                        }
                    } else {
                        Log.d(TAG, "onPostExecute:" + jsonLog);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                String ms = context.getString(R.string.error_message_not_stable_internet);
                listHistoryActivity.LoadListDataFailed(ms);
                Log.e(TAG, "onErrorResponse:" +error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return hashMap;
            }
        };

        requestQueue.add(request);
    }


    //region Method to show current record Current Selected Record
    /*private String FinalJSonObject;
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
                    //region ListHistoryBook : getHistory
                    case "getHistory":
                        try {
                            JSONArray jsonArrayChapter = new JSONArray(jsonResult);
                            if (jsonArrayChapter.length()!=0) {
                                for (int i = 0; i< jsonArrayChapter.length(); i++){
                                    try {
                                        listHistoryActivity.SetTableSelectedData(jsonArrayChapter.getJSONObject(i));
                                    } catch (JSONException ignored) {
                                        Log.d(TAG, "onPostExecute: "+jsonArrayChapter.getJSONObject(i));
                                    }
                                }
                            } else {
                                listHistoryActivity.LoadListDataFailed(jsonMessage);
                            }
                        } catch (JSONException ignored) {
                            Log.d(TAG, "onPostExecute: "+ jsonResult);
                        }
                        listHistoryActivity.ShowListFromSelected();
                        break;
                    //endregion
                }
            } else {
                Log.d(TAG, "onPostExecute:" + jsonLog);
            }

        }
    }


*/    //endregion
    //endregion
}
