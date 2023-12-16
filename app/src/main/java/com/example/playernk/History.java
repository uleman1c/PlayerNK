package com.example.playernk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class History {

    public String id, date, file_id, name, ext, style, description;
    public Boolean nowPlaying;


    public History(String id, String date, String file_id, String name, String ext, String style, String description) {
        this.id = id;
        this.date = date;
        this.file_id = file_id;
        this.name = name;
        this.ext = ext;
        this.style = style;
        this.description = description;
    }

    public static History FromJsonObject(JSONObject jsonObject){

        return new History(
                DefaultJson.getString(jsonObject, "id", ""),
                DefaultJson.getString(jsonObject, "date", ""),
                DefaultJson.getString(jsonObject, "file_id", ""),
                DefaultJson.getString(jsonObject, "name", ""),
                DefaultJson.getString(jsonObject, "ext", ""),
                DefaultJson.getString(jsonObject, "style", ""),
                DefaultJson.getString(jsonObject, "description", "")
        );

    }

    public static ArrayList<History> getFromJsonArray(ArrayList<History> songs, JSONArray jsonArray){

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            songs.add(FromJsonObject(jsonObject));

        }


        return songs;
    }





}
