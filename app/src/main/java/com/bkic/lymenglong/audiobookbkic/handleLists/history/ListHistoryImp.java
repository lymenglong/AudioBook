package com.bkic.lymenglong.audiobookbkic.handleLists.history;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface ListHistoryImp {

    void CompareDataPhoneWithServer(JSONArray jsonArray);

    void SetTableSelectedData(JSONObject jsonObject) throws JSONException;

    void ShowListFromSelected();

    void LoadListDataFailed(String jsonMessage);

    void RemoveHistorySuccess(String message);

    void RemoveHistoryFailed(String message);

    void RemoveAllHistorySuccess(String message);

    void RemoveAllHistoryFailed(String message);
}
