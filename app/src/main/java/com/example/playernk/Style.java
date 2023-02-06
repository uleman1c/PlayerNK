package com.example.playernk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Style {

    public String id, name, ext, style, description;

    public Style(String id, String name, String ext, String style, String description) {
        this.id = id;
        this.name = name;
        this.ext = ext;
        this.style = style;
        this.description = description;
    }



    public static Style getSongFromJsonObject(JSONObject jsonObject){

        return new Style(
                DefaultJson.getString(jsonObject, "id", ""),
                DefaultJson.getString(jsonObject, "name", ""),
                DefaultJson.getString(jsonObject, "ext", ""),
                DefaultJson.getString(jsonObject, "style", ""),
                DefaultJson.getString(jsonObject, "description", "")
                );

    }

    public static ArrayList<Style> getFromJsonArray(ArrayList<Style> songs, JSONArray jsonArray){

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            songs.add(getSongFromJsonObject(jsonObject));

        }


        return songs;
    }
}
