package social.app.wesocial;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import timber.log.Timber;


public class Profile extends Fragment {
    Functions functions;
    Data data = new Data();
    LottieAnimationView lottie;

    String firstName, lastName, middleName, nickName, bio;
    String currentPassword, newPassword, confirmNewPassword;
    String newEmail, oldEmail;


    TextView txtProfilefirstName, txtProfilelastName, txtProfilemiddleName, txtProfilenickName, txtProfileAboutMe;
    TextView txtProfileCurrentPassword, txtProfileNewPassword, txtProfileConfirmNewPassword, txtProfileEmail;

    Button btnUpdateProfile, btnUpdateProfileEmail, btnUpdateProfilePassword;


    public Profile() {
    }

    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lottie = requireActivity().findViewById(R.id.frontpageProgressView);
        functions = new Functions(requireContext());
        txtProfilefirstName = requireActivity().findViewById(R.id.txtProfileFirstname);
        txtProfilelastName = requireActivity().findViewById(R.id.txtProfileLastname);
        txtProfilemiddleName = requireActivity().findViewById(R.id.txtProfileMiddlename);
        txtProfilenickName = requireActivity().findViewById(R.id.txtProfileNickname);
        txtProfileAboutMe = requireActivity().findViewById(R.id.txtProfileAboutMe);

        //EMAIL
        txtProfileEmail = requireActivity().findViewById(R.id.txtProfileEmail);
        //Passwords
        txtProfileCurrentPassword = requireActivity().findViewById(R.id.txtProfileCurrentPassword);
        txtProfileNewPassword = requireActivity().findViewById(R.id.txtProfileNewPassword);
        txtProfileConfirmNewPassword = requireActivity().findViewById(R.id.txtProfileConfirmNewPassword);
        //Buttons
        btnUpdateProfile = requireActivity().findViewById(R.id.btnUpdateProfile);
        btnUpdateProfileEmail = requireActivity().findViewById(R.id.btnUpdateProfileEmail);
        btnUpdateProfilePassword = requireActivity().findViewById(R.id.btnUpdateProfilePassword);
        //ASSIGNING
        txtProfilefirstName.setText(Frontpage.firstName);
        txtProfilelastName.setText(Frontpage.lastName);
        txtProfilemiddleName.setText(Frontpage.middleName);
        txtProfilenickName.setText(Frontpage.nickName);
        txtProfileAboutMe.setText(Frontpage.aboutMe);
        txtProfileEmail.setText(Frontpage.email);

        //EVENTS
        btnUpdateProfilePassword.setOnClickListener(view1 -> {
            functions.hideSoftKeyboard(requireActivity());
            updatePasswordRequest();
        });

        btnUpdateProfileEmail.setOnClickListener(view1 -> {
            functions.hideSoftKeyboard(requireActivity());
            updateEmailRequest();

        });
    }


    public void updateProfile() {

    }

    public void updateEmailRequest() {
        newEmail = txtProfileEmail.getText().toString();

        if (newEmail.equalsIgnoreCase(Frontpage.email)) {
            txtProfileEmail.setError(requireActivity().getString(R.string.email_the_same));
            return;
        }

        if (newEmail.equals("")) {
            txtProfileEmail.setError(requireActivity().getString(R.string.email_required));
            return;
        }

        if (!functions.validateEmail(newEmail)) {
            txtProfileEmail.setError(requireActivity().getString(R.string.invalid_email));
            return;
        }


        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);
        postData.put("email", Frontpage.email);
        postData.put("new_email", newEmail);

        functions.showProgress(lottie);

        NetworkController networkController = new NetworkController(requireContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                if (functions.isJsonObject(response)) {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String responseCode = jsonObject.get("responseCode").toString();
                    String message = jsonObject.get("message").toString();

                    if (responseCode.equals("0")) {
                        functions.showSnackBarError(message, requireActivity().findViewById(android.R.id.content), requireActivity().getApplicationContext());
                    }

                    if (responseCode.equals("1")) {
                        String token = jsonObject.get("token").toString();
                        AlertDialog.Builder alertName = new AlertDialog.Builder(requireContext(), R.style.AlertDialog_AppCompat_Light);
                        final EditText editTextName1 = new EditText(requireContext());
                        alertName.setTitle(getString(R.string.enter_the_verification_code));
                        alertName.setView(editTextName1);
                        LinearLayout layoutName = new LinearLayout(requireContext());
                        layoutName.setOrientation(LinearLayout.VERTICAL);
                        layoutName.addView(editTextName1); //displays the user input bar
                        alertName.setView(layoutName);

                        alertName.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        });

                        alertName.setPositiveButton("Continue", (dialog, whichButton) -> {
                            EditText dialogEditext = editTextName1; // variable to collect user input
                            if (!dialogEditext.getText().toString().equals(token)) {
                                functions.hideSoftKeyboard(requireActivity());
                                functions.showSnackBarError(getString(R.string.invalid_verification_code), requireActivity().findViewById(android.R.id.content), requireActivity().getApplicationContext());
                                dialogEditext.setText("");
                            }

                            if (dialogEditext.getText().toString().equals(token)) {
                                functions.hideSoftKeyboard(requireActivity());
                                HashMap<String, String> postData = new HashMap<>();
                                postData.put("userID", Frontpage.userID);
                                postData.put("new_email", newEmail);
                                //postData.put("email", Frontpage.email);
                                functions.showProgress(lottie);
                                NetworkController networkController1 = new NetworkController(requireContext(), new NetworkController.IResult() {
                                    @Override
                                    public void notifySuccess(String response) throws JSONException {
                                        functions.hideProgress(lottie);
                                        if (functions.isJsonObject(response)) {
                                            JSONObject jsonObject = new JSONObject(response.toString());
                                            String responseCode = jsonObject.get("responseCode").toString();
                                            String message = jsonObject.get("message").toString();
                                            if (responseCode.equals("1")) {
                                                functions.showSnackBar(message, requireActivity().findViewById(android.R.id.content), requireActivity().getApplicationContext());
                                                functions.hideSoftKeyboard(requireActivity());
                                            }

                                            if (responseCode.equals("0")) {
                                                functions.hideSoftKeyboard(requireActivity());
                                                functions.showSnackBarError(message, requireActivity().findViewById(android.R.id.content), requireActivity().getApplicationContext());
                                            }
                                        }
                                    }

                                    @Override
                                    public void notifyError(VolleyError error) {
                                        functions.hideProgress(lottie);
                                    }
                                });

                                networkController1.PostMethod(data.updateEmail_Api, postData);
                            }
                        });
                        alertName.show();
                    }

                }
            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                functions.showSnackBarError(requireActivity().getString(R.string.network_something_wrong), requireActivity().findViewById(android.R.id.content), requireActivity().getApplicationContext());
            }
        });

        networkController.PostMethod(data.sendEmailToken_Api, postData);
    }


    public void updatePasswordRequest() {
        currentPassword = txtProfileCurrentPassword.getText().toString();
        newPassword = txtProfileNewPassword.getText().toString();
        confirmNewPassword = txtProfileConfirmNewPassword.getText().toString();

        if (currentPassword.equals("")) {
            txtProfileCurrentPassword.setError(requireActivity().getString(R.string.password_required));
            return;
        }

        if (!currentPassword.equals(Frontpage.loginPassword)) {
            txtProfileCurrentPassword.setError(requireActivity().getString(R.string.invalid_currentPassword));
            return;
        }

        if (newPassword.equals("")) {
            txtProfileNewPassword.setError(requireActivity().getString(R.string.password_required));
            return;
        }
        if (confirmNewPassword.equals("")) {
            txtProfileConfirmNewPassword.setError(requireActivity().getString(R.string.password_required));
            return;
        }


        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);
        postData.put("old_pw", currentPassword);
        postData.put("new_pw", newPassword);
        postData.put("cnew_pw", confirmNewPassword);

        functions.showProgress(lottie);
        btnUpdateProfilePassword.setEnabled(false);

        NetworkController networkController = new NetworkController(requireContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                functions.hideProgress(lottie);
                btnUpdateProfilePassword.setEnabled(true);
                Timber.i(response);
                if (functions.isJsonObject(response)) {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String responseCode = jsonObject.get("responseCode").toString();
                    String message = jsonObject.get("message").toString();
                    switch (responseCode) {
                        case "1":
                            txtProfileCurrentPassword.setText("");
                            txtProfileNewPassword.setText("");
                            txtProfileConfirmNewPassword.setText("");
                            functions.showSnackBar(message, requireActivity().findViewById(android.R.id.content), requireActivity().getApplicationContext());
                            SharedPreferences.Editor sharedPrefEditor = Frontpage.sharedpreferences.edit();
                            sharedPrefEditor.putString("password", newPassword);
                            sharedPrefEditor.clear();
                            sharedPrefEditor.apply();

                            Intent intent = new Intent(requireContext(), LoginRegisterForgot.class);
                            startActivity(intent);
                            break;

                        default:
                            functions.showSnackBarError(message, requireActivity().findViewById(android.R.id.content), requireActivity().getApplicationContext());
                            break;
                    }
                }
            }

            @Override
            public void notifyError(VolleyError error) {
                btnUpdateProfilePassword.setEnabled(true);
                functions.hideProgress(lottie);
                functions.showSnackBarError(getString(R.string.network_something_wrong), requireActivity().findViewById(android.R.id.content), requireActivity().getApplicationContext());
            }
        });
        networkController.PostMethod(data.updatePassword_Api, postData);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}