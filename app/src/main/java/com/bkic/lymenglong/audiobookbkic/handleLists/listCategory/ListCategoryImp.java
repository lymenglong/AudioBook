package com.bkic.lymenglong.audiobookbkic.handleLists.listCategory;

import org.json.JSONArray;
import org.json.JSONException;

public interface ListCategoryImp {

    void CompareDataPhoneWithServer(JSONArray jsonArray);

    void ShowListFromSelected();

    void LoadListDataFailed(String jsonMessage);

    void SetTableSelectedData(JSONArray jsonArray) throws JSONException;
}
