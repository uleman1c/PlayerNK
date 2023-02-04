package com.example.playernk;

import org.json.JSONArray;
import org.json.JSONObject;

public interface JsonCallback {

    void CallbackObject(JSONObject jsonObject);

    void CallbackArray(JSONArray jsonArray);

}
