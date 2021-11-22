package social.app.wesocial;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.HashMap;

import timber.log.Timber;

public class NetworkController {
    Context context;
    RequestQueue requestQueue;
    IResult result;
    Functions functions;


    public NetworkController(Context context, IResult result) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        this.result = result;
        functions = new Functions(context);

    }


    public void PostMethod(String URL, HashMap<String, String> postParams) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, response -> {
            try {
                result.notifySuccess(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            VolleyLog.d("TAG", "Error: " + error.getMessage());
            result.notifyError(error);
        }) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> params;
                params = postParams;
                return params;
            }

        };
        requestQueue.add(postRequest);
    }

    public void GetMethod(String URL) {
        StringRequest GetRequest = new StringRequest(Request.Method.GET, URL,
                response -> {
                    try {
                        result.notifySuccess(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            Timber.e(error.toString());
            result.notifyError(error);
            functions.ShowToast(context.getString(R.string.network_something_wrong));
        });
        requestQueue.add(GetRequest);
    }

    public interface IResult {
        void notifySuccess(String response) throws JSONException;

        void notifyError(VolleyError error);
    }
}