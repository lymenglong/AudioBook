package com.bkic.lymenglong.audiobookbkic.account.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.bkic.lymenglong.audiobookbkic.account.utils.User;
import com.bkic.lymenglong.audiobookbkic.https.HttpParse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.bkic.lymenglong.audiobookbkic.utils.Const.HttpURL_API;


public class PresenterLogin implements PresenterLoginImp {
    private ViewLoginActivity loginActivity;
    private String Email;
    private String TAG = getClass().getSimpleName();
    private String messageSuccess;

    public PresenterLogin(ViewLoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }


    @Override
    public void Login(String email, String password) {
        RequestLogin(email,password);
    }

    private void RequestLogin(final String email, final String password) {
        this.Email = email;
        HashMap<String,String> ResultHash = new HashMap<>();
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\": \"login\", " +
                        "\"UserName\": \""+email+"\", " +
                        "\"UserPassword\": \""+password+"\"" +
                "}";
        ResultHash.put(keyPost,valuePost);
        HttpWebCall(loginActivity,ResultHash,HttpURL_API);
    }

    @Override
    public void UserDetail(String email){
        HashMap<String, String> ResultHash = new HashMap<>();
        // GetUserDetail
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\": \"getUserDetail\", " +
                        "\"UserName\": \"" + email + "\" " +
                        "}";
        ResultHash.put(keyPost,valuePost);
        HttpWebCall(loginActivity,ResultHash,HttpURL_API);
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

        String jsonAction = null;

        Boolean LogSuccess = false;

        String ResultJsonObject;

        String jsonMessage;

        GetHttpResponseFromHttpWebCall(Activity activity)
        {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute()
        {
//            progressDialog = ProgressDialog.show(activity,"Loading Data","Please wait",true,true);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            if(FinalJSonObject != null )
            {
                JSONObject jsonObject;

                try {
                    jsonObject = new JSONObject(FinalJSonObject);

                    jsonAction = jsonObject.getString("Action");

                    ResultJsonObject = jsonObject.getString("Result");

                    LogSuccess = jsonObject.getString("Log").equals("Success");

                    jsonMessage = jsonObject.getString("Message");

                }
                catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            switch (jsonAction){
                case "login":
                    if (LogSuccess) {
                        Session session = new Session(loginActivity);
                        session.setLoggedin(true);
                        session.setNameLoggedIn(Email);
                        UserDetail(Email);
                        messageSuccess = jsonMessage;
                    } else {
                        loginActivity.LoginFailed(jsonMessage);
                    }
                    break;
                case "getUserDetail":
                    if(LogSuccess){
                        try {
                            GetUserDetail(ResultJsonObject);
                        } catch (JSONException ignored) {
                        }
                    }
                    break;
                default:
                    Log.d(TAG, "onPostExecute: "+ jsonAction);
                    break;
            }

        }
    }
    //endregion
    //endregion

    private void GetUserDetail(String ResultJsonObject) throws JSONException {
        JSONObject jsonObjectResult = new JSONObject(ResultJsonObject);
        if (jsonObjectResult.length()!=0){
            User userModel = new User();
            userModel.setId(Integer.parseInt(jsonObjectResult.getString("UserId")));
            userModel.setUsername(jsonObjectResult.getString("UserName"));
            userModel.setEmail(jsonObjectResult.getString("UserMail"));
            userModel.setFirstName(jsonObjectResult.getString("UserFirstName"));
            userModel.setLastName(jsonObjectResult.getString("UserLastName"));
//            userModel.setPassword(jsonObjectResult.getString("Password"));
            userModel.setAddress(jsonObjectResult.getString("UserAddress"));
//            userModel.setIdentitynumber(jsonObjectResult.getString("IdentityNumber"));
//            userModel.setBirthday(jsonObjectResult.getString("Birthday"));
            userModel.setPhonenumber(jsonObjectResult.getString("UserPhone"));
            // add to list
            //                           users.add(userModel);
            Session session = new Session(loginActivity);
            session.setUserInfo(userModel);
//            session.setUserIdLoggedIn(String.valueOf(userModel.getId()));
//            session.setNameLoggedIn(userModel.getName());
            session.getListUserInfo();
            loginActivity.LoginSuccess(messageSuccess);
        }
    }

}
