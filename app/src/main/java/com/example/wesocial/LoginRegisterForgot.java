package com.example.wesocial;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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

import com.aghamiri.fastdl.DownloadManager;
import com.aghamiri.fastdl.OnDownloadProgressListener;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Timer;
import java.util.concurrent.Executor;


public class LoginRegisterForgot extends AppCompatActivity {
    Data data = new Data();
    private DownloadManager dlManager;
    Executor executor;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    private String regUsername;
    private String regPassword;
    private RequestQueue queue;
    private Button forgot_si;
    private Button signup_si;
    private Button forgot_su;
    private Button signin_su;
    private Button signin_f;
    private Button signup_f;
    private EditText txtUser;
    private EditText txtPass;
    private EditText txtUsername;
    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private EditText txtEmail;
    private TextView txtVerify;
    private Button BtnVerify_email;
    public EditText forgot_email;
    public String ErrorMessage = "";
    public SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // PRDownloader.initialize(getApplicationContext());
        //INITIALIZE THE SHAREDPREF FILE
        sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        //Init Executor and BioPrompt
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                //BIOMETRCIC
                checkLoginStatus();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                errString = getString(R.string.biometric_auth_error);
                Toast.makeText(LoginRegisterForgot.this, errString, Toast.LENGTH_LONG).show();
                //LoginRegisterForgot.this.finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                String errorMsg = getString(R.string.biometric_auth_failed);
                Toast.makeText(LoginRegisterForgot.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });

        //Checking if SharedPref contains a Username key
        if (sharedpreferences.contains("username")) {
            biometricPrompt();
        }

        signin_su = findViewById(R.id.signin_su);
        signin_f = findViewById(R.id.signin_f);
        signup_si = findViewById(R.id.signup_si);
        signup_f = findViewById(R.id.signup_f);
        forgot_si = findViewById(R.id.forgot_si);
        forgot_su = findViewById(R.id.forgot_su);

        signin_su.setOnClickListener(v -> toggleSignin());
        signup_si.setOnClickListener(v -> toggleSignup());
        signin_f.setOnClickListener(v -> toggleSignin());
        signup_f.setOnClickListener(v -> toggleSignup());
        forgot_si.setOnClickListener(v -> toggleForgot());
        forgot_su.setOnClickListener(v -> toggleForgot());

        Button BtnForgotPassword;
        findViewById(R.id.forgot);

        txtVerify = findViewById(R.id.verify_email_check);
        queue = Volley.newRequestQueue(this);

        txtUser = findViewById(R.id.txtUsername);
        txtPass = findViewById(R.id.txtPassword);

        Button btnSignIn = findViewById(R.id.BtnSignIn);

        btnSignIn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(txtUser.getText())) {
                ErrorMessage = getString(R.string.username_required);
                txtUser.setError(ErrorMessage);
                Toast.makeText(getApplicationContext(), ErrorMessage, Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(txtPass.getText())) {
                ErrorMessage = getString(R.string.password_required);
                txtPass.setError(ErrorMessage);
                Toast.makeText(getApplicationContext(), ErrorMessage, Toast.LENGTH_SHORT).show();
            } else {
                signIn();
            }
        });

        txtUsername = findViewById(R.id.Username);
        txtPassword = findViewById(R.id.Password);
        txtConfirmPassword = findViewById(R.id.cPassword);

        Button btnSign_up = findViewById(R.id.sign_up);
        btnSign_up.setOnClickListener(v -> {
            if (TextUtils.isEmpty(txtUsername.getText())) {
                ErrorMessage = getString(R.string.username_required);
                txtUsername.setError(ErrorMessage);
                Toast.makeText(getApplicationContext(), ErrorMessage, Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(txtPassword.getText())) {
                ErrorMessage = getString(R.string.password_required);
                txtPassword.setError(ErrorMessage);
                Toast.makeText(getApplicationContext(), ErrorMessage, Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(txtConfirmPassword.getText())) {
                ErrorMessage = getString(R.string.confirmation_password_required);
                txtConfirmPassword.setError(ErrorMessage);
                Toast.makeText(getApplicationContext(), ErrorMessage, Toast.LENGTH_SHORT).show();
            } else {
                String password = txtPassword.getText().toString();
                String cpassword = txtConfirmPassword.getText().toString();

                if (password.equals(cpassword)) {
                    signUp();
                } else {
                    ErrorMessage = getString(R.string.password_unmatch);
                    txtPassword.setError(ErrorMessage);
                    txtConfirmPassword.setError(ErrorMessage);
                    Toast.makeText(getApplicationContext(), ErrorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtEmail = findViewById(R.id.Email);
        BtnVerify_email = findViewById(R.id.verify_email);
        BtnVerify_email.setOnClickListener(v -> {
            if (TextUtils.isEmpty(txtEmail.getText())) {
                ErrorMessage = getString(R.string.email_required);
                txtEmail.setError(ErrorMessage);
                Toast.makeText(getApplicationContext(), ErrorMessage, Toast.LENGTH_SHORT).show();
            } else {
                BtnVerify_email.setVisibility(View.INVISIBLE);
                securityStep();
            }
        });
        Button BtnCancel_email = findViewById(R.id.cancel_email);
        BtnCancel_email.setOnClickListener(v -> cancelEmail());

        BtnForgotPassword = findViewById(R.id.forgot);
        BtnForgotPassword.setOnClickListener(v -> forgotPassword());


    }

    public void signUp() {
        FrameLayout signup_form = findViewById(R.id.signup_form);
        FrameLayout email_form = findViewById(R.id.email_form);
        signup_form.setVisibility(View.INVISIBLE);
        email_form.setVisibility(View.VISIBLE);
    }

    public void forgotPassword() {
        if (forgot_email.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.email_required), Toast.LENGTH_SHORT).show();
            return;
        }

        String mail = forgot_email.getText().toString();
        String url = data.forgotpassword_url + mail;
        StringRequest forgot = new StringRequest(Request.Method.GET, url, response -> Toast.makeText(getApplicationContext(), getString(R.string.sent_password_msg), Toast.LENGTH_SHORT).show(), error -> {
        });

        queue.add(forgot);
        FrameLayout signin_form = findViewById(R.id.signin_form);
        FrameLayout forgot_form = findViewById(R.id.email_form);
        signin_form.setVisibility(View.VISIBLE);
        forgot_form.setVisibility(View.INVISIBLE);
    }

    public void securityStep() {
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        regUsername = username;
        regPassword = password;
        String email = txtEmail.getText().toString();

        HashMap<String, String> postData = new HashMap<>();
        postData.put("username", regUsername);
        postData.put("password", password);
        postData.put("email", email);

        //SIMPLIFIED THE NETWORK CALL - POST PARAMETER
        NetworkController networkController = new NetworkController(this, new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) {
                verifyAccount();
            }

            @Override
            public void notifyError(VolleyError error) {
                ShowToast(getString(R.string.network_something_wrong));
            }
        });

        networkController.PostMethod(data.register_url(),postData);


    }

    public Runnable verifyAccount() {
        String email = txtEmail.getText().toString();

        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) {
                if (response.equals("true")) {
                    //Save username and Password to sharedPref after registration.
                    saveUsernameAndPassword(regUsername, regPassword);
                    txtVerify.setText(getString(R.string.verified));
                    Intent intent = new Intent(getApplicationContext(), Frontpage.class);
                    startActivity(intent);
                } else {
                    Timer timer = new Timer();
                    txtVerify.setText(getString(R.string.awaiting_verification));
                    Handler timeout = new Handler();
                    timeout.postDelayed(verifyAccount(), 3000);

                }
            }

            @Override
            public void notifyError(VolleyError error) {
                cancelEmail();
                Toast.makeText(getApplicationContext(), getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
            }
        });
                //Make sure post variable in PHP Script is email
                HashMap<String,String> postData = new HashMap<>();
                postData.put("email",email);
                networkController.PostMethod(data.verifyAccountUrl(),postData);

        return null;
    }

    public void signIn() {

        String username = txtUser.getText().toString();
        String password = txtPass.getText().toString();

        HashMap<String,String> postData = new HashMap<>();
        postData.put("username",username);
        postData.put("password",password);

        String url = "https://wesocial.space/mob_api?login=" + username + "&key=" + password;

        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) {
                if (response.equals("Wrong username") || response.equals("Wrong password")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.wrong_credentials), Toast.LENGTH_SHORT).show();
                } else {
                    //Save Username and password
                    saveUsernameAndPassword(username, password);
                    Toast.makeText(getApplicationContext(), getString(R.string.welcome_back), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Frontpage.class);
                    startActivity(intent);
                }

            }

            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });

        networkController.PostMethod(url,postData);


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

        Handler timeout = new Handler();
        timeout.removeCallbacks(this::verifyAccount);
        BtnVerify_email.setVisibility(View.VISIBLE);
        txtVerify.setText(null);
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
        FrameLayout signin_form = findViewById(R.id.signin_form);
        FrameLayout forgot_form = findViewById(R.id.forgot_form);
        FrameLayout signup_form = findViewById(R.id.signup_form);

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
        /**
         * User has been logged in before, then set Username and password to intent...
         *
         */
        String username;
        String password;
        username = sharedpreferences.getString("username", "");
        password = sharedpreferences.getString("password", "");
        Intent intent = new Intent(this, Frontpage.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        startActivity(intent);
        finish();
    }

    public void saveUsernameAndPassword(String username, String password) {
        /**
         * Save username and password to sharedPREF
         */
        SharedPreferences.Editor sharedPrefEditor = sharedpreferences.edit();
        sharedPrefEditor.putString("username", username);
        sharedPrefEditor.putString("password", password);
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
