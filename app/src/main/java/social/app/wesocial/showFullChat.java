package social.app.wesocial;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import timber.log.Timber;


public class showFullChat extends Fragment {


    private static  String chat_username = "";
    private static  String chat_avatar= "";
    private static  String chat_friendID = "";
    private static  Drawable online_status_drawable = null;
    public EditText txtChatMessageContent;
    Functions functions = new Functions();
    Data data = new Data();

    public showFullChat() {
        // Required empty public constructor
    }


    public static showFullChat newInstance(String param1, Drawable drawable, String param3,String friendID) {
        showFullChat fragment = new showFullChat();

        chat_username = param1;
        online_status_drawable = drawable;
        chat_avatar = param3;
        chat_friendID = friendID;

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
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return inflater.inflate(R.layout.show_full_chat, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView imgChatHeaderGoBack = view.findViewById(R.id.imgChatHeaderGoBack);
        ImageView imgChatHeaderOnlineStatus = view.findViewById(R.id.imgChatHeaderOnlineStatus);
        ImageView imgChatHeaderProfilePicture = view.findViewById(R.id.imgChatHeaderProfilePicture);
        TextView txtChatHeaderUsername = view.findViewById(R.id.imgChatUsernameTitle);
         txtChatMessageContent = view.findViewById(R.id.txtChatMessageContent);
        Button btnChatSendMessage = view.findViewById(R.id.btnChatSendMessage);
        functions.loadProfilePictureDrawableThumb(chat_avatar,imgChatHeaderProfilePicture);
        imgChatHeaderOnlineStatus.setImageDrawable(online_status_drawable);
        txtChatHeaderUsername.setText(chat_username);


        imgChatHeaderGoBack.setOnClickListener(view1 -> {
            requireActivity().onBackPressed();
        });

        btnChatSendMessage.setOnClickListener(view1 -> {
            if (txtChatMessageContent.getText().toString().equals("")){
                txtChatMessageContent.setError(getString(R.string.empty_message_content));
                return;
            }
            sendMessage(chat_friendID,txtChatMessageContent.getText().toString());

        });

        loadAllMessages(chat_friendID);

        super.onViewCreated(view, savedInstanceState);
    }

    public void sendMessage(String friendID,String message) {
        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);
        postData.put("friendID", friendID);
        postData.put("content", message);

        NetworkController networkController = new NetworkController(requireActivity().getApplicationContext(),
                new NetworkController.IResult() {
                    @Override
                    public void notifySuccess(String response) throws JSONException {
                        Timber.i(response);
                        if (functions.isJsonObject(response)){
                            JSONObject jsonObject = new JSONObject(response);
                            String responseCode = jsonObject.get("responseCode").toString();
                            if(responseCode.equals("1")){
                                txtChatMessageContent.setText("");
                                loadAllMessages(friendID);
                            }

                        }

                    }

                    @Override
                    public void notifyError(VolleyError error) {

                    }
                  });

        networkController.PostMethod(data.sendMessage_API,postData);
}

    public void loadAllMessages(String friendID ) {
        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);
        postData.put("friendID", friendID);

        NetworkController networkController = new NetworkController(requireActivity().getApplicationContext(),
                new NetworkController.IResult() {
                    @Override
                    public void notifySuccess(String response) throws JSONException {
                        Timber.i(response);

                    }

                    @Override
                    public void notifyError(VolleyError error) {

                    }
                });

        networkController.PostMethod(data.showFullMessages_API,postData);
    }
}