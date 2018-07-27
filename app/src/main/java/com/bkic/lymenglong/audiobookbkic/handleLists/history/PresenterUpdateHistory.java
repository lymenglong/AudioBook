package com.bkic.lymenglong.audiobookbkic.handleLists.history;

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
import com.bkic.lymenglong.audiobookbkic.player.PlayControl;
import com.bkic.lymenglong.audiobookbkic.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.bkic.lymenglong.audiobookbkic.utils.Const.HttpURL_API;

public class PresenterUpdateHistory implements PresenterUpdateHistoryImp {
    private static final String TAG = "PreUpdateHistory";
    private PlayControl playControlActivity;
    private ListHistory listHistoryActivity;

    public PresenterUpdateHistory(PlayControl playControlActivity) {
        this.playControlActivity = playControlActivity;
    }

    PresenterUpdateHistory(ListHistory listHistoryActivity) {
        this.listHistoryActivity = listHistoryActivity;
    }

    private String jsonAction, /*jsonResult,*/ jsonMessage, jsonLog;
    private Boolean LogSuccess;
    private void RequestJSON(final Context context, final HashMap<String,String> hashMap){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, HttpURL_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    jsonAction = jsonObject.getString(Const.JSON_KEY_ACTION);
//                    jsonResult = jsonObject.getString(Const.JSON_KEY_RESULT);
                    jsonMessage = jsonObject.getString(Const.JSON_KEY_MESSAGE);
                    jsonLog = jsonObject.getString(Const.JSON_KEY_LOG);
                    LogSuccess = jsonLog.equals(Const.JSON_KEY_LOG_SUCCESS);
                    switch (jsonAction){
                        case "addHistory":
                            if (LogSuccess) playControlActivity.UpdateHistorySuccess(jsonMessage);
                            else playControlActivity.UpdateHistoryFailed(jsonMessage);
                            break;
                        case "removeHistory":
                            if (LogSuccess) listHistoryActivity.RemoveHistorySuccess(jsonMessage);
                            else listHistoryActivity.RemoveHistoryFailed(jsonMessage);
                            break;
                        case "removeAllHistory":
                            if (LogSuccess) listHistoryActivity.RemoveAllHistorySuccess(jsonMessage);
                            else listHistoryActivity.RemoveAllHistoryFailed(jsonMessage);

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
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return hashMap;
            }
        };

        requestQueue.add(request);
    }


    //region Method to Update Record
    /*private String FinalJSonObject;
    private String finalResult ;
    private HttpParse httpParse = new HttpParse();
    private void UpdateRecordData(final Activity activity, final HashMap<String, String> ResultHash, final String HttpUrl){

        @SuppressLint("StaticFieldLeak")
        class UpdateRecordDataClass extends AsyncTask<Void,Void,String> {
            @Override
            protected String  doInBackground(Void... voids) {

                finalResult = httpParse.postRequest(ResultHash, HttpUrl);

                return finalResult;
            }
            @Override
            protected void onPostExecute(String httpResponseMsg) {
                super.onPostExecute(httpResponseMsg);
                FinalJSonObject = httpResponseMsg;
                new GetHttpResponseFromHttpWebCall(activity).execute();
            }
        }
        UpdateRecordDataClass updateRecordDataClass = new UpdateRecordDataClass();
        updateRecordDataClass.execute();
    }

    //region Parsing Complete JSON Object.
    @SuppressLint("StaticFieldLeak")
    private class GetHttpResponseFromHttpWebCall extends AsyncTask<Void, Void, Void>
    {
        public Activity activity;

        String jsonAction = null;

        Boolean LogSuccess = false;

        String jsonResult;

        String jsonMessage;

        String jsonLog;

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
                if(FinalJSonObject != null )
                {
                    JSONObject jsonObject;

                    try {
                        jsonObject = new JSONObject(FinalJSonObject);

                        jsonAction = jsonObject.getString("Action");

                        jsonResult = jsonObject.getString("Result");

                        jsonLog = jsonObject.getString("Log");

                        LogSuccess = jsonObject.getString("Log").equals("Success");

                        jsonMessage = jsonObject.getString("Message");

                    }
                    catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            switch (jsonAction){
                case "addHistory":
                    if (LogSuccess) playControlActivity.UpdateHistorySuccess(jsonMessage);
                    else playControlActivity.UpdateHistoryFailed(jsonMessage);
                    break;
                case "removeHistory":
                    if (LogSuccess) listHistoryActivity.RemoveHistorySuccess(jsonMessage);
                    else listHistoryActivity.RemoveHistoryFailed(jsonMessage);
                    break;
                case "removeAllHistory":
                    if (LogSuccess) listHistoryActivity.RemoveAllHistorySuccess(jsonMessage);
                    else listHistoryActivity.RemoveAllHistoryFailed(jsonMessage);

            }

        }
    }
 */   //endregion

    //Update Favorite Or History To Server (addHistory, addFavorite)
    @Override
    public void RequestUpdateToServer(String actionRequest, String userId, String bookId, String insertTime) {
        HashMap<String, String> ResultHash = new HashMap<>();
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\":\""+actionRequest+"\", " +
                        "\"UserId\":\""+userId+"\", " +
                        "\"BookId\":\""+bookId+"\", " +
                        "\"InsertTime\":\""+insertTime+"\"" +
                        "}";
        ResultHash.put(keyPost, valuePost);
//        UpdateRecordData(playControlActivity, ResultHash, HttpURL_API);
        RequestJSON(playControlActivity, ResultHash);
    }
    //endregion


    @Override
    public void RequestToRemoveBookById(Context context, String userId, String bookId) {
        HashMap<String, String> ResultHash = new HashMap<>();
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\":\"removeHistory\", " +
                        "\"UserId\":\""+userId+"\", " +
                        "\"BookId\":\""+bookId+"\"" +
                "}";
        ResultHash.put(keyPost, valuePost);
//        UpdateRecordData(playControlActivity, ResultHash, HttpURL_API);
        RequestJSON(context, ResultHash);
    }

    @Override
    public void RequestToRemoveAllBook(String userId) {
        HashMap<String, String> ResultHash = new HashMap<>();
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\":\"removeAllHistory\", " +
                        "\"UserId\":\""+userId+"\"" +
                "}";
        ResultHash.put(keyPost, valuePost);
//        UpdateRecordData(playControlActivity, ResultHash, HttpURL_API);
        RequestJSON(playControlActivity, ResultHash);
    }
}
