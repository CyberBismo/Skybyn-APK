package social.app.wesocial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import social.app.wesocial.databinding.FragmentViewPageBinding;
import timber.log.Timber;

public class ViewPage extends Fragment {


    FragmentViewPageBinding binding;
    LottieAnimationView lottie;
    Data data = new Data();
    public Functions functions;

    static String PAGEID;
    private String mParam2;

    public ViewPage() {
        // Required empty public constructor
    }


    public static ViewPage newInstance(String pageID, String param2) {
        PAGEID = pageID;

        return new ViewPage();
    }

    void loadPageDetails() {
        functions.showProgress(lottie);
        HashMap<String, String> postData = new HashMap<>();
        postData.put("pageID", PAGEID);

        NetworkController networkController = new NetworkController(requireContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                functions.hideProgress(lottie);
                if (functions.isJsonObject(response)) {
                    Timber.i(response);
                    JSONObject jsonObject = new JSONObject(response);
                    String responseCode = jsonObject.get("responseCode").toString();
                    if (responseCode.equals(data.requestSuccessful)) {
                        binding.txtPageName.setText(jsonObject.get("name").toString());
                        binding.txtPageDescription.setText(jsonObject.get("desc").toString());
                        functions.loadPageDrawableThumb(jsonObject.get("logo").toString(), binding.imgPageLogo, false);
                        functions.loadPageDrawableThumb(jsonObject.get("banner").toString(), binding.imgPageBanner, true);
                    }

                } else {
                    functions.ShowToast(getString(R.string.something_wrong));
                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.ShowToast(getString(R.string.network_something_wrong));
            }
        });

        networkController.PostMethod(data.page_content_API, postData);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding = FragmentViewPageBinding.bind(view);
        binding.imgPageLogo.bringToFront();
        functions = new Functions(requireContext());
        lottie = requireActivity().findViewById(R.id.frontpageProgressView);
        loadPageDetails();
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_page, container, false);
    }
}