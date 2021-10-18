package social.app.wesocial;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;


public class Messages extends Fragment {

    Functions functions = new Functions();
    Data data = new Data();
    LottieAnimationView lottie;
    RecyclerView recyclerView;
    String OldMessageJson = "";


    public Messages() {
        // Required empty public constructor
    }

    public static Messages newInstance(String messagesJson) {

        return new Messages();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    private void listMessagesOnRecyclerView(String response) throws JSONException {
        OldMessageJson = response;
        JSONArray jsonArray = new JSONArray(response);
        ArrayList<MessageListDataClass> messages = new ArrayList<>();
        JSONObject jsonObject;

        String content, date, username,online,avatarlink,msgID,friendID,userID,nickName;

        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObject = jsonArray.getJSONObject(i);
            content = jsonObject.get("content").toString();
            avatarlink = jsonObject.get("avatar").toString();
            date = jsonObject.get("date").toString();
            online = jsonObject.get("online").toString();
            msgID = jsonObject.get("msgID").toString();
            friendID = jsonObject.get("friendID").toString();
            userID = jsonObject.get("userID").toString();
            username = jsonObject.get("username").toString();
            nickName = jsonObject.get("nickname").toString();

            messages.add(new MessageListDataClass(msgID,content,avatarlink,date,friendID,nickName,userID,username,online));
        }

        MessageListAdapter messageListAdapter = new MessageListAdapter(messages,requireActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(messageListAdapter);
        messageListAdapter.notifyDataSetChanged();


    }
    private void loadMessagesRequests() throws JSONException {
        if (!OldMessageJson.equals("")){
            listMessagesOnRecyclerView(OldMessageJson);
        }else{
            functions.showProgress(lottie);
        }
        //functions.showProgressNoBackground(lottie);
        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);


        NetworkController networkController = new NetworkController(requireActivity().getApplicationContext(),
                new NetworkController.IResult() {
                    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
                    @Override
                    public void notifySuccess(String response) throws JSONException {
                        functions.hideProgress(lottie);
                        if (functions.isJsonArray(response)) {
                            Timber.i(response);
                            listMessagesOnRecyclerView(response);
                             }
                             if (!functions.isJsonArray(response)) {
                             Toast.makeText(requireActivity().getApplicationContext(), getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                             }

                        }


                    @Override
                    public void notifyError(VolleyError error) {
                        functions.hideProgress(lottie);
                        Toast.makeText(requireActivity().getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
                    }
                });

        networkController.PostMethod(data.showMessages_API, postData);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        lottie = requireActivity().findViewById(R.id.frontpageProgressView);
        recyclerView = view.findViewById(R.id.messagesRecyclerView);

        if (functions.isJsonArray(pa))
        try {
            loadMessagesRequests();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onViewCreated(view, savedInstanceState);
    }
}

