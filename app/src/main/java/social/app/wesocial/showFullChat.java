package social.app.wesocial;
import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

public class showFullChat extends Fragment {
    private static String chat_username = "";
    private static String chat_avatar = "";
    private static String chat_friendID = "";
    private static Drawable online_status_drawable = null;
    public EditText txtChatMessageContent;
    static Functions  functions;
    Data data = new Data();
    private String OldMessageJson;
    ArrayList<ChatMessageListDataClass> chatMessageListData = new ArrayList<>();
    ScrollView chatScrollView;
    RecyclerView recyclerView;
    ChatMessageAdapter chatMessageAdapter;
    public static  String chatMessageJson= "";
    public showFullChat() {
        // Required empty public constructor
    }


    public static showFullChat newInstance(String param1, Drawable drawable, String param3, String friendID,String chatJson) {
        showFullChat fragment = new showFullChat();
        chat_username = param1;
        online_status_drawable = drawable;
        chat_avatar = param3;
        chat_friendID = friendID;
        chatMessageJson =chatJson;

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
    public void onResume() {
        super.onResume();
        loadAllMessages(chat_friendID);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        functions= new Functions(getContext());
        ImageView imgChatHeaderGoBack = view.findViewById(R.id.imgChatHeaderGoBack);
        ImageView imgChatHeaderOnlineStatus = view.findViewById(R.id.imgChatHeaderOnlineStatus);
        ImageView imgChatHeaderProfilePicture = view.findViewById(R.id.imgChatHeaderProfilePicture);
        TextView txtChatHeaderUsername = view.findViewById(R.id.imgChatUsernameTitle);

        chatScrollView = view.findViewById(R.id.chatScrollView);
        txtChatMessageContent = view.findViewById(R.id.txtChatMessageContent);
        Button btnChatSendMessage = view.findViewById(R.id.btnChatSendMessage);

        imgChatHeaderOnlineStatus.setImageDrawable(online_status_drawable);
        txtChatHeaderUsername.setText(chat_username);
        recyclerView = view.findViewById(R.id.chatRecyclerview);

        functions.loadProfilePictureDrawableThumb(chat_avatar, imgChatHeaderProfilePicture);

        imgChatHeaderGoBack.setOnClickListener(view1 -> {
            functions.hideSoftKeyboard(requireActivity());
            requireActivity().onBackPressed();

        });

        btnChatSendMessage.setOnClickListener(view1 -> {
            if (txtChatMessageContent.getText().toString().equals("")) {
                txtChatMessageContent.setError(getString(R.string.empty_message_content));
                return;
            }
            sendMessage(chat_friendID, txtChatMessageContent.getText().toString());
        });


        //IF JSON WAS SENT BY CLICKING THE MESSAGE ADAPTER, Load the json messages ...No delay
        if (functions.isJsonArray(chatMessageJson)){
            try {
                listChatMessagesOnRecyclerView(chatMessageJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ExecutorService service = Executors.newFixedThreadPool(4);
        service.submit(new Runnable() {
            public void run() {
                //Load Messages
                //LOAD All messages
                loadAllMessages(chat_friendID);
            }
        });

    }

    //CONVERT TO FUNCTION
    public void scrollDown() {
        recyclerView.scrollToPosition(chatMessageListData.size() - 1);
        chatScrollView.post(() -> chatScrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    public void updateChatRecyclerMessages(Boolean iamsender) {

        if (isVisible()){
            chatMessageAdapter = new ChatMessageAdapter(chatMessageListData, requireActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(chatMessageAdapter);
        if (iamsender) {
            //
        }
        chatMessageAdapter.notifyDataSetChanged();
        recyclerView.setItemViewCacheSize(chatMessageListData.size());
        scrollDown();
    }
    }

    public void sendMessage(String friendID, String message) {
        txtChatMessageContent.setText("");
        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);
        postData.put("friendID", friendID);
        postData.put("content", message);

        //ADD MESSAGE TO VIEW WHILE WAITING FOR SEND
        chatMessageListData.add(chatMessageListData.size(), new ChatMessageListDataClass("", message, Frontpage.avatarLink, "sending", Frontpage.userID, Frontpage.userID, Frontpage.loginUsername));
        updateChatRecyclerMessages(true);
        //END

        NetworkController networkController = new NetworkController(requireActivity().getApplicationContext(),
                new NetworkController.IResult() {
                    @Override
                    public void notifySuccess(String response) throws JSONException {
                        Timber.i(response);
                        if (functions.isJsonObject(response)) {
                            JSONObject jsonObject = new JSONObject(response);
                            String responseCode = jsonObject.get("responseCode").toString();
                            if (responseCode.equals("1")) {
                                loadAllMessages(friendID);
                                return;
                            }
                            if (!responseCode.equals("1")) {
                                functions.showSnackBar(getString(R.string.no_message), requireActivity().findViewById(android.R.id.content), requireActivity().getApplicationContext());
                            }

                        }

                    }

                    @Override
                    public void notifyError(VolleyError error) {

                    }
                });

        networkController.PostMethod(data.sendMessage_API, postData);
    }

    private void listChatMessagesOnRecyclerView(String response) throws JSONException {
        OldMessageJson = response;
        JSONArray jsonArray = new JSONArray(response);
        JSONObject jsonObject;

        String content, date, username, online, avatarlink, msgID, friendID, userID, nickName;

        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObject = jsonArray.getJSONObject(i);
            content = jsonObject.get("content").toString();
            avatarlink = jsonObject.get("avatar").toString();
            date = jsonObject.get("date").toString();
            msgID = jsonObject.get("msgID").toString();
            friendID = jsonObject.get("friendID").toString();
            userID = jsonObject.get("userID").toString();
            username = jsonObject.get("username").toString();
            nickName = jsonObject.get("nickname").toString();
            username = jsonObject.get("username").toString();
            chatMessageListData.add(new ChatMessageListDataClass(msgID, content, avatarlink, date, friendID, userID, username));
        }

        updateChatRecyclerMessages(false);


    }

    public void loadAllMessages(String friendID) {

        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);
        postData.put("friendID", friendID);

        NetworkController networkController = new NetworkController(requireActivity().getApplicationContext(),
                new NetworkController.IResult() {
                    @Override
                    public void notifySuccess(String response) throws JSONException {
                        Timber.i(response);
                        if (functions.isJsonArray(response)) {
                            if (!response.equals(chatMessageJson)){
                            listChatMessagesOnRecyclerView(response);
                        }
                        }
                    }

                    @Override
                    public void notifyError(VolleyError error) {

                    }
                });

        networkController.PostMethod(data.showFullMessages_API, postData);
    }


    public void loadAllLatestMessages(String friendID) {
        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);
        postData.put("friendID", friendID);

        NetworkController networkController = new NetworkController(requireActivity().getApplicationContext(),
                new NetworkController.IResult() {
                    @Override
                    public void notifySuccess(String response) throws JSONException {
                        Timber.i(response);
                        if (functions.isJsonArray(response)) {
                            listChatMessagesOnRecyclerView(response);
                        }
                    }

                    @Override
                    public void notifyError(VolleyError error) {

                    }
                });

        networkController.PostMethod(data.showFullMessages_API, postData);
    }
}


