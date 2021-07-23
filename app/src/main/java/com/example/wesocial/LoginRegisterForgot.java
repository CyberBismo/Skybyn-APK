package com.example.wesocial;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.util.concurrent.Executor;


public class LoginRegisterForgot extends AppCompatActivity {
    Data data  = new Data();
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
                Toast.makeText(LoginRegisterForgot.this,errorMsg, Toast.LENGTH_LONG).show();
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

        findViewById(R.id.forgot);
        Button BtnForgotPassword;

        txtVerify = findViewById(R.id.verify_email_check);

        queue = Volley.newRequestQueue(this);

        txtUser = findViewById(R.id.user);
        txtPass = findViewById(R.id.pass);
        Button btnSignIn = findViewById(R.id.sign_in);


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


        String version = data.version_url;
        String url = data.apk_url;
        PackageManager pm = getApplicationContext().getPackageManager();
        String pkgName = getApplicationContext().getPackageName();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert pkgInfo != null;
        String current = pkgInfo.versionName;
        StringRequest checkUpdate = new StringRequest(Request.Method.GET, version, response -> {
            if (!response.equals(current)) {
                Toast.makeText(getApplicationContext(), getString(R.string.downloading_latest), Toast.LENGTH_SHORT).show();
                String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
                String fileName = data.getApkName().toString();
                destination += fileName;
                final Uri uri = Uri.parse("file://" + destination);
                File file = new File(fileName);
                if (file.exists()) {
                    deleteFile(destination);
                }
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setDestinationUri(uri);
                final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                final long downloadId = manager.enqueue(request);
                BroadcastReceiver onComplete = new BroadcastReceiver() {
                    public void onReceive(Context ctxt, Intent intent) {
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        install.setDataAndType(uri,
                                manager.getMimeTypeForDownloadedFile(downloadId));
                        startActivity(install);

                        unregisterReceiver(this);
                        finish();
                    }
                };
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        }, error -> {
        });
        queue.add(checkUpdate);
    }

    public void signUp() {
        FrameLayout signup_form = findViewById(R.id.signup_form);
        FrameLayout email_form = findViewById(R.id.email_form);
        signup_form.setVisibility(View.INVISIBLE);
        email_form.setVisibility(View.VISIBLE);
    }

    public void forgotPassword() {
        if(forgot_email.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),getString(R.string.email_required),Toast.LENGTH_SHORT).show();
            return;
        }
        String mail = forgot_email.getText().toString();
        String url = data.forgotpassword_url + mail;
        StringRequest forgot = new StringRequest(Request.Method.GET, url, response -> Toast.makeText(getApplicationContext(), "If the email you provided match with anyone registered, we have now sent a new password to your .", Toast.LENGTH_SHORT).show(), error -> {
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
        String mail = txtEmail.getText().toString();
        String url = data.register_url(mail,regUsername,regPassword);
        StringRequest register = new StringRequest(Request.Method.GET, url, response -> verifyAccount(), error -> Toast.makeText(getApplicationContext(), "Registration failed! Please try again", Toast.LENGTH_SHORT).show());
        queue.add(register);
    }

    public void verifyAccount() {
        String mail = txtEmail.getText().toString();
        String url = data.verifyAccountUrl(mail);
        @SuppressLint("SetTextI18n") StringRequest check = new StringRequest(Request.Method.GET, url, response -> {
            if (response.equals("true")) {
                //Save username and Password to sharedPref after registration.
                saveUsernameAndPassword(regUsername, regPassword);

                txtVerify.setText(getString(R.string.verified));
                Intent intent = new Intent(this, Frontpage.class);
                startActivity(intent);
            } else {
                txtVerify.setText(getString(R.string.awaiting_verification));
                Handler timeout = new Handler();
                timeout.postDelayed(this::verifyAccount, 3000);
            }
        }, error -> {
            cancelEmail();
            Toast.makeText(getApplicationContext(), getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
        });
        queue.add(check);
    }

    public void signIn() {
        String username = txtUser.getText().toString();
        String password = txtPass.getText().toString();

        String url = "https://wesocial.space/mob_api?login=" + username + "&key=" + password;

        @SuppressLint("SetTextI18n") StringRequest check = new StringRequest(Request.Method.GET, url, response -> {
            if (response.equals("Wrong username") || response.equals("Wrong password")) {
                Toast.makeText(getApplicationContext(), getString(R.string.wrong_credentials), Toast.LENGTH_SHORT).show();
            } else {
                //Save Username and password
                saveUsernameAndPassword(username, password);

                Toast.makeText(getApplicationContext(), getString(R.string.welcome_back), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, Frontpage.class);
                startActivity(intent);
            }
        }, error -> Toast.makeText(getApplicationContext(), getString(R.string.something_wrong), Toast.LENGTH_SHORT).show());
        queue.add(check);
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
                .setTitle(getString(R.string.fingerprint_required))
                .setDescription(getString(R.string.touch_sensor))
                .setNegativeButtonText("Exit")
                //.setConfirmationRequired(true)
                .build();
        biometricPrompt.authenticate(promptInfo);
    }

}
