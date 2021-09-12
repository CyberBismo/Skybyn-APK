package social.app.wesocial;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class SharePost extends Fragment {

public TextView txtPostContent;
public Button btnPost;
Functions functions= new Functions();
Data data = new Data();

    public SharePost() {
        // Required empty public constructor
    }

    public static SharePost newInstance(String param1, String param2) {
        SharePost fragment = new SharePost();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_share_post, container, false);
    }

    public void showAlertDialog(String title, String Message, Boolean Cancelable){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
        builder.setTitle(title)
                .setCancelable(Cancelable)
                .setMessage(Message)
                .setPositiveButton(getString(R.string.Continue), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }})

                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtPostContent = getView().findViewById(R.id.txtTimelineContent);
        btnPost = getView().findViewById(R.id.btnPost);


        txtPostContent.requestFocus();
        if(txtPostContent.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        LottieAnimationView lottie = getActivity().findViewById(R.id.frontpageProgressView);

        btnPost.setOnClickListener( view1 -> {
            functions.hideSoftKeyboard(getActivity());

            if (txtPostContent.length() > getResources().getInteger(R.integer.max_post_size)) {
                functions.showSnackBarError(getString(R.string.post_Long), getActivity().findViewById(android.R.id.content), getActivity().getApplicationContext());
                return;
            }

                if (txtPostContent.length() == 0 ){
                    functions.showSnackBarError(getString(R.string.empty_post),getActivity().findViewById(android.R.id.content),getActivity().getApplicationContext());
                    return;
            }
                functions.showProgress(lottie);
                HashMap<String,String> postData = new HashMap<>();
                postData.put("userID",Frontpage.userID);
                postData.put("content",txtPostContent.getText().toString());
                NetworkController networkController = new NetworkController(getActivity().getApplicationContext(), new NetworkController.IResult() {
                    @Override
                    public void notifySuccess(String response) throws JSONException {
                    functions.hideProgress(lottie);
                    if (functions.isJsonObject(response.toString())) {
                            Log.i("Json", response);
                            JSONObject jsonObject = new JSONObject(response.toString());
                            String responseCode = jsonObject.get("responseCode").toString();
                            String message = jsonObject.getString("message").toString();
                            switch (responseCode){
                                case "1":
                                    txtPostContent.setText("");
                                    functions.showSnackBar(message,getActivity().findViewById(android.R.id.content),getActivity().getApplicationContext());
                                    Fragment timelineFragment = Timeline.newInstance("","");
                                    functions.LoadFragment(timelineFragment, "timelinePosts",getActivity(),true);
                                    break;

                                    case "0":
                                    functions.showSnackBarError(message,getActivity().findViewById(android.R.id.content),getActivity().getApplicationContext());
                                    break;

                            }

                        }

                        }

                    @Override
                    public void notifyError(VolleyError error) {
                        functions.hideProgress(lottie);
                    }
                });

                networkController.PostMethod(data.post_Api,postData);

        });
    }



}

