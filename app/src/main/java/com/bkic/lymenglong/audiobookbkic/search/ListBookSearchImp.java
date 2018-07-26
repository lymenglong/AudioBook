package com.bkic.lymenglong.audiobookbkic.search;

import org.json.JSONException;
import org.json.JSONObject;

public interface ListBookSearchImp {

    void SetTableSelectedData(JSONObject jsonObject) throws JSONException;

    void ShowListFromSelected();

    void LoadListDataFailed(String jsonMessage);
}
