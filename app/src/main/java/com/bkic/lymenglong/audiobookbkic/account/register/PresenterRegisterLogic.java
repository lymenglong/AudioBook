package com.bkic.lymenglong.audiobookbkic.account.register;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.account.utils.User;
import com.bkic.lymenglong.audiobookbkic.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.bkic.lymenglong.audiobookbkic.utils.Const.HttpURL_API;


public class PresenterRegisterLogic implements PresenterRegisterImp {
    private ViewRegisterActivity registerActivity;
    private static String TAG = "PresenterRegisterLogic";

    PresenterRegisterLogic(ViewRegisterActivity registerActivity) {
        this.registerActivity = registerActivity;
    }
    @Override
    public void Register(User userModel) {
        HashMap<String, String> ResultHash = new HashMap<>();
        // GetUserDetail
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\":\"register\", " +
                        "\"UserName\":\""+ userModel.getUsername()+"\"," +
                        "\"UserMail\":\""+ userModel.getEmail()+"\", " +
                        "\"UserFirstName\":\""+ userModel.getFirstName() +"\", " +
                        "\"UserLastName\":\""+ userModel.getLastName() +"\"," +
                        "\"UserPassword\":\""+ userModel.getPassword()+"\"," +
                        "\"UserAddress\":\""+ userModel.getAddress()+"\", " +
                        "\"UserPhone\":\""+ userModel.getPhonenumber()+"\" " +
                        "}";
        ResultHash.put(keyPost,valuePost);
        RequestJSON(registerActivity,ResultHash);
//        HttpWebCall(registerActivity, ResultHash, HttpUrl_API);
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
                        case "register":
                            if (LogSuccess) {
                                registerActivity.RegisterSuccess(jsonMessage);
                            } else {
                                registerActivity.RegisterFailed(jsonMessage);
                            }
                            break;
                        default:
                            String msAction = "Wrong action";
                            registerActivity.RegisterFailed(msAction);
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
                registerActivity.RegisterFailed(ms);
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


    //region RequestRegister Old Code
/*
    private void RequestRegister(final User userModel) {
        RequestQueue requestQueue = Volley.newRequestQueue(registerActivity);
        StringRequest request = new StringRequest(Request.Method.POST, HttpUrl_Register, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")) {
                        registerActivity.RegisterSuccess(jsonObject.getString("success"));
                    } else {
                        registerActivity.RegisterFailed(jsonObject.getString("error"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(registerActivity, "Lỗi, Kết nối thất bại", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("Fullname", userModel.getName());
                hashMap.put("Username", userModel.getUsername().trim().toLowerCase());
                hashMap.put("Email", userModel.getEmail().trim());
                hashMap.put("Password", userModel.getPassword().trim());
                hashMap.put("confirm_password", userModel.getConfirmPassword().trim());
                hashMap.put("Address", userModel.getAddress().trim());
                hashMap.put("PhoneNumber", userModel.getPhonenumber().trim());
                return hashMap;
            }
        };
        requestQueue.add(request);
    }*/
    //endregion

    //region HTTPWebCall
   /* private String FinalJSonObject;
    private String ParseResult;
    private HttpParse httpParse = new HttpParse();
    private void HttpWebCall(final Activity activity, final HashMap<String,String> ResultHash, final String httpHolder){

        @SuppressLint("StaticFieldLeak")
        class HttpWebCallFunction extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                pDialog = ProgressDialog.show(activity,"Loading Data","Please wait",true,true);
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
//                pDialog.dismiss();
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

        Boolean RegisterSuccess = false;

        String ResultJsonObject;

        String message = null;

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
                    JSONObject jsonObject;

                    try {

                        jsonObject = new JSONObject(FinalJSonObject);

                        ResultJsonObject = jsonObject.getString("Result");

                        RegisterSuccess = jsonObject.getString("Log").equals("Success");

                        message = jsonObject.getString("Message");

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if (RegisterSuccess){
                registerActivity.RegisterSuccess(message);
            } else{
                registerActivity.RegisterFailed(message);
            }
        }
    }*/
    //endregion

}
