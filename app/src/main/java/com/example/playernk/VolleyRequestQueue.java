package com.example.playernk;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VolleyRequestQueue {

    public static void executeRequest(Context ctx, String url, JsonCallback jsonCallback){

        RequestQueue requestQueue = Volley.newRequestQueue(ctx);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("appId", DB.getDbConstant(ctx,"appId"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, //GET - API-запрос для получение данных
                url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                jsonCallback.CallbackObject(response);

            }
        }, new Response.ErrorListener() { // в случае возникновеня ошибки
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);

    }

    public static void executeRequestPost(Context ctx, String url, JsonCallback jsonCallback){

        executeRequestPost(ctx, url, new JSONObject(), jsonCallback);

    }

    public static void executeRequestPost(Context ctx, String url, JSONObject params, JsonCallback jsonCallback){

        RequestQueue requestQueue = Volley.newRequestQueue(ctx);

        try {
            params.put("appId", DB.getDbConstant(ctx,"appId"));
            params.put("userId", DB.getDbConstant(ctx,"userId"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                jsonCallback.CallbackObject(response);

            }
        }, new Response.ErrorListener() { // в случае возникновеня ошибки
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);

    }


}
