package com.bkic.lymenglong.audiobookbkic.handleLists.listChapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface ListChapterImp {
    void CompareDataPhoneWithServer(JSONArray jsonArray);

    void SetTableSelectedData(JSONObject jsonObject) throws JSONException;

    void ShowListFromSelected();

    void LoadListDataFailed(String jsonMessage);

    void SetUpdateBookDetail(JSONObject jsonObject) throws JSONException;

    void SetTableSelectedData(JSONArray jsonArrayChapter);

}
