package social.app.wesocial;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.Executor;


public class LoginRegisterForgot extends AppCompatActivity {
    Data data = new Data();
    FrameLayout verify_form;
    String oneTimetoken;
    Executor executor;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;

    private Button forgot_si;
    private Button signup_si;
    private Button forgot_su;
    private Button signin_su;
    private Button signin_f;
    private Button signup_f;
    private EditText txtLoginUserName;
    private EditText txtLoginPassWord;
    private EditText txtUsername;
    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private EditText txtEmail;
    EditText txtOneTimeCode;
    private TextView txtVerify;
    private Button BtnVerify_email;
    public EditText forgot_email;
    public String ErrorMessage = "";
    public SharedPreferences sharedpreferences;
    public Functions functions = new Functions();
    public LottieAnimationView lottieview;

    FrameLayout signup_form;
    FrameLayout forgot_form;
    FrameLayout signin_form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Functions functions = new Functions();

        //FindView by ID, shoul have used DataBinding from the start
        lottieview = findViewById(R.id.loginProgressView);
        signin_su = findViewById(R.id.signin_su);
        signin_f = findViewById(R.id.signin_f);
        signup_si = findViewById(R.id.signup_si);
        signup_f = findViewById(R.id.signup_f);
        forgot_si = findViewById(R.id.forgot_si);
        forgot_su = findViewById(R.id.forgot_su);
        txtVerify = findViewById(R.id.verify_email_check);
        txtLoginUserName = findViewById(R.id.txtUsername);
        txtOneTimeCode = findViewById(R.id.txtOTC);
        txtLoginPassWord = findViewById(R.id.txtPassword);
        txtUsername = findViewById(R.id.Username);
        txtPassword = findViewById(R.id.Password);
        txtConfirmPassword = findViewById(R.id.cPassword);
        txtEmail = findViewById(R.id.Email);
        verify_form = findViewById(R.id.verify_form);
        TextView lblCloseVerify = findViewById(R.id.lblCloseVerify);
        Button btnSign_up = findViewById(R.id.sign_up);
        signin_form = findViewById(R.id.signin_form);
        forgot_form = findViewById(R.id.forgot_form);
        forgot_email = findViewById(R.id.forgot_email);
        signup_form = findViewById(R.id.signup_form);

        Button BtnForgotPassword;
        Button btnSignIn = findViewById(R.id.BtnSignIn);
        Button btnVerifyOTC = findViewById(R.id.btnVerifyOTC);

        lblCloseVerify.setOnClickListener(view -> {
            verify_form.setVisibility(View.INVISIBLE);
            txtOneTimeCode.setText("");
            signup_form.setVisibility(View.VISIBLE);
            signin_form.setVisibility(View.INVISIBLE);
        });

        //INITIALIZE THE SHAREDPREF FILE
        sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        //Init Executor and BioPrompt
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                //BIOMETRCIC
                functions.hideProgress(lottieview);
                checkLoginStatus();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                functions.hideProgress(lottieview);
                errString = getString(R.string.biometric_auth_error);
                ShowToast(errString.toString());
                //LoginRegisterForgot.this.finish();
            }

            @Override
            public void onAuthenticationFailed() {
                functions.hideProgress(lottieview);
                super.onAuthenticationFailed();
                String errorMsg = getString(R.string.biometric_auth_failed);
                ShowToast(errorMsg);
            }
        });


        signin_su.setOnClickListener(v -> toggleSignin());
        signup_si.setOnClickListener(v -> toggleSignup());
        signin_f.setOnClickListener(v -> toggleSignin());
        signup_f.setOnClickListener(v -> toggleSignup());
        forgot_si.setOnClickListener(v -> toggleForgot());
        forgot_su.setOnClickListener(v -> toggleForgot());


        //BUTTON TO VERIFY ONE TIME CODE
        btnVerifyOTC.setOnClickListener(view -> {
            functions.hideSoftKeyboard(LoginRegisterForgot.this);
            if (txtOneTimeCode.getText().toString().equals(oneTimetoken)) {
                //Register User
                performRegistration();
            }

            if (!txtOneTimeCode.getText().toString().equals(oneTimetoken)) {
                Toast.makeText(getApplicationContext(), getString(R.string.invalidOTC), Toast.LENGTH_SHORT).show();
            }

        });

        //BUTTON TO SignIn
        btnSignIn.setOnClickListener(v -> {
            functions.hideSoftKeyboard(LoginRegisterForgot.this);
            if (TextUtils.isEmpty(txtLoginUserName.getText())) {
                ErrorMessage = getString(R.string.username_required);
                txtLoginUserName.setError(ErrorMessage);
                ShowToast(ErrorMessage);
            } else if (TextUtils.isEmpty(txtLoginPassWord.getText())) {
                ErrorMessage = getString(R.string.password_required);
                txtLoginPassWord.setError(ErrorMessage);
                ShowToast(ErrorMessage);
            } else {
                signIn();
            }
        });

        //BUTTON to Register User
        btnSign_up.setOnClickListener(v -> {
            functions.hideSoftKeyboard(LoginRegisterForgot.this);
            if (TextUtils.isEmpty(txtUsername.getText())) {
                ErrorMessage = getString(R.string.username_required);
                txtUsername.setError(ErrorMessage);
                ShowToast(ErrorMessage);
            } else if (TextUtils.isEmpty(txtPassword.getText())) {
                ErrorMessage = getString(R.string.password_required);
                txtPassword.setError(ErrorMessage);
                ShowToast(ErrorMessage);
            } else if (TextUtils.isEmpty(txtConfirmPassword.getText())) {
                ErrorMessage = getString(R.string.confirmation_password_required);
                ShowToast(ErrorMessage);

            } else {
                String password = txtPassword.getText().toString();
                String cpassword = txtConfirmPassword.getText().toString();

                if (password.equals(cpassword)) {
                    // signUp();
                    verifyRegistrationEmail(txtEmail.getText().toString());
                } else {
                    ErrorMessage = getString(R.string.password_unmatch);
                    txtPassword.setError(ErrorMessage);
                    txtConfirmPassword.setError(ErrorMessage);
                    ShowToast(ErrorMessage);
                }
            }
        });

        //BUTTON TO VERIFY Email
        BtnVerify_email = findViewById(R.id.verify_email);
        BtnVerify_email.setOnClickListener(v -> {
            functions.hideSoftKeyboard(LoginRegisterForgot.this);
            if (!functions.validateEmail(txtEmail.getText().toString())) {
                ShowToast(getString(R.string.invalid_email));
            }

            if (TextUtils.isEmpty(txtEmail.getText())) {
                ErrorMessage = getString(R.string.email_required);
                txtEmail.setError(ErrorMessage);
                ShowToast(ErrorMessage);
            } else {
                BtnVerify_email.setVisibility(View.INVISIBLE);
                verifyRegistrationEmail(txtEmail.getText().toString());
            }
        });
        Button BtnCancel_email = findViewById(R.id.cancel_email);
        BtnCancel_email.setOnClickListener(v -> cancelEmail());

        BtnForgotPassword = findViewById(R.id.forgot);
        BtnForgotPassword.setOnClickListener(v -> forgotPassword());

        //Checking if SharedPref contains a Username key
        if (sharedpreferences.contains("username")) {
            Boolean showFingerPrintPrompt;
            showFingerPrintPrompt = sharedpreferences.getBoolean(getString(R.string.biometric_prompt_key), false);
            if (showFingerPrintPrompt) {
                functions.showFingerPrintPrompt(lottieview);
                biometricPrompt();
            }else{
                checkLoginStatus();
            }
        }
    }

    public void forgotPassword() {
        if (forgot_email.getText().toString().equals("")) {
            ShowToast(getString(R.string.email_required));
            return;
        }

        HashMap<String, String> postData = new HashMap<>();
        String mail = forgot_email.getText().toString();
        postData.put("email", mail);
        NetworkController networkController = new NetworkController(this, new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) {
                functions.hideProgress(lottieview);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("responseCode")) {
                        String responseCode = jsonObject.get("responseCode").toString();
                        switch (responseCode) {
                            case "1":
                                ShowToast(jsonObject.get("message").toString());
                                toggleSignin();
                                break;

                            case "0":
                                ShowToast(jsonObject.get("message").toString());
                                break;
                        }

                    }

                    if (!jsonObject.has("responseCode")) {
                        ShowToast(getString(R.string.something_wrong));
                    }

                } catch (JSONException e) {
                    Log.i("E:", e.toString());
                }


            }

            @Override
            public void notifyError(VolleyError error) {
                ShowToast(getString(R.string.network_something_wrong));
            }
        });

        functions.showProgress(lottieview);
        networkController.PostMethod(data.forgotPassword_Api, postData);

        FrameLayout signin_form = findViewById(R.id.signin_form);
        FrameLayout forgot_form = findViewById(R.id.email_form);
        signin_form.setVisibility(View.VISIBLE);
        forgot_form.setVisibility(View.INVISIBLE);
    }

    public void verifyRegistrationEmail(String email) {
        HashMap<String, String> postData = new HashMap<>();
        postData.put("email", email);

        //SIMPLIFIED THE NETWORK CALL - POST PARAMETER
        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) {
                Log.i("Reg Response", response);
                functions.hideProgress(lottieview);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("responseCode")) {
                        String responseCode = jsonObject.get("responseCode").toString();
                        if (responseCode.equals("1")) {
                            ShowToast(jsonObject.get("message").toString());
                            oneTimetoken = jsonObject.get("token").toString();
                            //HIDE SIGNU and SignIn
                            signup_form.setVisibility(View.INVISIBLE);
                            signin_f.setVisibility(View.INVISIBLE);
                            verify_form.setVisibility(View.VISIBLE);
                            verify_form.bringToFront();

                        } else if (responseCode.equals("0")) {
                            ShowToast(jsonObject.get("message").toString());

                        }
                    }
                    if (!jsonObject.has("responseCode")) {
                        ShowToast(getString(R.string.something_wrong));

                    }
                } catch (JSONException e) {

                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottieview);
                ShowToast(getString(R.string.network_something_wrong));
            }
        });
        functions.showProgress(lottieview);
        networkController.PostMethod(data.verifyEmail_Api, postData);
    }


    public void performRegistration() {
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        String email = txtEmail.getText().toString();

        HashMap<String, String> postData = new HashMap<>();
        postData.put("username", username);
        postData.put("password", password);
        postData.put("email", email);

        //SIMPLIFIED THE NETWORK CALL - POST PARAMETER
        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {

            @Override
            public void notifySuccess(String response) {
                Log.i("Reg Response", response);
                functions.hideProgress(lottieview);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("responseCode")) {
                        String responseCode = jsonObject.get("responseCode").toString();
                        if (responseCode.equals("1")) {
                            ShowToast(jsonObject.get("message").toString());
                            signup_form.setVisibility(View.INVISIBLE);
                            verify_form.setVisibility(View.INVISIBLE);
                            txtOneTimeCode.setText("");
                            signin_form.setVisibility(View.VISIBLE);
                        } else if (responseCode.equals("0")) {
                            ShowToast(jsonObject.get("message").toString());

                        }
                    }
                    if (!jsonObject.has("responseCode")) {
                        ShowToast(getString(R.string.something_wrong));

                    }
                } catch (JSONException e) {

                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottieview);
                ShowToast(getString(R.string.network_something_wrong));
            }
        });
        functions.showProgress(lottieview);
        networkController.PostMethod(data.registration_Api, postData);
    }


    public void signIn() {
        functions.showProgress(lottieview);
        String username = txtLoginUserName.getText().toString();
        String password = txtLoginPassWord.getText().toString();

        HashMap<String, String> postData = new HashMap<>();

        postData.put("username", username);
        postData.put("password", password);
        postData.put("login", "");

        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) {
                if (!functions.isJsonObject(response)) {
                    functions.hideProgress(lottieview);
                    Toast.makeText(getApplicationContext(), getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (functions.isJsonObject(response)) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String response_code = jsonResponse.get("responseCode").toString();

                        if (response_code.equals("1")) {
                            functions.hideProgress(lottieview);
                            //Get the UserID from the response...
                            String userID = jsonResponse.getString("userID");
                            //Save Username and password
                            saveUsernameAndPassword(userID, username, password);
                            finish();
                            Intent intent = new Intent(getApplicationContext(), Frontpage.class);
                            intent.putExtra("loginAction", data.login_auth);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), getString(R.string.welcome_back), Toast.LENGTH_SHORT).show();
                        }

                        if (response_code.equals("0")) {
                            functions.hideProgress(lottieview);
                            String errorMsg = jsonResponse.get("message").toString();
                            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                    }


                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottieview);
                Toast.makeText(getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });
        networkController.PostMethod(data.login_Api, postData);
    }


    // Switch forms and buttons
    public void toggleSignup() {
        FrameLayout signin_form = findViewById(R.id.signin_form);
        FrameLayout forgot_form = findViewById(R.id.forgot_form);
        FrameLayout signup_form = findViewById(R.id.signup_form);

        signin_su.setVisibility(View.VISIBLE);
        forgot_su.setVisibility(View.VISIBLE);
        signup_form.setVisibility(View.VISIBLE);
        signup_si.setVisibility(View.INVISIBLE);
        forgot_si.setVisibility(View.INVISIBLE);
        signin_form.setVisibility(View.INVISIBLE);
        signin_f.setVisibility(View.INVISIBLE);
        signup_f.setVisibility(View.INVISIBLE);
        forgot_form.setVisibility(View.INVISIBLE);
    }

    public void cancelEmail() {
        FrameLayout signin_form = findViewById(R.id.signin_form);
        FrameLayout forgot_form = findViewById(R.id.forgot_form);
        FrameLayout signup_form = findViewById(R.id.signup_form);
        FrameLayout email_form = findViewById(R.id.email_form);

        email_form.setVisibility(View.INVISIBLE);
        signin_su.setVisibility(View.VISIBLE);
        forgot_su.setVisibility(View.VISIBLE);
        signup_form.setVisibility(View.VISIBLE);
        signup_si.setVisibility(View.INVISIBLE);
        forgot_si.setVisibility(View.INVISIBLE);
        signin_form.setVisibility(View.INVISIBLE);
        signin_f.setVisibility(View.INVISIBLE);
        signup_f.setVisibility(View.INVISIBLE);
        forgot_form.setVisibility(View.INVISIBLE);


        BtnVerify_email.setVisibility(View.VISIBLE);
        txtVerify.setText("");
    }

    public void toggleForgot() {
        FrameLayout signin_form = findViewById(R.id.signin_form);
        FrameLayout forgot_form = findViewById(R.id.forgot_form);
        FrameLayout signup_form = findViewById(R.id.signup_form);

        signin_su.setVisibility(View.INVISIBLE);
        forgot_su.setVisibility(View.INVISIBLE);
        signup_form.setVisibility(View.INVISIBLE);
        signup_si.setVisibility(View.INVISIBLE);
        forgot_si.setVisibility(View.INVISIBLE);
        signin_form.setVisibility(View.INVISIBLE);
        signin_f.setVisibility(View.VISIBLE);
        signup_f.setVisibility(View.VISIBLE);
        forgot_form.setVisibility(View.VISIBLE);
    }

    public void toggleSignin() {

        signin_su.setVisibility(View.INVISIBLE);
        forgot_su.setVisibility(View.INVISIBLE);
        signup_form.setVisibility(View.INVISIBLE);
        signup_si.setVisibility(View.VISIBLE);
        forgot_si.setVisibility(View.VISIBLE);
        signin_form.setVisibility(View.VISIBLE);
        signin_f.setVisibility(View.INVISIBLE);
        signup_f.setVisibility(View.INVISIBLE);
        forgot_form.setVisibility(View.INVISIBLE);
    }

    public void checkLoginStatus() {
        //user has been logged in before, then set Username and password to intent...
        Intent intent = new Intent(this, Frontpage.class);
        intent.putExtra("loginAction", data.fingerprint_auth);
        startActivity(intent);
        finish();
    }

    public void saveUsernameAndPassword(String userID, String username, String password) {
        SharedPreferences.Editor sharedPrefEditor = sharedpreferences.edit();
        sharedPrefEditor.putString("username", username);
        sharedPrefEditor.putString("password", password);
        sharedPrefEditor.putString("userID", userID);
        sharedPrefEditor.apply();
    }

    public void biometricPrompt() {
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometrics_required))
                .setDescription(getString(R.string.touch_sensor))
                .setNegativeButtonText("Cancel")
                .setConfirmationRequired(false)
                .build();
        biometricPrompt.authenticate(promptInfo);
    }

    //Show Toast Method
    public void ShowToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


}
