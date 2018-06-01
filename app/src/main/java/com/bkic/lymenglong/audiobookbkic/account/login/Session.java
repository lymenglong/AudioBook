package com.bkic.lymenglong.audiobookbkic.account.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.bkic.lymenglong.audiobookbkic.account.utils.User;

import java.util.ArrayList;
import java.util.List;

public class Session {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private final String TAG = getClass().getSimpleName();

    @SuppressLint("CommitPrefEdits")
    public Session(Context ctx){
        prefs = ctx.getSharedPreferences("ACCOUNT", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setUserInfo(User user){
        editor.putInt("ID_USER", user.getId());
        editor.putString("FIRST_NAME",user.getFirstName());
        editor.putString("LAST_NAME",user.getLastName());
        editor.putString("FULL_NAME",user.getLastName() + " " + user.getFirstName());
        editor.putString("USERNAME",user.getUsername());
        editor.putString("BIRTHDAY",user.getBirthday());
        editor.putString("EMAIL",user.getEmail());
        editor.putString("IDENTITY_NUMBER",user.getIdentitynumber());
        editor.putString("PASSWORD",user.getPassword());
        editor.putString("PHONE_NUMBER",user.getPhonenumber());
        editor.putString("ADDRESS",user.getAddress());
        editor.commit();
    }

    public void setUpdateUserDetail(User user){
        editor.putString("FIRST_NAME",user.getFirstName());
        editor.putString("LAST_NAME",user.getLastName());
        editor.putString("FULL_NAME",user.getLastName() + " " + user.getFirstName());
        editor.putString("USERNAME",user.getUsername());
        editor.putString("EMAIL",user.getEmail());
        editor.putString("PHONE_NUMBER",user.getPhonenumber());
        editor.putString("ADDRESS",user.getAddress());
        editor.commit();
    }

    public List<User> getListUserInfo(){
        List<User> userList = new ArrayList<>();
        User user = new User();
        user.setId(prefs.getInt("ID_USER", -1));
        user.setUsername(prefs.getString("USERNAME","DEFAULT"));
        user.setFirstName(prefs.getString("FIRST_NAME","DEFAULT"));
        user.setLastName(prefs.getString("LAST_NAME","DEFAULT"));
        user.setPhonenumber(prefs.getString("PHONE_NUMBER","DEFAULT"));
        user.setBirthday(prefs.getString("BIRTHDAY","DEFAULT"));
        user.setAddress(prefs.getString("ADDRESS","DEFAULT"));
        user.setEmail(prefs.getString("EMAIL","DEFAULT"));
        user.setIdentitynumber(prefs.getString("IDENTITY_NUMBER","DEFAULT"));
        user.setName(prefs.getString("FULL_NAME","DEFAULT"));
        user.setPassword(prefs.getString("PASSWORD","DEFAULT"));
        userList.add(user);
        return userList;
    }

    public void setLoggedin(boolean logggedin){
        editor.putBoolean("loggedInmode",logggedin);
        editor.commit();
    }

    public void setNameLoggedIn(String strEmail){
        editor.putString("NAME_LOGGED_IN", strEmail);
        editor.commit();
    }

   /* public void setUserIdLoggedIn(String strUserId){
        editor.putString("USER_ID", strUserId);
        editor.commit();
    }*/

    public void setRemoveEmailLoggedIn(String strEmail){
        editor.remove(strEmail);
        editor.commit();
    }

    public String getFullName(){
        return prefs.getString("FULL_NAME", "DEFAULT");
    }

    public String getNameLoggedIn(){
//        Log.d(TAG, "EmailState: "+"Email: "+prefs.getString("EMAIL_LOGGED_IN", "Email"));
        return prefs.getString("NAME_LOGGED_IN", "DEFAULT");
    }

    public int getUserIdLoggedIn(){
//        Log.d(TAG, "EmailState: "+"Email: "+prefs.getString("EMAIL_LOGGED_IN", "Email"));
        return prefs.getInt("ID_USER", -1);
    }

    public void getClearSession(){
        editor.clear();
        editor.apply();
    }

    public boolean loggedin(){
        return prefs.getBoolean("loggedInmode", false);
    }
}
