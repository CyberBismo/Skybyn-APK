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

import social.app.wesocial.databinding.FragmentCreatePageBinding;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreatePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreatePage extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    FragmentCreatePageBinding binding;
    Functions functions;
    Data data = new Data();


    public CreatePage() {
        // Required empty public constructor
    }


    public static CreatePage newInstance(String param1, String param2) {
        CreatePage fragment = new CreatePage();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        functions = new Functions(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_create_page, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LottieAnimationView lottie = requireActivity().findViewById(R.id.frontpageProgressView);
        binding = FragmentCreatePageBinding.bind(view);


        if (binding.txtCreatePageName.getText().toString() == "") {
            functions.ShowToast(getString(R.string.empty_page_name));
            return;
        }


        if (binding.txtCreatePageDescription.getText().toString() == "") {
            functions.ShowToast(getString(R.string.empty_page_desc));
            return;
        }


        binding.btnCreatePage.setOnClickListener(view1 -> {
            HashMap<String, String> postData = new HashMap<>();

            if (binding.radioCreatePagePrivate.isChecked()) {
                postData.put("special", "private");
            } else {
                postData.put("special", "public");
            }


            functions.showProgress(lottie);


            postData.put("userID", Frontpage.userID);
            postData.put("name", binding.txtCreatePageName.getText().toString());
            postData.put("desc", binding.txtCreatePageDescription.getText().toString());
            postData.put("password", binding.txtCreatePagePassword.getText().toString());


            NetworkController networkController = new NetworkController(requireContext(), new NetworkController.IResult() {

                @Override
                public void notifySuccess(String response) throws JSONException {
                    functions.hideProgress(lottie);
                    Timber.i(response);

                    if (functions.isJsonObject(response)) {
                        JSONObject jsonObject = new JSONObject(response);
                        String responseCode = jsonObject.get("responseCode").toString();
                        String message = jsonObject.get("message").toString();
                        if (responseCode.equals("1")) {
                            functions.ShowToast(getString(R.string.page_created));
                            requireActivity().onBackPressed();
                        }
                        if (responseCode.equals("0")) {
                            functions.ShowToast(message);
                        }

                    }
                }

                @Override
                public void notifyError(VolleyError error) {
                    functions.hideProgress(lottie);
                    functions.ShowToast(getString(R.string.network_something_wrong));
                }
            });

            networkController.PostMethod(data.create_page_API, postData);
        });
    }


}