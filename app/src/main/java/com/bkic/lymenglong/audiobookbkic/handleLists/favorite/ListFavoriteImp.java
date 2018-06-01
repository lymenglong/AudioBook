package com.bkic.lymenglong.audiobookbkic.handleLists.favorite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface ListFavoriteImp {

    void CompareDataPhoneWithServer(JSONArray jsonArray);

    void SetTableSelectedData(JSONObject jsonObject) throws JSONException;

    void ShowListFromSelected();

    void LoadListDataFailed(String jsonMessage);

    void RemoveFavoriteSuccess(String message);

    void RemoveFavoriteFailed(String message);

    void RemoveAllFavoriteSuccess(String message);

    void RemoveAllFavoriteFailed(String message);
}
