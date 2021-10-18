package social.app.wesocial;


//import android.content.Context;
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
    public EditText txtforgot_email;
    public String ErrorMessage = "";
    public SharedPreferences sharedpreferences;
    public Functions functions;
    public LottieAnimationView lottieview;
    Data data = new Data();
    FrameLayout verify_form;
    String oneTimetoken;
    Executor executor;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    EditText txtOneTimeCode;
    FrameLayout signup_form;
    FrameLayout forgot_form;
    FrameLayout Signin_form;
    private Button login_btnShowForgotForm;


    private EditText txtLoginUserName;
    private EditText txtLoginPassWord;
    private EditText txtUsername;
    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private EditText txtEmail;
    private TextView txtVerify;
    private Button BtnVerify_email;
    private Button login_btnShowSignupForm, login_btnShowLoginForm;

    public void setFrameLayoutVisibility(FrameLayout frameLayout) {
        FrameLayout layout = findViewById(R.id.mainFrameLayout);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            if (v instanceof FrameLayout) {
                if (v == frameLayout) {
                    v.setVisibility(View.VISIBLE);
                    v.bringToFront();
                } else {
                    v.setVisibility(View.INVISIBLE);

                }
            }
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        functions = new Functions(this);

        LottieAnimationView lottieview = findViewById(R.id.loginProgressView);

        login_btnShowForgotForm = findViewById(R.id.login_btnShowForgotForm);
        login_btnShowSignupForm = findViewById(R.id.login_btnShowSignupForm);
         login_btnShowLoginForm = findViewById(R.id.login_btnShowLoginForm);

        txtVerify = findViewById(R.id.verify_email_check);
        txtLoginUserName = findViewById(R.id.txtUsername);
        txtOneTimeCode = findViewById(R.id.txtOTC);
        txtLoginPassWord = findViewById(R.id.txtPassword);
        txtUsername = findViewById(R.id.Username);
        txtPassword = findViewById(R.id.Password);
        txtConfirmPassword = findViewById(R.id.cPassword);
        txtEmail = findViewById(R.id.Email);
        txtforgot_email = findViewById(R.id.forgot_email);

        TextView lblCloseVerify = findViewById(R.id.lblCloseVerify);
        Button btnSign_up = findViewById(R.id.btnProcessSignUp);

        //DECLARING FORM

        Signin_form = findViewById(R.id.signin_form);
        forgot_form = findViewById(R.id.forgot_form);
        signup_form = findViewById(R.id.signup_form);
        verify_form = findViewById(R.id.verify_form);



        Button BtnForgotPassword;
        Button btnProcessSignIn = findViewById(R.id.BtnProcessLogin);
        Button btnVerifyOTC = findViewById(R.id.btnVerifyOTC);

        lblCloseVerify.setOnClickListener(view -> {
           setFrameLayoutVisibility(verify_form);
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
                functions.ShowToast(errString.toString());
                //LoginRegisterForgot.this.finish();
            }

            @Override
            public void onAuthenticationFailed() {
                functions.hideProgress(lottieview);
                super.onAuthenticationFailed();
                String errorMsg = getString(R.string.biometric_auth_failed);
                functions.ShowToast(errorMsg);
            }
        });


        login_btnShowForgotForm.setOnClickListener(v -> toggleForgot());
        login_btnShowSignupForm.setOnClickListener(v->toggleSignup());
        login_btnShowLoginForm.setOnClickListener(v->toggleSignin();

        //BUTTON TO VERIFY ONE TIME CODE
        btnVerifyOTC.setOnClickListener(view -> {
            functions.hideSoftKeyboard(LoginRegisterForgot.this);
            if (txtOneTimeCode.getText().toString().equals(oneTimetoken)) {
                //Register User
                performRegistrationRequests();
            }

            if (!txtOneTimeCode.getText().toString().equals(oneTimetoken)) {
                Toast.makeText(getApplicationContext(), getString(R.string.invalidOTC), Toast.LENGTH_SHORT).show();
            }

        });

        //BUTTON TO SignIn
        btnProcessSignIn.setOnClickListener(v -> {
            functions.hideSoftKeyboard(LoginRegisterForgot.this);
            if (TextUtils.isEmpty(txtLoginUserName.getText())) {
                ErrorMessage = getString(R.string.username_required);
                txtLoginUserName.setError(ErrorMessage);
                functions.ShowToast(ErrorMessage);
            } else if (TextUtils.isEmpty(txtLoginPassWord.getText())) {
                ErrorMessage = getString(R.string.password_required);
                txtLoginPassWord.setError(ErrorMessage);
                functions.ShowToast(ErrorMessage);
            } else {
                signInRequests();
            }
        });

        //BUTTON to Register User
        btnSign_up.setOnClickListener(v -> {
            functions.hideSoftKeyboard(LoginRegisterForgot.this);
            if (TextUtils.isEmpty(txtUsername.getText())) {
                ErrorMessage = getString(R.string.username_required);
                txtUsername.setError(ErrorMessage);
                functions.ShowToast(ErrorMessage);
            } else if (TextUtils.isEmpty(txtPassword.getText())) {
                ErrorMessage = getString(R.string.password_required);
                txtPassword.setError(ErrorMessage);
                functions.ShowToast(ErrorMessage);
            } else if (TextUtils.isEmpty(txtConfirmPassword.getText())) {
                ErrorMessage = getString(R.string.confirmation_password_required);
                functions.ShowToast(ErrorMessage);

            } else {
                String password = txtPassword.getText().toString();
                String cpassword = txtConfirmPassword.getText().toString();

                if (password.equals(cpassword)) {
                    // signUp();
                    verifyRegistrationEmailRequests(txtEmail.getText().toString());
                } else {
                    ErrorMessage = getString(R.string.password_unmatch);
                    txtPassword.setError(ErrorMessage);
                    txtConfirmPassword.setError(ErrorMessage);
                    functions.ShowToast(ErrorMessage);
                }
            }
        });

        //BUTTON TO VERIFY Email
        BtnVerify_email = findViewById(R.id.btnSendVerifyEmail);
        BtnVerify_email.setOnClickListener(v -> {
            functions.hideSoftKeyboard(LoginRegisterForgot.this);
            if (!functions.validateEmail(txtEmail.getText().toString())) {
                functions.ShowToast(getString(R.string.invalid_email));
            }

            if (TextUtils.isEmpty(txtEmail.getText())) {
                ErrorMessage = getString(R.string.email_required);
                txtEmail.setError(ErrorMessage);
                functions.ShowToast(ErrorMessage);
            } else {
                BtnVerify_email.setVisibility(View.INVISIBLE);
                verifyRegistrationEmailRequests(txtEmail.getText().toString());
            }
        });
        Button BtnCancel_email = findViewById(R.id.btnCancelVerifyEmail);
        BtnCancel_email.setOnClickListener(v -> cancelEmail());

        BtnForgotPassword = findViewById(R.id.btnSendPasswordRequest);
        BtnForgotPassword.setOnClickListener(v -> forgotPasswordRequests());

        //Checking if SharedPref contains a Username key
        if (sharedpreferences.contains("username")) {
            Boolean showFingerPrintPrompt;
            showFingerPrintPrompt = sharedpreferences.getBoolean(getString(R.string.biometric_prompt_key), false);
            if (showFingerPrintPrompt) {
                functions.showFingerPrintPrompt(lottieview);
                biometricPrompt();
            } else {
                checkLoginStatus();
            }
        }
    }

    public void forgotPasswordRequests() {
        if (txtforgot_email.getText().toString().equals("")) {
            functions.ShowToast(getString(R.string.email_required));
            return;
        }

        HashMap<String, String> postData = new HashMap<>();
        String mail = txtforgot_email.getText().toString();
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
                                functions.ShowToast(jsonObject.get("message").toString());
                                toggleSignin();
                                break;

                            case "0":
                                functions.ShowToast(jsonObject.get("message").toString());
                                break;
                        }

                    }

                    if (!jsonObject.has("responseCode")) {
                        functions.ShowToast(getString(R.string.something_wrong));
                    }

                } catch (JSONException e) {
                    Log.i("E:", e.toString());
                }


            }

            @Override
            public void notifyError(VolleyError error) {
                functions.ShowToast(getString(R.string.network_something_wrong));
            }
        });

        functions.showProgress(lottieview);
        networkController.PostMethod(data.forgotPassword_Api, postData);

        FrameLayout signin_form = findViewById(R.id.signin_form);
        FrameLayout forgot_form = findViewById(R.id.email_form);
        signin_form.setVisibility(View.VISIBLE);
        forgot_form.setVisibility(View.INVISIBLE);
    }

    public void verifyRegistrationEmailRequests(String email) {
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
                            functions.ShowToast(jsonObject.get("message").toString());
                            oneTimetoken = jsonObject.get("token").toString();
                            //HIDE SIGNUP and SignIn
                            signup_form.setVisibility(View.INVISIBLE);
                            signin_f.setVisibility(View.INVISIBLE);
                            verify_form.setVisibility(View.VISIBLE);
                            verify_form.bringToFront();

                        } else if (responseCode.equals("0")) {
                            functions.ShowToast(jsonObject.get("message").toString());

                        }
                    }
                    if (!jsonObject.has("responseCode")) {
                        functions.ShowToast(getString(R.string.something_wrong));

                    }
                } catch (JSONException e) {

                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottieview);
                functions.ShowToast(getString(R.string.network_something_wrong));
            }
        });
        functions.showProgress(lottieview);
        networkController.PostMethod(data.verifyEmail_Api, postData);
    }


    public void performRegistrationRequests() {
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
                            functions.ShowToast(jsonObject.get("message").toString());
                            signup_form.setVisibility(View.INVISIBLE);
                            verify_form.setVisibility(View.INVISIBLE);
                            txtOneTimeCode.setText("");
                            Signin_form.setVisibility(View.VISIBLE);
                        } else if (responseCode.equals("0")) {
                            functions.ShowToast(jsonObject.get("message").toString());

                        }
                    }
                    if (!jsonObject.has("responseCode")) {
                        functions.ShowToast(getString(R.string.something_wrong));

                    }
                } catch (JSONException e) {

                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottieview);
                functions.ShowToast(getString(R.string.network_something_wrong));
            }
        });
        functions.showProgress(lottieview);
        networkController.PostMethod(data.registration_Api, postData);
    }


    public void signInRequests() {
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
        setFrameLayoutVisibility(signup_form);
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
        btnSignup_si.setVisibility(View.INVISIBLE);
        login_btnShowForgotForm.setVisibility(View.INVISIBLE);
        signin_form.setVisibility(View.INVISIBLE);
        signin_f.setVisibility(View.INVISIBLE);
        signup_f.setVisibility(View.INVISIBLE);
        forgot_form.setVisibility(View.INVISIBLE);


        BtnVerify_email.setVisibility(View.VISIBLE);
        txtVerify.setText("");
    }

    public void toggleForgot() {
        setFrameLayoutVisibility(forgot_form);
    }
    

    public void toggleSignin() {
        setFrameLayoutVisibility(Signin_form);
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




}
