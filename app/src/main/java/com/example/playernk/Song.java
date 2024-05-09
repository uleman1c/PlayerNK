package com.example.playernk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Song {

    public String id, name, ext, style, description;
    public Boolean nowPlaying, favorite, isNew;

    public Song(String id, String name, String ext, String style, String description, Boolean favorite, Boolean isNew) {
        this.id = id;
        this.name = name;
        this.ext = ext;
        this.style = style;
        this.description = description;
        this.favorite = favorite;
        this.isNew = isNew;

        this.nowPlaying = false;
    }



    public static Song getSongFromJsonObject(JSONObject jsonObject){

        return new Song(
                DefaultJson.getString(jsonObject, "id", ""),
                DefaultJson.getString(jsonObject, "name", ""),
                DefaultJson.getString(jsonObject, "ext", ""),
                DefaultJson.getString(jsonObject, "style", ""),
                DefaultJson.getString(jsonObject, "description", ""),
                DefaultJson.getBoolean(jsonObject, "favorite", false),
                DefaultJson.getBoolean(jsonObject, "new", false)
                );

    }

    public static ArrayList<Song> getFromJsonArray(ArrayList<Song> songs, JSONArray jsonArray){

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
