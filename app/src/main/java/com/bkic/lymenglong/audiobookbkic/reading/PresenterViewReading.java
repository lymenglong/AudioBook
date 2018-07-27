package com.bkic.lymenglong.audiobookbkic.reading;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bkic.lymenglong.audiobookbkic.account.login.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class PresenterViewReading implements PresenterViewReadingImp {
    private ViewReading viewReadingActivity;
    private Session session;

    PresenterViewReading(ViewReading viewReadingActivity) {
        this.viewReadingActivity = viewReadingActivity;
    }

    @Override
    public void postHistoryDataToServer(String HttpUrl, final int idChapter) {
        session = new Session(viewReadingActivity);
        RequestQueue requestQueue = Volley.newRequestQueue(viewReadingActivity);
        StringRequest request = new StringRequest(Request.Method.POST, HttpUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")) {
                        viewReadingActivity.PostDataSuccess(jsonObject.getString("success"));
                    } else {
                        viewReadingActivity.PostDataFailed(jsonObject.getString("error"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                viewReadingActivity.PostDataFailed(error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("IdBook", String.valueOf(idChapter));
                hashMap.put("IdUser", String.valueOf(session.getUserIdLoggedIn()));
                return hashMap;
            }
        };

        requestQueue.add(request);
    }
    @Override
    public void postFavoriteDataToServer(String favoriteURL, final int idChapter) {
        session = new Session(viewReadingActivity);
        RequestQueue requestQueueFavorite = Volley.newRequestQueue(viewReadingActivity);
        StringRequest requestFavorite = new StringRequest(Request.Method.POST, favoriteURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")) {
                        viewReadingActivity.PostDataSuccess(jsonObject.getString("success"));
                    } else {
                        viewReadingActivity.PostDataFailed(jsonObject.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(viewReadingActivity, "Lỗi mạng, không được thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("IdBook", String.valueOf(idChapter));
                hashMap.put("IdUser", String.valueOf(session.getUserIdLoggedIn()));
                return hashMap;
            }
        };
        requestQueueFavorite.add(requestFavorite);
    }
}
