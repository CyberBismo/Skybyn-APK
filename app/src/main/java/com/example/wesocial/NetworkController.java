package com.example.wesocial;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

public class NetworkController {
    Context context;
    RequestQueue requestQueue;

    IResult result;


    public NetworkController(Context context, IResult result) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        this.result = result;
    }

    public void PostMethod(String URL, HashMap<String, String> params) {

        StringRequest objectRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        result.notifySuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
                result.notifyError(error);
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {


                return params;
            }

        };

        requestQueue.add(objectRequest);

    }

    public void GetMethod(String URL) {

        StringRequest objectRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        result.notifySuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
                result.notifyError(error);
            }
        });

        requestQueue.add(objectRequest);


    }


    public interface IResult {
        void notifySuccess(String response);

        void notifyError(VolleyError error);
    }
}