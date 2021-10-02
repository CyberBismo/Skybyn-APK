package social.app.wesocial;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class userTimeline extends Fragment {


    public static String userID = "";
    public static LottieAnimationView lottie;
    public Functions functions = new Functions();
    public Data data = new Data();
    public ImageView imgUserCoverPhoto, imgUserProfilePhoto;
    public TextView txtUserProfileFullname, txtUserProfileUsername;
    private String mParam1;

    public userTimeline() {

    }

    public static userTimeline newInstance(String param1, String param2) {
        userTimeline fragment = new userTimeline();
        userID = param1;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    public void loadUserProfile(String userID) {
        functions.showProgress(lottie);
        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", userID);
        NetworkController networkController = new NetworkController(requireActivity().getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                //Load profile picture thumb after profile loads.
                functions.hideProgress(lottie);
                if (functions.isJsonObject(response)) {
                    Log.i("Json", response);
                    JSONObject jsonObject = new JSONObject(response);
                    String responseCode = jsonObject.get("responseCode").toString();

                    if (responseCode.equals("1")) {
                        //Start the PUSH SERVICE after successful Login
                        String username = jsonObject.getString("username");
                        String email = jsonObject.getString("email");
                        String avatarLink = jsonObject.getString("avatar");
                        String firstName = jsonObject.get("fname").toString();
                        String lastName = jsonObject.get("lname").toString();
                        String nickName = jsonObject.get("nickname").toString();
                        String middleName = jsonObject.get("mname").toString();
                        String userRank = jsonObject.get("rank").toString();
                        String userTitle = jsonObject.get("title").toString();
                        String deactivated = jsonObject.get("deactivated").toString();
                        txtUserProfileUsername.setText(username);

                        if (firstName.equals("") || lastName.equals("")) {
                            txtUserProfileFullname.setText("------");
                        } else {
                            txtUserProfileFullname.setText(firstName.concat(" ").concat(lastName));
                        }

                        functions.loadProfilePictureDrawableThumb(avatarLink, imgUserProfilePhoto);
                        functions.loadProfilePictureDrawableThumb(avatarLink, imgUserCoverPhoto);
                        imgUserCoverPhoto.setColorFilter(R.color.dark_gray, PorterDuff.Mode.OVERLAY);
                        imgUserProfilePhoto.bringToFront();


                    }
                }
            }


            @Override
            public void notifyError(VolleyError error) {

            }
        });
        networkController.PostMethod(data.profile_Api, postData);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        lottie = requireActivity().findViewById(R.id.frontpageProgressView);

        imgUserProfilePhoto = requireActivity().findViewById(R.id.userProfilePhoto);
        imgUserCoverPhoto = requireActivity().findViewById(R.id.userProfileCoverPhoto);
        txtUserProfileFullname = requireActivity().findViewById(R.id.txtUserProfileFullname);
        txtUserProfileUsername = requireActivity().findViewById(R.id.txtUserProfileUsername);
        loadUserProfile(userID);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_timeline, container, false);


    }
}