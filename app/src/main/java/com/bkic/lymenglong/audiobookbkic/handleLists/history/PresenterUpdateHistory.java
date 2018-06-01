package com.bkic.lymenglong.audiobookbkic.handleLists.history;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;

import com.bkic.lymenglong.audiobookbkic.https.HttpParse;
import com.bkic.lymenglong.audiobookbkic.player.PlayControl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.bkic.lymenglong.audiobookbkic.utils.Const.HttpURL_API;

public class PresenterUpdateHistory implements PresenterUpdateHistoryImp {
    private PlayControl playControlActivity;
    private ListHistory listHistoryActivity;

    public PresenterUpdateHistory(PlayControl playControlActivity) {
        this.playControlActivity = playControlActivity;
    }

    public PresenterUpdateHistory(ListHistory listHistoryActivity) {
        this.listHistoryActivity = listHistoryActivity;
    }

    //region Method to Update Record
    private String FinalJSonObject;
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
    //endregion

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
        UpdateRecordData(playControlActivity, ResultHash, HttpURL_API);
    }
    //endregion


    @Override
    public void RequestToRemoveBookById(String userId, String bookId) {
        HashMap<String, String> ResultHash = new HashMap<>();
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\":\"removeHistory\", " +
                        "\"UserId\":\""+userId+"\", " +
                        "\"BookId\":\""+bookId+"\"" +
                "}";
        ResultHash.put(keyPost, valuePost);
        UpdateRecordData(playControlActivity, ResultHash, HttpURL_API);
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
        UpdateRecordData(playControlActivity, ResultHash, HttpURL_API);
    }
}
