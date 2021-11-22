package social.app.wesocial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;
import com.vanniktech.emoji.EmojiPopup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import timber.log.Timber;


public class SharePost extends Fragment {

    public static final Data data = new Data();
    public Button btnPost;
    Functions functions;
    public static final String successful = data.requestSuccessful;
    public static final String failed = data.requestFailed;
    public EditText txtPostContent;

    public SharePost() {
        // Required empty public constructor
    }

    public static SharePost newInstance() {

        return new SharePost();

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtPostContent = requireView().findViewById(R.id.txtTimelineContent);
        btnPost = requireView().findViewById(R.id.btnPost);
        functions = new Functions(requireContext());


        //  emoji button click
        ImageView imgSharePostEmoji = requireView().findViewById(R.id.imgSharePostEmoji);

        imgSharePostEmoji.setOnClickListener(view1 -> {
            EmojiPopup emojiPopup;
            emojiPopup = EmojiPopup.Builder.fromRootView(view).build(txtPostContent);
            emojiPopup.toggle(); // Toggles visibility of the Popup.
            emojiPopup.dismiss(); // Dismisses the Popup.
            emojiPopup.isShowing(); // Returns true when Popup is showing.
        });

        txtPostContent.requestFocus();
        if (txtPostContent.requestFocus()) {
            requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        LottieAnimationView lottie = requireActivity().findViewById(R.id.frontpageProgressView);

        btnPost.setOnClickListener(view1 -> {
            functions.hideSoftKeyboard(requireActivity());

            if (txtPostContent.length() > getResources().getInteger(R.integer.max_post_size)) {
                functions.showSnackBarError(getString(R.string.post_Long), requireActivity().findViewById(android.R.id.content), requireActivity().getApplicationContext());
                return;
            }

            if (txtPostContent.length() == 0) {
                functions.showSnackBarError(getString(R.string.empty_post), requireActivity().findViewById(android.R.id.content), requireActivity().getApplicationContext());
                return;
            }
            functions.showProgress(lottie);
            HashMap<String, String> postData = new HashMap<>();
            postData.put("userID", Frontpage.userID);
            postData.put("content", txtPostContent.getText().toString());
            NetworkController networkController = new NetworkController(requireActivity().getApplicationContext(), new NetworkController.IResult() {
                @Override
                public void notifySuccess(String response) throws JSONException {
                    functions.hideProgress(lottie);
                    if (functions.isJsonObject(response)) {
                        Timber.i(response);
                        JSONObject jsonObject = new JSONObject(response);
                        String responseCode = jsonObject.get("responseCode").toString();
                        String message = jsonObject.getString("message");

                        if (responseCode.equals(data.requestSuccessful)) {
                            txtPostContent.setText("");
                            functions.showSnackBar(message, requireActivity().findViewById(android.R.id.content), requireActivity().getApplicationContext());
                            Fragment timelineFragment = Timeline.newInstance();
                            functions.LoadFragment(timelineFragment, "timeline", requireActivity(), true, false);
                        } else {
                            functions.showSnackBarError(message, requireActivity().findViewById(android.R.id.content), requireActivity().getApplicationContext());

                        }

                    }

                }

                @Override
                public void notifyError(VolleyError error) {
                    functions.hideProgress(lottie);
                }
            });

            networkController.PostMethod(data.addPost_API, postData);

        });
    }


}

