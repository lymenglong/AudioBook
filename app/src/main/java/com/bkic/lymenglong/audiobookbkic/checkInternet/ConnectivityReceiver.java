package com.bkic.lymenglong.audiobookbkic.checkInternet;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bkic.lymenglong.audiobookbkic.account.login.Session;
import com.bkic.lymenglong.audiobookbkic.database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ConnectivityReceiver
        extends BroadcastReceiver {

    public static ConnectivityReceiverListener connectivityReceiverListener;
    private Context context;
    private DBHelper dbHelper;

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        this.context = context;
        dbHelper = new DBHelper(context, Const.DB_NAME,null,Const.DB_VERSION);

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat =
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String insertTime = simpledateformat.format(calendar.getTime());

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                //history
                GetUnSyncDataAndSync("addHistory", "history", insertTime);
                //removeHistory
                GetUnSyncDataAndSync("removeHistory", "bookHistorySyncs",insertTime);
                //favorite
                GetUnSyncDataAndSync("addFavourite", "favorite",insertTime);
                //removeFavorite
                GetUnSyncDataAndSync("removeFavourite", "bookFavoriteSyncs",insertTime);
            }
        }

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }
    }

    private void GetUnSyncDataAndSync(String jsonAction, String tableName, String insertTime) {
        Cursor cursor;
        switch (jsonAction){
            case "addHistory":
                //getting all the unsynced books
                cursor = dbHelper.GetData(
                        "SELECT BookId FROM "+tableName+" WHERE BookSync = '"+Const.BOOK_NOT_SYNCED_WITH_SERVER+"'"
                );
                while (cursor.moveToNext()){
                    //calling the method to save the unsynced books to MySQL server
                    syncBook(
                            jsonAction,
                            tableName,
                            cursor.getInt(0), // BookId
                            insertTime
                    );
                }
                cursor.close();
                break;
            case "addFavourite":
                //getting all the unsynced books to mysql server
                cursor = dbHelper.GetData(
                        "SELECT BookId FROM "+tableName+" WHERE BookSync = '"+Const.BOOK_NOT_SYNCED_WITH_SERVER+"'"
                        );
                while (cursor.moveToNext()){
                    //calling the method to save the unsynced books to MySQL server
                    syncBook(
                            jsonAction,
                            tableName,
                            cursor.getInt(0), // BookId
                            insertTime
                    );
                }
                cursor.close();
                break;
            case "removeHistory":
                //getting all the removed books to mysql server
                cursor = dbHelper.GetData(
                        "SELECT BookId FROM "+tableName+" WHERE BookRemoved = '"+Const.BOOK_REQUEST_REMOVE_WITH_SERVER+"'"
                );
                while (cursor.moveToNext()){
                    //calling the method to save the removed books to MySQL server
                    syncBook(
                            jsonAction,
                            tableName,
                            cursor.getInt(0), // BookId
                            insertTime
                    );
                }
                cursor.close();
                break;
            case "removeFavourite":
                //getting all the removed books to mysql server
                cursor = dbHelper.GetData(
                        "SELECT BookId FROM "+tableName+" WHERE BookRemoved = '"+Const.BOOK_REQUEST_REMOVE_WITH_SERVER+"'"
                );
                while (cursor.moveToNext()){
                    //calling the method to save the removed books to MySQL server
                    syncBook(
                            jsonAction,
                            tableName,
                            cursor.getInt(0), // BookId
                            insertTime
                    );
                }
                cursor.close();
                break;
        }

    }

    public static boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) MyApplication.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }


    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }

    //SyncBook Update SQLite Status For History & Favorite
    private void UpdateBookSyncStatus(String tableName, int bookId) {
        String UPDATE_BOOK_SYNC =
                "UPDATE " +
                        "'"+tableName+"' " +
                        "SET " +
                        "BookSync = '"+ Const.BOOK_SYNCED_WITH_SERVER+"' " +
                        "WHERE " +
                        "BookId = '"+bookId+"'" +
                        ";";
        dbHelper.QueryData(UPDATE_BOOK_SYNC);
        dbHelper.close();
    }
    //SyncBook Update SQLite Status For removeHistory & removeFavorite
    private void UpdateBookRemovedStatus(String tableName, int bookId) {
        /*String UPDATE_BOOK_REMOVED =
                "UPDATE " +
                        "'"+tableName+"' " +
                        "SET " +
                        "BookRemoved = '"+ Const.BOOK_NOT_REQUEST_REMOVE_SYNCED_WITH_SERVER+"' ," +
                        "BookSync = '"+Const.BOOK_SYNCED_WITH_SERVER+"' " +
                        "WHERE " +
                        "BookId = '"+bookId+"'" +
                        ";";
        dbHelper.QueryData(UPDATE_BOOK_REMOVED);*/
        dbHelper.QueryData(
                "DELETE FROM "+tableName+" WHERE BookId = '"+bookId+"';"
        );
        dbHelper.close();
    }


    public void syncBook(final String jsonAction, final String tableName, final int bookId, final String insertTime) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.HttpURL_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            switch (jsonAction){
                                case "addHistory":
                                    if (obj.getString("Log").equals("Success")) {
                                        //updating the status in sqlite
//                                      db.updateNameStatus(id, MainActivity.NAME_SYNCED_WITH_SERVER);
                                        UpdateBookSyncStatus(tableName, bookId);
                                        //sending the broadcast to refresh the list
//                                      context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
                                    }
                                    break;
                                case "addFavorite":
                                    if (obj.getString("Log").equals("Success")) {
                                        //updating the status in sqlite
                                        UpdateBookSyncStatus(tableName, bookId);
                                    }
                                    break;
                                case "removeHistory":
                                    if (obj.getString("Log").equals("Success")) {
                                        //updating the status in sqlite
                                        UpdateBookRemovedStatus(tableName, bookId);
                                    }
                                    break;
                                case "removeFavorite":
                                    if (obj.getString("Log").equals("Success")) {
                                        //updating the status in sqlite
                                        UpdateBookRemovedStatus(tableName, bookId);
                                    }
                                    break;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                Session session = new Session(context);
                String valuePost;
                switch (jsonAction){
                    case "addHistory":
                        valuePost =
                                "{" +
                                        "\"Action\":\""+jsonAction+"\", " +
                                        "\"UserId\":\""+session.getUserIdLoggedIn()+"\", " +
                                        "\"BookId\":\""+bookId+"\", " +
                                        "\"InsertTime\":\""+insertTime+"\"" +
                                        "}";
                        params.put("json", valuePost);
                        break;
                    case "addFavourite":
                        valuePost =
                                "{" +
                                        "\"Action\":\""+jsonAction+"\", " +
                                        "\"UserId\":\""+session.getUserIdLoggedIn()+"\", " +
                                        "\"BookId\":\""+bookId+"\", " +
                                        "\"InsertTime\":\""+insertTime+"\"" +
                                        "}";
                        params.put("json", valuePost);
                        break;
                    case "removeHistory":
                        valuePost =
                                "{" +
                                        "\"Action\":\""+jsonAction+"\", " +
                                        "\"UserId\":\""+session.getUserIdLoggedIn()+"\", " +
                                        "\"BookId\":\""+bookId+"\""+
                                        "}";
                        params.put("json", valuePost);
                        break;
                    case "removeFavourite":
                        valuePost =
                                "{" +
                                        "\"Action\":\""+jsonAction+"\", " +
                                        "\"UserId\":\""+session.getUserIdLoggedIn()+"\", " +
                                        "\"BookId\":\""+bookId+"\""+
                                        "}";
                        params.put("json", valuePost);
                        break;
                }
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


//    /**
//     * Get the network info
//     * @param context
//     * @return
//     */
    /*
    public static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    */
//    /**
//     * Check if there is any connectivity
//     * @param context
//     * @return
//     */
    /*
    public static boolean isConnected(Context context){
        NetworkInfo info = ConnectivityReceiver.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    */
//    /**
//     * Check if there is any connectivity to a Wifi network
//     * @param context
//     * @return
//     */
    /*
    public static boolean isConnectedWifi(Context context){
        NetworkInfo info = ConnectivityReceiver.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    */

//    /**
//     * Check if there is any connectivity to a mobile network
//     * @param context
//     * @return
//     */

     /*
    public static boolean isConnectedMobile(Context context){
        NetworkInfo info = ConnectivityReceiver.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    */
//     /**
//     * Check if there is fast connectivity
//     * @param context
//     * @return
//     */
     /*
    public static boolean isConnectedFast(Context context){
        NetworkInfo info = ConnectivityReceiver.getNetworkInfo(context);
        return (info != null && info.isConnected() && ConnectivityReceiver.isConnectionFast(info.getType(),info.getSubtype()));
    }

//    */
///**
//     * Check if the connection is fast
//     * @param type
//     * @param subType
//     * @return
//     */
    /*
    public static boolean isConnectionFast(int type, int subType){
        if(type==ConnectivityManager.TYPE_WIFI){
            return true;
        }else if(type==ConnectivityManager.TYPE_MOBILE){
            switch(subType){
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                *//*
                 * Above API level 7, make sure to set android:targetSdkVersion
                 * to appropriate level to use these
                 *//*
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        }else{
            return false;
        }
    }*/

}