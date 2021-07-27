package social.app.wesocial;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

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

        StringRequest PostRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            result.notifySuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

        requestQueue.add(PostRequest);

    }

    public void GetMethod(String URL) {

        StringRequest GetRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            result.notifySuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
                result.notifyError(error);
            }
        });
        requestQueue.add(GetRequest);


    }


    public interface IResult {
        void notifySuccess(String response) throws JSONException;

        void notifyError(VolleyError error);
    }
}