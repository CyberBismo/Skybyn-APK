package social.app.wesocial;

import static com.google.android.material.tabs.TabLayout.*;

import android.os.Bundle;
import android.util.Log;
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
import java.util.Objects;

import social.app.wesocial.databinding.FragmentPageBinding;
import timber.log.Timber;


public class BrowsePages extends Fragment {

    FragmentPageBinding binding;
    Functions functions;
    LottieAnimationView lottie;
    Data data;

    public BrowsePages() {
        // Required empty public constructor
    }

    public static BrowsePages newInstance() {
        BrowsePages fragment = new BrowsePages();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        functions = new Functions(requireContext());
        data = new Data();
        lottie = requireActivity().findViewById(R.id.frontpageProgressView);

    }

    void loadPages(String userID) {
        functions.showProgress(lottie);
        HashMap<String, String> postData = new HashMap<>();

        NetworkController networkController = new NetworkController(requireContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                Timber.i(response.toString());
                functions.hideProgress(lottie);


                if (functions.isJsonArray(response)) {
                    Log.i("response", response);

                String pageID;
                String pageAvatarLink;
                String pageLock;
                String pageName;
                String pageDesc = "";

                JSONArray jsonArray = new JSONArray(response);
                ArrayList<BrowsePageDataClass> browsePageDataClass = new ArrayList<>();
                JSONObject jsonObject;

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);

                    pageAvatarLink = (String) jsonObject.get("logo");
                    pageID = (String) jsonObject.get("page_id");
                    pageName = (String) jsonObject.get("name");
                    pageLock = (String) jsonObject.get("lock");
                    pageDesc = (String) jsonObject.get("desc");

                    browsePageDataClass.add(new BrowsePageDataClass(pageID, pageAvatarLink, pageLock, pageName, pageDesc));

                    BrowsePageAdapter browsePageAdapter = new BrowsePageAdapter(browsePageDataClass);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireActivity().getApplicationContext());
                    binding.browsePagesRecyclerView.setLayoutManager(mLayoutManager);
                    binding.browsePagesRecyclerView.setAdapter(browsePageAdapter);
                    browsePageAdapter.notifyDataSetChanged();
                }

                    if (!functions.isJsonArray(response)) {
                        functions.showSnackBarError(getString(R.string.no_page_found), requireActivity().findViewById(android.R.id.content), requireContext());
                        return;
                    }


                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                Toast.makeText(requireContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });

        if (userID.equals("")) {
            networkController.PostMethod(data.page_list_API, postData);
        } else {
            networkController.PostMethod(data.page_member_of_API, postData);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding = FragmentPageBinding.bind(view);
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        loadPages("");

        binding.pageSearchView.setOnCloseListener(() -> false);
        binding.pageSearchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.equals("")) {
                }

                functions.hideSoftKeyboard(requireActivity());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (!query.equals("")) {


                }

                return true;
            }
        });

        Objects.requireNonNull(binding.tabLayout).addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                switch (tab.getPosition()){

                    case 0:
                        loadPages("");
                        break;

                    case 1:
                        functions.ShowToast("Hello there");
                        break;

                    case 2:
                        functions.ShowToast("Hello there favourite");
                        break;

                }
            }

            @Override
            public void onTabUnselected(Tab tab) {

            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page, container, false);
    }
}