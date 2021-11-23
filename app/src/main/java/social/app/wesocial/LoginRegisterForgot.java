package social.app.wesocial;


//import android.content.Context;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.firebase.FirebaseApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executor;

import timber.log.Timber;


public class LoginRegisterForgot extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    public EditText txtforgot_email;
    public String ErrorMessage = "";
    public SharedPreferences sharedpreferences;
    public LottieAnimationView lottieview;
    Functions functions = null;
    Data data = new Data();
    String oneTimetoken;
    Executor executor;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    EditText txtOneTimeCode;
    FrameLayout signup_form;
    FrameLayout forgot_form;
    FrameLayout signin_form;
    FrameLayout email_form;
    FrameLayout verify_form;
    CodeScanner mCodeScanner;
    Button login_btnShowForgotForm, login_btnShowSignupForm, signup_btnShowForgotForm, signup_btnShowLoginForm, forgot_btnShowLoginForm, forgot_btnShowSignupForm;
    TextView lblCloseVerify;
    Button btnSign_up;
    private EditText txtLoginUserName;
    private EditText txtLoginPassWord;
    private EditText txtRegisterUsername;
    private EditText txtRegisterPassword;
    private EditText txtConfirmPassword;
    private EditText txtEmail;
    private TextView txtVerify, lblQrSignTitle;
    private Button BtnVerify_email;
    Button btnScanQrCode, btnExitScanner;

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean hasPermission() {
        // Permission is not granted
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions!",
                                    (dialog, which) -> {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermission();
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private void viewReferences() {
        //VIEW REFERENCES
        lottieview = findViewById(R.id.loginProgressView);
        login_btnShowForgotForm = findViewById(R.id.loginShowForgotForm);
        login_btnShowSignupForm = findViewById(R.id.loginShowSignupForm);
        signup_btnShowForgotForm = findViewById(R.id.signupShowForgotForm);
        signup_btnShowLoginForm = findViewById(R.id.signupShowLoginForm);
        forgot_btnShowLoginForm = findViewById(R.id.forgotShowLoginForm);
        forgot_btnShowSignupForm = findViewById(R.id.forgotShowSignupForm);
        txtVerify = findViewById(R.id.verify_email_check);
        txtLoginUserName = findViewById(R.id.txtUsername);
        txtOneTimeCode = findViewById(R.id.txtOTC);
        txtLoginPassWord = findViewById(R.id.txtPassword);
        txtRegisterUsername = findViewById(R.id.Username);
        txtRegisterPassword = findViewById(R.id.Password);
        txtConfirmPassword = findViewById(R.id.cPassword);
        txtEmail = findViewById(R.id.Email);
        txtforgot_email = findViewById(R.id.forgot_email);
        lblCloseVerify = findViewById(R.id.lblCloseVerify);
        btnSign_up = findViewById(R.id.btnProcessSignUp);
        signin_form = findViewById(R.id.signin_form);
        forgot_form = findViewById(R.id.forgot_form);
        signup_form = findViewById(R.id.signup_form);
        verify_form = findViewById(R.id.verify_form);
        btnScanQrCode = findViewById(R.id.btnLoginWithQR);
        lblQrSignTitle = findViewById(R.id.lblSignInQRTitle);
        btnExitScanner = findViewById(R.id.btnExitScanner);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        //INITIALIZE THE SHAREDPREF FILE
        sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);

        viewReferences();
        //CHECK IF DARK MODE IS SET
        if (sharedpreferences.contains(getString(R.string.toggleDarkMode_key))) {
            Boolean darkMode = sharedpreferences.getBoolean(getString(R.string.toggleDarkMode_key), false);
            if (darkMode) {
                setTheme(R.style.Theme_AppCompat);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

        //Instatiate the Functions class
        functions = new Functions(getApplicationContext());


        //LOGGING
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        //Check for Camera Permission!
        if (hasPermission()) {
        } else {
            requestPermission();
        }

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        ConstraintLayout scannerFrameLayout = findViewById(R.id.scannerFrameLayout);

        mCodeScanner = new CodeScanner(this, scannerView);
        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> Toast.makeText(LoginRegisterForgot.this, result.getText(), Toast.LENGTH_SHORT).show()));
        scannerView.setAutoFocusButtonVisible(false);
        mCodeScanner.setFlashEnabled(false);


        btnExitScanner.setOnClickListener(view -> {
            mCodeScanner.releaseResources();
            scannerFrameLayout.setVisibility(View.INVISIBLE);
        });

        btnScanQrCode.setOnClickListener(view -> {
            scannerFrameLayout.setVisibility(View.VISIBLE);
            mCodeScanner.startPreview();

            lblQrSignTitle.setText("Visit " + data.domain + " and scan the QR Code.");
            //functions.Linkify(lblQrSignTitle);
        });

        login_btnShowForgotForm.setOnClickListener(v -> {
            toggleForgot();
        });
        login_btnShowSignupForm.setOnClickListener(v -> {
            toggleSignup();
        });

        signup_btnShowForgotForm.setOnClickListener(v -> {
            toggleForgot();
        });
        signup_btnShowLoginForm.setOnClickListener(v -> {
            toggleSignin();
        });

        forgot_btnShowSignupForm.setOnClickListener(v -> {
            toggleSignup();
        });
        forgot_btnShowLoginForm.setOnClickListener(v -> {
            toggleSignin();
        });


        Button BtnForgotPassword;
        Button btnProcessSignIn = findViewById(R.id.BtnProcessLogin);
        Button btnVerifyOTC = findViewById(R.id.btnVerifyOTC);

        lblCloseVerify.setOnClickListener(view -> {
            toggleSignup();
        });


        //Init Executor and BioPrompt
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                //BIOMETRCIC
                functions.hideProgress(lottieview);
                sendLoginIntentDataToFrontPage();
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
            functions.hideSoftKeyboard(Objects.requireNonNull(LoginRegisterForgot.this));
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
            if (TextUtils.isEmpty(txtRegisterUsername.getText())) {
                ErrorMessage = getString(R.string.username_required);
                txtRegisterUsername.setError(ErrorMessage);
                functions.ShowToast(ErrorMessage);
            } else if (TextUtils.isEmpty(txtRegisterPassword.getText())) {
                ErrorMessage = getString(R.string.password_required);
                txtRegisterPassword.setError(ErrorMessage);
                functions.ShowToast(ErrorMessage);
            } else if (TextUtils.isEmpty(txtConfirmPassword.getText())) {
                ErrorMessage = getString(R.string.confirmation_password_required);
                functions.ShowToast(ErrorMessage);

            } else {
                String password = txtRegisterPassword.getText().toString();
                String cpassword = txtConfirmPassword.getText().toString();

                if (password.equals(cpassword)) {
                    // signUp();
                    verifyRegistrationEmailRequests(txtEmail.getText().toString());
                } else {
                    ErrorMessage = getString(R.string.password_unmatch);
                    txtRegisterPassword.setError(ErrorMessage);
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
                sendLoginIntentDataToFrontPage();
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
        networkController.PostMethod(data.forgotPassword_API, postData);

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
                            toggleVerify();

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
        networkController.PostMethod(data.verifyEmail_API, postData);
    }


    public void performRegistrationRequests() {
        String username = txtRegisterUsername.getText().toString();
        String password = txtRegisterPassword.getText().toString();
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
                            signin_form.setVisibility(View.VISIBLE);
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
        networkController.PostMethod(data.registration_API, postData);
    }


    public void signInRequests() {
        functions.showProgress(lottieview);
        String username = txtLoginUserName.getText().toString();
        String password = txtLoginPassWord.getText().toString();

        HashMap<String, String> postData = new HashMap<>();

        postData.put("username", username);
        postData.put("password", password);

        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) {
                if (!functions.isJsonObject(response)) {
                    functions.hideProgress(lottieview);
                    Toast.makeText(getApplicationContext(), getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    Timber.i(response);
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
        networkController.PostMethod(data.login_API, postData);
    }


    // Switch forms and buttons

    public void toggleSignin() {
        signup_form.setVisibility(View.INVISIBLE);
        forgot_form.setVisibility(View.INVISIBLE);
        verify_form.setVisibility(View.INVISIBLE);
        signin_form.setVisibility(View.VISIBLE);
    }

    public void toggleSignup() {
        signin_form.setVisibility(View.INVISIBLE);
        forgot_form.setVisibility(View.INVISIBLE);
        verify_form.setVisibility(View.INVISIBLE);

        signup_form.setVisibility(View.VISIBLE);
    }

    public void toggleForgot() {
        signin_form.setVisibility(View.INVISIBLE);
        signup_form.setVisibility(View.INVISIBLE);
        verify_form.setVisibility(View.INVISIBLE);

        forgot_form.setVisibility(View.VISIBLE);
    }

    public void toggleVerify() {
        signin_form.setVisibility(View.INVISIBLE);
        signup_form.setVisibility(View.INVISIBLE);
        forgot_form.setVisibility(View.INVISIBLE);

        verify_form.setVisibility(View.VISIBLE);
    }

    public void toggleEmail() {
        signin_form.setVisibility(View.INVISIBLE);
        signup_form.setVisibility(View.INVISIBLE);
        forgot_form.setVisibility(View.INVISIBLE);
        verify_form.setVisibility(View.INVISIBLE);

        email_form.setVisibility(View.VISIBLE);
    }

    public void cancelEmail() {
        toggleSignin();

        BtnVerify_email.setVisibility(View.VISIBLE);
        txtVerify.setText("");
    }

    public void sendLoginIntentDataToFrontPage() {
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
