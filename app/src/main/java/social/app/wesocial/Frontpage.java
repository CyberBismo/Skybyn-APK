package social.app.wesocial;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static social.app.wesocial.R.drawable;
import static social.app.wesocial.R.id;
import static social.app.wesocial.R.layout;
import static social.app.wesocial.R.string;


public class Frontpage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences sharedpreferences;
    DrawerLayout drawerLayout;
    Data data = new Data();
    Functions functions = new Functions();
    String loginAction;
    String userID;
    DownloadManager downloadManager;
    LottieAnimationView lottieview;
    long downLoadId;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_front_page);
        sharedpreferences = getSharedPreferences(getString(string.app_name), Context.MODE_PRIVATE);


        configureToolbarAndDrawer();
        checkAppUpdate();

        Intent intent = getIntent();
        loginAction = intent.getStringExtra("loginAction");
        lottieview = findViewById(id.frontpageProgressView);

        NavigationView navView = findViewById(id.sideNavView);
        navView.setNavigationItemSelectedListener(this);


        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


        if (loginAction.equals(data.fingerprint_auth)) {
            //perform login
            performLoginAuth();
        } else {
            userID = sharedpreferences.getString("userID", "");
            loadUserProfile(userID);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                installApk();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.permission_required), Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;

            default:
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void configureToolbarAndDrawer() {
        Toolbar toolbar = findViewById(id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(string.app_name);
        drawerLayout = findViewById(id.drawerlayout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, string.nav_open, string.nav_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(drawable.hamburger);

    }

    private void logOut() {
        //DELETE ALL keys
        SharedPreferences.Editor sharedPrefEditor = sharedpreferences.edit();
        sharedPrefEditor.clear();
        sharedPrefEditor.apply();
        Intent intent = new Intent(this, LoginRegisterForgot.class);
        startActivity(intent);
    }

    private void checkAppUpdate() {
        PackageManager pm = getApplicationContext().getPackageManager();
        String pkgName = getApplicationContext().getPackageName();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert pkgInfo != null;
        //Using version code, Instead of Version name
        Integer currentVersion = pkgInfo.versionCode;

        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) {
                try {
                    Integer serverVersion = Integer.valueOf(response);
                    if (serverVersion > currentVersion) {
                        //DOWNLOAD AND INSTALL
                        downloadApk();

                    }
                } catch (Exception e) {
                    Log.i("e:", e.getMessage());
                }
            }

            @Override
            public void notifyError(VolleyError error) {
             //  Toast.makeText(getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });
        networkController.GetMethod(data.version_url);

    }


       private void downloadApk() {
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(data.apk_url));
        request.setTitle(getString(string.app_name))
                .setDescription("Update is downloading...")
                .setDestinationInExternalFilesDir(this,
                        Environment.DIRECTORY_DOWNLOADS, data.apk_name)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //Enqueue the download.The download will start automatically once the download manager is ready
        // to execute it and connectivity is available.
        downLoadId = downloadManager.enqueue(request);
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downLoadId == id) {
                installApk();

            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }


    private void installApk() {
        Toast.makeText(getApplicationContext(),getString(R.string.update_Downloaded),Toast.LENGTH_LONG).show();
        try {
            Context mContext = null;
            File file = new File(Environment.DIRECTORY_DOWNLOADS + "/" + data.apk_name);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= 24) {
                Uri downloaded_apk = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                intent.setDataAndType(downloaded_apk, "application/vnd.android.package-archive");
                @SuppressLint("QueryPermissionsNeeded") List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    mContext.grantUriPermission(mContext.getApplicationContext().getPackageName() + ".provider", downloaded_apk, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                intent.setAction(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }

    public void loadUserProfile(String userID){
        functions.showProgress(lottieview);
        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", userID);
        postData.put("profile", "");

        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) {
                functions.hideProgress(lottieview);
                if (functions.isJsonObject(response.toString())) {
                    Log.i("Profile json response", response.toString());
                }
                if (!functions.isJsonObject(response.toString())) {
                    Log.i("Profile Json error", response.toString());
                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottieview);
                Toast.makeText(getApplicationContext(), getString(string.network_something_wrong), Toast.LENGTH_SHORT).show();
                logOut();
                Toast.makeText(getApplicationContext(), getString(R.string.please_login_again), Toast.LENGTH_SHORT).show();
            }
        });

        networkController.PostMethod(data.profile_Api, postData);
    }

    public void performLoginAuth() {
        functions.showProgress(lottieview);
        String username;
        String password;
        username = sharedpreferences.getString("username", "");
        password = sharedpreferences.getString("password", "");

        HashMap<String, String> postData = new HashMap<>();
        postData.put("username", username);
        postData.put("password", password);
        postData.put("login", "");

        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) {

                functions.hideProgress(lottieview);
                if (!functions.isJsonObject(response)) {
                    Toast.makeText(getApplicationContext(), getString(string.something_wrong), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (functions.isJsonObject(response)) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String response_code = jsonResponse.get("responseCode").toString();

                        if (response_code.equals("1")) {
                            userID = jsonResponse.getString("userID");
                            //LOAD PROFILE
                            Toast.makeText(getApplicationContext(), getString(string.loginSuccessful), Toast.LENGTH_LONG).show();
                            loadUserProfile(userID);
                        }

                        if (response_code.equals("0")) {
                            String errorMsg = jsonResponse.get("message").toString();
                            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                            logOut();
                        }

                    } catch (JSONException e) {
                    }


                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottieview);
                Toast.makeText(getApplicationContext(), getString(string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });
        networkController.PostMethod(data.login_Api, postData);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                logOut();
                break;
            case R.id.closeapp:
                finishAffinity();
                break;


        }
        return false;
    }
}
