package social.app.wesocial;

import static com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import static com.google.android.material.tabs.TabLayout.Tab;

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
    String reqResponse = "";
    BrowsePageAdapter browsePageAdapter;
    String searchReqResponse = "";

    public BrowsePages() {
        // Required empty public constructor
    }

    public static BrowsePages newInstance() {
        return new BrowsePages();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        functions = new Functions(requireContext());
        data = new Data();
        lottie = requireActivity().findViewById(R.id.frontpageProgressView);


    }


    void loadJsontoRecycler(String response) throws JSONException {

        Log.i("response", response);
        reqResponse = response;
        searchReqResponse = response;
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
            String pageMembers = (String) jsonObject.get("members");
            String pageAmIAMember = (String) jsonObject.get("member");

            browsePageDataClass.add(new BrowsePageDataClass(pageID, pageAvatarLink, pageLock, pageName, pageDesc, pageMembers, pageAmIAMember));
             browsePageAdapter = new BrowsePageAdapter(browsePageDataClass, requireActivity());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireActivity().getApplicationContext());
            binding.browsePagesRecyclerView.setLayoutManager(mLayoutManager);
            binding.browsePagesRecyclerView.setAdapter(browsePageAdapter);
            browsePageAdapter.notifyDataSetChanged();
        }
    }

    void loadPages(String pageID) {
        // if (reqResponse.equals("")) {
        functions.showProgress(lottie);
        //}

        HashMap<String, String> postData = new HashMap<>();
        NetworkController networkController = new NetworkController(requireContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                Timber.i(response);
                functions.hideProgress(lottie);

                if (functions.isJsonArray(response)) {
                    loadJsontoRecycler(response);

                }
                if (!functions.isJsonArray(response)) {
                    functions.showSnackBarError(getString(R.string.no_page_found), requireActivity().findViewById(android.R.id.content), requireContext());
                    return;
                }
            }


            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                Toast.makeText(requireContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });

        if (pageID.equals("")) {
            networkController.PostMethod(data.page_list_API, postData);
        } else if (pageID.equals("2")) {
            postData.put("userID", Frontpage.userID);
            networkController.PostMethod(data.page_mypages_API, postData);
        } else if (pageID.equals("3")) {
            postData.put("userID", Frontpage.userID);
            networkController.PostMethod(data.page_member_of_API, postData);
        }
    }


    public void performPageSearch(String userID, String keyword) {
        functions.showProgress(lottie);
        HashMap<String, String> postData = new HashMap<>();
        postData.put("name", keyword);
        NetworkController networkController = new NetworkController(requireContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                Log.i("response", response.toString());
                functions.hideProgress(lottie);

                if (!functions.isJsonArray(response)) {
                    functions.showSnackBarError(getString(R.string.no_search_result) + keyword, requireActivity().findViewById(android.R.id.content), requireContext());
                    return;
                }

                if (functions.isJsonArray(response)) {
                    if (binding.tabLayout.getTabCount() < 4) {
                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.search_results)));
                    }
                    loadJsontoRecycler(searchReqResponse);
                    binding.tabLayout.selectTab(binding.tabLayout.getTabAt(3));
                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                Toast.makeText(requireContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });
        networkController.PostMethod(data.page_search_API, postData);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding = FragmentPageBinding.bind(view);
        super.onViewCreated(view, savedInstanceState);

        loadPages("");

        binding.txtCreateNewPage.setOnClickListener(view1 -> {
            Fragment createNewPage = CreatePage.newInstance("", "");
            functions.LoadFragment(createNewPage, "", requireActivity(), false, false);

        });

        binding.pageSearchView.setOnCloseListener(() -> false);
        binding.pageSearchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.equals("")) {
                    performPageSearch(Frontpage.userID, query);
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
                switch (tab.getPosition()) {

                    case 0:
                        loadPages("");
                        break;

                    case 1:
                        loadPages("2");
                        break;

                    case 2:
                        loadPages("3");
                        break;

                    case 3:
                        try {
                            loadJsontoRecycler(searchReqResponse);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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