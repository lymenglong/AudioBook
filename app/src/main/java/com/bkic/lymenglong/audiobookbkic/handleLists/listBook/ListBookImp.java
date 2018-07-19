package com.bkic.lymenglong.audiobookbkic.handleLists.listBook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface ListBookImp {

    void ShowListFromSelected();

    void LoadListDataFailed(String jsonMessage);

    void SetTableSelectedData(JSONArray jsonArrayResult);
}
