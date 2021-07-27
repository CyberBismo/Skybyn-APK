package social.app.wesocial;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class NetworkController {
    Context context;
    RequestQueue requestQueue;
    IResult result;


    public NetworkController(Context context, IResult result) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        this.result = result;
    }


    public void PostMethod(String URL,Map<String,String> postParams) {
        StringRequest postRequest = new StringRequest(Request.Method.POST,   URL, new Response.Listener<String>() {
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
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                result.notifyError(error);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params = postParams;
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/x-www-form-urlencoded");
                //headers.put("abc", "value");
                return headers;
            }
        };
        requestQueue.add(postRequest);
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