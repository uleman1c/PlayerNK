package com.example.playernk;

import org.json.JSONException;
import org.json.JSONObject;

public class DefaultJson {

    public static String getString(JSONObject jsonObject, String key, String def){

        String result = def;

        try {
            result = jsonObject.getString(key);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return result;

    }

}
