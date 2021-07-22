/**
 * CHANGE LOG - First Update
 * -------I ADDED A CUSTOM MESSAGE VARIABLE TO REDUCE BOILERPLATE INSIDE THE sign_in.OnClickListener
 * ------ADDED A METHOD TO CHECK BIOMETRIC
 * -----ADDED A METHOD THAT SAVES Username and password to sharedPref, so user can autoLogin
 * ----CREATED NEW DATA CLass to Hold all URL
 * ---CREATED A METHOD THAT ACCEPTS ALL PARAMETERS AND RETURNS REGISTRATION LINK
 * --FIXed a Bug in forgot OnClick that causes Crash..
 */

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
    private EditText user;
    private EditText pass;
    private EditText un;
    private EditText pw;
    private EditText cpw;
    private EditText email;
    private TextView verify;
    private Button verify_email;
    public EditText forgot_email;
    public String ErrorMessage = "";
    public SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //INITIALIZE THE SHAREDPREF FILE and Check
        sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

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
        Button forgot;

        verify = findViewById(R.id.verify_email_check);

        queue = Volley.newRequestQueue(this);

        user = findViewById(R.id.user);
        pass = findViewById(R.id.pass);
        Button sign_in = findViewById(R.id.sign_in);


        sign_in.setOnClickListener(v -> {


            if (TextUtils.isEmpty(user.getText())) {
               ErrorMessage = getString(R.string.username_required);
                user.setError(ErrorMessage);
                Toast.makeText(getApplicationContext(), ErrorMessage, Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(pass.getText())) {
                ErrorMessage = getString(R.string.password_required);
                pass.setError(ErrorMessage);
                Toast.makeText(getApplicationContext(), ErrorMessage, Toast.LENGTH_SHORT).show();
            } else {
                signIn();
            }
        });

        un = findViewById(R.id.Username);
        pw = findViewById(R.id.Password);
        cpw = findViewById(R.id.cPassword);
        Button sign_up = findViewById(R.id.sign_up);
        sign_up.setOnClickListener(v -> {

            if (TextUtils.isEmpty(un.getText())) {
                ErrorMessage = getString(R.string.username_required);
                un.setError(ErrorMessage);
                Toast.makeText(getApplicationContext(), ErrorMessage, Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(pw.getText())) {
                ErrorMessage = getString(R.string.password_required);
                pw.setError(ErrorMessage);
                Toast.makeText(getApplicationContext(), ErrorMessage, Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(cpw.getText())) {
                ErrorMessage = getString(R.string.confirmation_password_required);
                cpw.setError(ErrorMessage);
                Toast.makeText(getApplicationContext(), ErrorMessage, Toast.LENGTH_SHORT).show();
            } else {
                String password = pw.getText().toString();
                String cpassword = cpw.getText().toString();
                if (password.equals(cpassword)) {
                    signUp();
                } else {
                    ErrorMessage = getString(R.string.password_unmatch);
                    pw.setError(ErrorMessage);
                    cpw.setError(ErrorMessage);
                    Toast.makeText(getApplicationContext(), ErrorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
        email = findViewById(R.id.Email);
        verify_email = findViewById(R.id.verify_email);
        verify_email.setOnClickListener(v -> {
            if (TextUtils.isEmpty(email.getText())) {
                ErrorMessage = getString(R.string.email_required);
                email.setError(ErrorMessage);
                Toast.makeText(getApplicationContext(), ErrorMessage, Toast.LENGTH_SHORT).show();
            } else {
                verify_email.setVisibility(View.INVISIBLE);
                securityStep();
            }
        });
        Button cancel_email = findViewById(R.id.cancel_email);
        cancel_email.setOnClickListener(v -> cancelEmail());

        forgot = findViewById(R.id.forgot);
        forgot.setOnClickListener(v -> forgotPassword());


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
                Toast.makeText(getApplicationContext(), "Downloading latest version..", Toast.LENGTH_SHORT).show();
                String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
                String fileName = "WeSocial.apk";
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
        if(forgot_email.getText().toString() == ""){
            Toast.makeText(getApplicationContext(),getString(R.string.email_required),Toast.LENGTH_SHORT);
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
        String username = un.getText().toString();
        String password = pw.getText().toString();
        regUsername = username;
        regPassword = password;
        String mail = email.getText().toString();
        String url = data.register_url(mail,regUsername,regPassword);
        StringRequest register = new StringRequest(Request.Method.GET, url, response -> verifyAccount(), error -> Toast.makeText(getApplicationContext(), "Registration failed! Please try again", Toast.LENGTH_SHORT).show());
        queue.add(register);
    }

    public void verifyAccount() {
        String mail = email.getText().toString();
        String url = data.verifyAccountUrl(mail);
        @SuppressLint("SetTextI18n") StringRequest check = new StringRequest(Request.Method.GET, url, response -> {
            if (response.equals("true")) {
                //Save username and Password to sharedPref after registration.
                saveUsernameAndPassword(regUsername, regPassword);

                verify.setText("Verified!");
                Intent intent = new Intent(this, FrontPage.class);
                startActivity(intent);
            } else {
                verify.setText("Awaiting verification");
                Handler timeout = new Handler();
                timeout.postDelayed(this::verifyAccount, 3000);
            }
        }, error -> {
            cancelEmail();
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        });
        queue.add(check);
    }

    public void signIn() {
        String username = user.getText().toString();
        String password = pass.getText().toString();

        String url = "https://wesocial.space/mob_api?login=" + username + "&key=" + password;

        @SuppressLint("SetTextI18n") StringRequest check = new StringRequest(Request.Method.GET, url, response -> {
            if (response.equals("Wrong username") || response.equals("Wrong password")) {
                Toast.makeText(getApplicationContext(), "Wrong credentials", Toast.LENGTH_SHORT).show();
            } else {
                //Save Username and password
                saveUsernameAndPassword(username, password);

                Toast.makeText(getApplicationContext(), "Welcome back!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, FrontPage.class);
                startActivity(intent);
            }
        }, error -> Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show());
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
        verify_email.setVisibility(View.VISIBLE);
        verify.setText(null);
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
            Intent intent = new Intent(this, FrontPage.class);
            intent.putExtra("username", username);
            intent.putExtra("password", password);
            startActivity(intent);
    }

    public void saveUsernameAndPassword(String username, String password) {
        /**
         * Save username and password to sharedPREF
         */
        SharedPreferences.Editor sharedPrefEditor = sharedpreferences.edit();
        sharedPrefEditor.putString("username", username);
        sharedPrefEditor.putString("password", password);
        sharedPrefEditor.commit();

    }

    public void biometricPrompt() {
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint required")
                .setDescription("Touch the fingerprint sensor")
                .setNegativeButtonText("Exit")
                //.setConfirmationRequired(true)
                .build();
        biometricPrompt.authenticate(promptInfo);
    }

}
