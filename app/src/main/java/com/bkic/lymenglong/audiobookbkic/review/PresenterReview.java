package com.bkic.lymenglong.audiobookbkic.review;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.checkInternet.ConnectivityReceiver;
import com.bkic.lymenglong.audiobookbkic.https.HttpParse;
import com.bkic.lymenglong.audiobookbkic.utils.Const;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.player.PlayControl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

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
    private int rateNumber;
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
        HttpWebCall(activity,ResultHash, Const.HttpURL_API);
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
        HttpWebCall(activity, ResultHash, Const.HttpURL_API);
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
        HttpWebCall(activity, hashMap,Const.HttpURL_API);
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
        HttpWebCall(activity, ResultHash, Const.HttpURL_API);
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
                    SubmitFromDialog();
                else Toast.makeText(playControlActivity, playControlActivity.getString(R.string.message_internet_not_connected), Toast.LENGTH_SHORT).show();
//                SubmitFromDialog3();
                break;
            case R.id.button_dismiss:
                dialog.dismiss();
        }
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

    private void SubmitFromDialog() {
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
        }
        playControlActivity.setRateNumber(rateNumber);
        playControlActivity.setReview("");//todo add Comment Review Of User
//            playControlActivity.AddReviewBookToServer();
        playControlActivity.UpdateReviewTable();
        playControlActivity.AddReviewChapterToServer();
        dialog.dismiss();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }

    //region Method to Update Record
    private String FinalJSonObject;
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



}
