package com.bkic.lymenglong.audiobookbkic.review;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.checkInternet.ConnectivityReceiver;
import com.bkic.lymenglong.audiobookbkic.player.PlayControl;
import com.bkic.lymenglong.audiobookbkic.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.bkic.lymenglong.audiobookbkic.utils.Const.HttpURL_API;

public class PresenterReview
        implements
            PresenterReviewImp ,
            DialogInterface.OnShowListener,
            DialogInterface.OnDismissListener,
            View.OnClickListener,
            RadioGroup.OnCheckedChangeListener{
    private PlayControl playControlActivity;
    private static final String TAG = "PresenterReview";
    private RadioButton radioButton;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioButton radioButton4;
    private RadioButton radioButton5;
    private int rateNumber = 0;
    private int clickCount = 0;

    public PresenterReview(PlayControl playControlActivity) {
        this.playControlActivity = playControlActivity;
    }
    private Dialog dialog;

    @Override
    public void ReviewBookDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_review);
        Button buttonDismiss = dialog.findViewById(R.id.button_dismiss);
        Button buttonSubmit = dialog.findViewById(R.id.button_submit);
        radioButton = dialog.findViewById(R.id.rb1);
        radioButton2 = dialog.findViewById(R.id.rb2);
        radioButton3 = dialog.findViewById(R.id.rb3);
        radioButton4 = dialog.findViewById(R.id.rb4);
        radioButton5 = dialog.findViewById(R.id.rb5);
        buttonSubmit.setOnClickListener(this);
        buttonDismiss.setOnClickListener(this);
        dialog.setOnShowListener(this);
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    @Override
    public void ReviewBookDialog4(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_review4);
        Button buttonDismiss = dialog.findViewById(R.id.button_dismiss);
        Button buttonSubmit = dialog.findViewById(R.id.button_submit);
        radioButton = dialog.findViewById(R.id.rb1);
        radioButton2 = dialog.findViewById(R.id.rb2);
        radioButton3 = dialog.findViewById(R.id.rb3);
        radioButton4 = dialog.findViewById(R.id.rb4);
        radioButton5 = dialog.findViewById(R.id.rb5);

        radioButton.setOnClickListener(this);
        radioButton2.setOnClickListener(this);
        radioButton3.setOnClickListener(this);
        radioButton4.setOnClickListener(this);
        radioButton5.setOnClickListener(this);

        buttonSubmit.setOnClickListener(this);
        buttonDismiss.setOnClickListener(this);
        dialog.setOnShowListener(this);
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    @Override
    public void ReviewBookDialog2(final Context context){
        dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_review2);
//        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        View view = dialog.findViewById(R.id.viewReview);
        view.setContentDescription(context.getString(R.string.prompt_review_question));
//        ViewCompat.setImportantForAccessibility(view, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
        dialog.show();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCount++;
                final Handler handler = new Handler();
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        switch (clickCount){
                            case 1:
                                Toast.makeText(context, "Single Click", Toast.LENGTH_SHORT).show();
                                rateNumber = clickCount;
                                playControlActivity.AddReviewBookToServer();
                                playControlActivity.setRateNumber(rateNumber);
                                dialog.dismiss();
                                playControlActivity.AddReviewBookToServer();
                                break;
                            case 2:
                                Toast.makeText(context, "Double Click", Toast.LENGTH_SHORT).show();
                                rateNumber = clickCount;
                                playControlActivity.setRateNumber(rateNumber);
                                dialog.dismiss();
                                playControlActivity.AddReviewBookToServer();
                                break;
                            case 3:
                                Toast.makeText(context, "Triple Click", Toast.LENGTH_SHORT).show();
                                rateNumber = clickCount;
                                playControlActivity.setRateNumber(rateNumber);
                                dialog.dismiss();
                                playControlActivity.AddReviewBookToServer();
                                break;
                            case 4:
                                Toast.makeText(context, "4 Times Click", Toast.LENGTH_SHORT).show();
                                rateNumber = clickCount;
                                playControlActivity.setRateNumber(rateNumber);
                                dialog.dismiss();
                                playControlActivity.AddReviewBookToServer();
                                break;
                            case 5:
                                Toast.makeText(context, "5 Times Click", Toast.LENGTH_SHORT).show();
                                rateNumber = clickCount;
                                playControlActivity.setRateNumber(rateNumber);
                                dialog.dismiss();
                                playControlActivity.AddReviewBookToServer();
                                break;
                            /*default:
                                Toast.makeText(context, "Unknown Click", Toast.LENGTH_SHORT).show();
                                break; //no need*/
                        }
                        clickCount = 0;
                    }
                };
                handler.postDelayed(runnable,3000);
            }
        });

    }

    @Override
    public void ReviewBookDialog3(Context context){
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_review3);
        Button buttonDismiss = dialog.findViewById(R.id.button_dismiss);
        Button buttonSubmit = dialog.findViewById(R.id.button_submit);
//        RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
        buttonSubmit.setOnClickListener(this);
        buttonDismiss.setOnClickListener(this);
        dialog.setOnShowListener(this);
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    @Override
    public void RequestReviewBook(Activity activity, int userId, int bookId, int rateNumber, String review){
        String keyPost = "json";
        String value =
                "{" +
                        "\"Action\":\"addReview\", " +
                        "\"UserId\":\""+userId+"\", " +
                        "\"BookId\":\""+bookId+"\", " +
                        "\"Rate\":\""+rateNumber+"\"," +
                        "\"Review\":\""+review+"\"" +
                "}";
        HashMap<String,String> ResultHash = new HashMap<>();
        ResultHash.put(keyPost,value);
//        HttpWebCall(activity,ResultHash, Const.HttpURL_API);
        RequestJSON(activity, ResultHash);
    }

    @Override
    public void RequestGetReviewData(Activity activity, int bookId) {
        String keyPost = "json";
        String valuePost =
                "{" +
                        " \"Action\":\"getReview\", " +
                        "\"BookId\":\""+bookId+"\"" +
                "}";
        HashMap<String,String> ResultHash = new HashMap<>();
        ResultHash.put(keyPost,valuePost);
//        HttpWebCall(activity, ResultHash, Const.HttpURL_API);
        RequestJSON(activity, ResultHash);
    }

    @Override
    public void RequestAddChapterReview(Activity activity, int userId, int bookId, int chapterId, int rateNumber, String review){
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\":\"addChapterReview\", " +
                        "\"BookId\": \""+bookId+"\", " +
                        "\"ChapterId\":\""+chapterId+"\", " +
                        "\"UserId\":\""+userId+"\", " +
                        "\"Rate\":\""+rateNumber+"\", " +
                        "\"Review\":\""+review+"\"" +
                "}";
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(keyPost, valuePost);
//        HttpWebCall(activity, hashMap,Const.HttpURL_API);
        RequestJSON(activity, hashMap);
    }
    @Override
    public void RequestGetReviewChapter(Activity activity, int bookId, int chapterId) {
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\":\"getChapterReview\"," +
                        "\"BookId\":\""+bookId+"\"," +
                        "\"ChapterId\":\""+chapterId+"\"" +
                "}";
        HashMap<String,String> ResultHash = new HashMap<>();
        ResultHash.put(keyPost,valuePost);
//        HttpWebCall(activity, ResultHash, Const.HttpURL_API);
        RequestJSON(activity, ResultHash);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        Log.d(TAG, "onShow: " +dialog.toString());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.d(TAG, "onDismiss: " +dialog.toString());
        NextMediaDialog(playControlActivity);
    }
    private void NextMediaDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.message_play_next_chapter_or_not));
        builder.setCancelable(true);
        builder.setPositiveButton("Nghe tiếp", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                playControlActivity.NextMedia();
                dialogInterface.dismiss();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_submit:
                if(ConnectivityReceiver.isConnected())
//                    SubmitFromDialog();
//                    SubmitFromDialog3();
                    SubmitFromDialog4();
                else Toast.makeText(playControlActivity, playControlActivity.getString(R.string.message_internet_not_connected), Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_dismiss:
                dialog.dismiss();
                break;
            case R.id.rb1:
                ClearCheckRatingBar();
                radioButton.setChecked(true);
                rateNumber = 1;
                break;
            case R.id.rb2:
                ClearCheckRatingBar();
                radioButton.setChecked(true);
                radioButton2.setChecked(true);
                rateNumber = 2;
                break;
            case R.id.rb3:
                ClearCheckRatingBar();
                radioButton.setChecked(true);
                radioButton2.setChecked(true);
                radioButton3.setChecked(true);
                rateNumber = 3;
                break;
            case R.id.rb4:
                ClearCheckRatingBar();
                radioButton.setChecked(true);
                radioButton2.setChecked(true);
                radioButton3.setChecked(true);
                radioButton4.setChecked(true);
                rateNumber = 4;
                break;
            case R.id.rb5:
                ClearCheckRatingBar();
                radioButton.setChecked(true);
                radioButton2.setChecked(true);
                radioButton3.setChecked(true);
                radioButton4.setChecked(true);
                radioButton5.setChecked(true);
                rateNumber = 5;
                break;

        }
    }

    private void ClearCheckRatingBar(){
        radioButton.setChecked(false);
        radioButton2.setChecked(false);
        radioButton3.setChecked(false);
        radioButton4.setChecked(false);
        radioButton5.setChecked(false);
    }
/*    private void SubmitFromDialog3() {
        SubmitBntIsClicked = true;
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Log.d(TAG, "onRatingChanged: "+rating);
            }
        });
        playControlActivity.setRateNumber((int) ratingBar.getRating());
        dialog.dismiss();
    }*/

/*    private void SubmitFromDialog() {
        if(radioButton.isChecked()){
            rateNumber = 1;
        } else if(radioButton2.isChecked()){
            rateNumber = 2;
        } else if(radioButton3.isChecked()){
            rateNumber = 3;
        } else if(radioButton4.isChecked()){
            rateNumber = 4;
        } else if(radioButton5.isChecked()){
            rateNumber = 5;
        } else rateNumber = 0;
        playControlActivity.setRateNumber(rateNumber);
        playControlActivity.setReview("");//todo add Comment Review Of User
//            playControlActivity.AddReviewBookToServer();
        if(rateNumber != 0){
            playControlActivity.UpdateReviewTable();
            playControlActivity.AddReviewChapterToServer();
            dialog.dismiss();
        } else {
            String ms = playControlActivity.getString(R.string.message_no_rate_value);
            playControlActivity.mToastMessage(ms);
        }
    }*/

    private void SubmitFromDialog4() {
        playControlActivity.setRateNumber(rateNumber);
        playControlActivity.setReview("");//todo add Comment Review Of User
//            playControlActivity.AddReviewBookToServer();
        if(rateNumber != 0){
            playControlActivity.UpdateReviewTable();
            playControlActivity.AddReviewChapterToServer();
            dialog.dismiss();
        } else {
            rateNumber = 0;
            String ms = playControlActivity.getString(R.string.message_no_rate_value);
            playControlActivity.mToastMessage(ms);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

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
                    switch (jsonAction) {
                        case "addReview":
                            if (LogSuccess) {
                                playControlActivity.UpdateReviewSuccess(jsonMessage);
                            } else {
                                playControlActivity.UpdateReviewFailed(jsonMessage);
                            }
                            break;
                        case "getReview":
                            //todo get review data
                            break;
                        case "addChapterReview":
                            if (LogSuccess) {
                                playControlActivity.UpdateChapterReviewSuccess(jsonMessage);
                            } else {
                                playControlActivity.UpdateChapterReviewFailed(jsonMessage);
                            }
                            break;
                        case "getChapterReview":
                            //todo get chapter review data
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
 /*   private String FinalJSonObject;
    private String finalResult ;
    private HttpParse httpParse = new HttpParse();
    private void HttpWebCall(final Activity activity, final HashMap<String, String> ResultHash, final String HttpUrl){

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

        String ResultJsonObject;

        String message;

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

                        ResultJsonObject = jsonObject.getString("Result");

                        LogSuccess = jsonObject.getString("Log").equals("Success");

                        message = jsonObject.getString("Message");

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
            switch (jsonAction) {
                case "addReview":
                    if (LogSuccess) {
                        playControlActivity.UpdateReviewSuccess(message);
                    } else {
                        playControlActivity.UpdateReviewFailed(message);
                    }
                    break;
                case "getReview":
                    //todo get review data
                    break;
                case "addChapterReview":
                    if (LogSuccess) {
                        playControlActivity.UpdateChapterReviewSuccess(message);
                    } else {
                        playControlActivity.UpdateChapterReviewFailed(message);
                    }
                    break;
                case "getChapterReview":
                    //todo get chapter review data
                    break;
            }
        }
    }
    //endregion
 */   //endregion



}
