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

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, //GET - API-запрос для получение данных
                url, null, new Response.Listener<JSONObject>() {
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
