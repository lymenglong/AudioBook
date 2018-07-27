package com.bkic.lymenglong.audiobookbkic.search;

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

public class PresenterSearchBook implements PresenterSearchImp {
    private ListBookSearch listBookSearchActivity;
    private static final String TAG = "PresenterSearchBook";
    private ProgressDialog pDialog;
    private String keyWord;

    PresenterSearchBook(ListBookSearch listBookSearchActivity) {
        this.listBookSearchActivity = listBookSearchActivity;
    }

    @Override
    public void SearchBook(String keyWord) {
        this.keyWord = keyWord;
        HashMap<String,String> hashMap = new HashMap<>();
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\":\"search\"," +
                        "\"Keyword\":\""+keyWord+"\"" +
                "}";
        hashMap.put(keyPost, valuePost);
        RequestJSON(listBookSearchActivity, hashMap);
//        HttpWebCall(listBookSearchActivity, hashMap, Const.HttpURL_API);
    }

    private String jsonAction, jsonResult, jsonMessage, jsonLog;
    private Boolean LogSuccess;
    private void RequestJSON(final Context context, final HashMap<String,String> hashMap){
        pDialog = ProgressDialog.show(context,context.getString(R.string.prompt_search),keyWord,true,true);
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
                    LogSuccess = jsonLog.equals(Const.JSON_KEY_LOG_SUCCESS)|jsonLog.equals("Successs");
                    switch (jsonAction) {
                        case "search":
                            if (LogSuccess) {
                                try {
                                    JSONArray jsonArrayResult = new JSONArray(jsonResult);
                                    if(jsonArrayResult.length()!=0) {
                                        for (int j = 0; j < jsonArrayResult.length(); j++) {
                                            try {
                                                listBookSearchActivity.SetTableSelectedData(jsonArrayResult.getJSONObject(j));
                                            } catch (JSONException ignored) {
                                                Log.d(TAG, "onPostExecute: "+jsonArrayResult.getJSONObject(j));
                                            }
                                        }
                                    } else{
                                        listBookSearchActivity.LoadListDataFailed(jsonMessage);
                                    }
                                } catch (JSONException ignored) {
                                    Log.e(TAG, "onPostExecute: "+ jsonResult);
                                }
                                listBookSearchActivity.ShowListFromSelected();
                                break;
                            } else {
                                if(jsonMessage.isEmpty()) jsonMessage = jsonLog;
                                listBookSearchActivity.LoadListDataFailed(jsonMessage);
                            }
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String ms = context.getString(R.string.error_message_not_stable_internet);
                Toast.makeText(context, ms, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onErrorResponse:" +error.getMessage());
                pDialog.dismiss();
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
/*
    private String FinalJSonObject;
    private String ParseResult;
    private HttpParse httpParse = new HttpParse();
    private void HttpWebCall(final Activity activity, final HashMap<String,String> ResultHash, final String httpHolder){

        @SuppressLint("StaticFieldLeak")
        class HttpWebCallFunction extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = ProgressDialog.show(activity, activity.getString(R.string.prompt_search),keyWord,true,true);
            }

            @Override
            protected String doInBackground(Void... voids) {

                ParseResult = httpParse.postRequest(ResultHash, httpHolder);

                return ParseResult;
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {
                super.onPostExecute(httpResponseMsg);
                pDialog.dismiss();
                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;
                //Parsing the Stored JSOn String to GetHttpResponse Method.
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
//            pDialog = ProgressDialog.show(activity,"Loading Data","Please wait",true,true);
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
                    logSuccess = jsonLog.equals("Successs")|jsonLog.equals("Success");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
//                pDialog.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            switch (jsonAction) {
                case "search":
                    if (logSuccess) {
                        try {
                            JSONArray jsonArrayResult = new JSONArray(jsonResult);
                            if(jsonArrayResult.length()!=0) {
                                for (int j = 0; j < jsonArrayResult.length(); j++) {
                                    try {
                                        listBookSearchActivity.SetTableSelectedData(jsonArrayResult.getJSONObject(j));
                                    } catch (JSONException ignored) {
                                        Log.d(TAG, "onPostExecute: "+jsonArrayResult.getJSONObject(j));
                                    }
                                }
                            } else{
                                listBookSearchActivity.LoadListDataFailed(jsonMessage);
                            }
                        } catch (JSONException ignored) {
                            Log.e(TAG, "onPostExecute: "+ jsonResult);
                        }
                        listBookSearchActivity.ShowListFromSelected();
                        break;
                    } else {
                        if(jsonMessage.isEmpty()) jsonMessage = jsonLog;
                        listBookSearchActivity.LoadListDataFailed(jsonMessage);
                    }
                    break;
            }
    }
    }
    //endregion
*/
    //endregion
}
