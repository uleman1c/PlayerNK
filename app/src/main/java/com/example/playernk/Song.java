package com.example.playernk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Song {

    public String id, name, ext;

    public Song(String id, String name, String ext) {
        this.id = id;
        this.name = name;
        this.ext = ext;
    }



    public static Song getSongFromJsonObject(JSONObject jsonObject){

        return new Song(
                DefaultJson.getString(jsonObject, "id", ""),
                DefaultJson.getString(jsonObject, "name", ""),
                DefaultJson.getString(jsonObject, "ext", "")
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
