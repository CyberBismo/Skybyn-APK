package social.app.wesocial;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Search#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Search extends Fragment {
public static String response,keyword;
public static RecyclerView recyclerView;



    public Search() {
        // Required empty public constructor
    }

    public static Search newInstance(String param1, String param2) {
        Search fragment = new Search();
       response = param2;
       keyword = param1;

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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.searchRecyclerView);
        try {
            showSearchResults(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    public void showSearchResults(String response) throws JSONException {
        Timber.i(response);
        String searchUsername;
        String searchUserNickname;
        String searchUserID;
        String searchUserAvatarLink;
        String searchUserOnline;
        String isSearchedUserAFriend;

        JSONArray jsonArray = new JSONArray(response);
        ArrayList<UserDataClass> userSearchData = new ArrayList<>();
        JSONObject jsonObject;


        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObject = jsonArray.getJSONObject(i);
            searchUserAvatarLink = (String) jsonObject.get("avatar");
            searchUserOnline = jsonObject.get("online").toString();
            searchUsername = jsonObject.get("username").toString();
            isSearchedUserAFriend = jsonObject.get("friends").toString();
            searchUserNickname = jsonObject.get("nickname").toString();
            searchUserID = jsonObject.get("id").toString();

            userSearchData.add(new UserDataClass(searchUserID,searchUserAvatarLink,searchUserNickname,searchUsername,isSearchedUserAFriend,searchUserOnline));
        }

        userSearchAdapter userSearchAdapter = new userSearchAdapter(userSearchData);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(userSearchAdapter);
        userSearchAdapter.notifyDataSetChanged();
        TextView txtSearchTitle;


        txtSearchTitle = requireActivity().findViewById(R.id.txtSearchTitle);
        txtSearchTitle.setText("Search Results for:"+keyword);

        Handler handler = new Handler();
        handler.postDelayed(() -> Frontpage.searchView.clearFocus(),2000);

    }

}