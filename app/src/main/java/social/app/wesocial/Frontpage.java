package social.app.wesocial;

import static social.app.wesocial.R.drawable;
import static social.app.wesocial.R.id;
import static social.app.wesocial.R.layout;
import static social.app.wesocial.R.string;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;
import com.ftinc.scoop.Scoop;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import timber.log.Timber;


public class Frontpage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences sharedpreferences;
    DrawerLayout drawerLayout;
    public static  Boolean gottenToken = false;
    Data data = new Data();
    Functions functions = new Functions();
    String loginAction;
    public static String userID;
    DownloadManager downloadManager;
    LottieAnimationView lottie;
    long downLoadId;
    ImageView imgNavProfilePicture;
    View navHeaderView;
    NavigationView sideNavView;
    TextView txtNavViewUsername;
    TextView txtNavViewUserEmail;

    public static String loginUsername, loginPassword;
    public static String firstName, lastName, middleName, nickName, avatarLink, userTitle, userRank, banned, banned_reason, visible, deactivated, deactivated_reason;
    Boolean userLoggedIn = false;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton fab;

    public static Activity frontpageActivity;
    public static SearchView searchView;
    private String keyword;
    private Boolean onQuery;
    public static String current_chat_user ;
    public static Boolean isTimeline;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //INIT FIREBASE
        FirebaseApp.initializeApp(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        setContentView(layout.activity_front_page);
        sharedpreferences = getSharedPreferences(getString(string.app_name), Context.MODE_PRIVATE);

        configureToolbarAndDrawer();
        checkAppUpdate();

        Intent intent = getIntent();
        loginAction = intent.getStringExtra("loginAction");
        lottie = findViewById(id.frontpageProgressView);
        sideNavView = findViewById(id.sideNavView);
        navHeaderView = sideNavView.getHeaderView(0);
        bottomNavigationView = (BottomNavigationView) findViewById(id.bottomNavigationView);
        fab = (FloatingActionButton) findViewById(id.fab);
        sideNavView.setNavigationItemSelectedListener(this);
        imgNavProfilePicture = navHeaderView.findViewById(R.id.imgNavViewProfilePicture);
        txtNavViewUsername = navHeaderView.findViewById(R.id.txtNavViewUsername);
        txtNavViewUserEmail = navHeaderView.findViewById(id.txtNavViewEmail);


        //Fab On CLick
        fab.setOnClickListener(view -> {
            Fragment sharePostFragment = SharePost.newInstance();
            functions.LoadFragment(sharePostFragment, "", Frontpage.this, false,false);
        });


        if (loginAction.equals(data.fingerprint_auth)) {
            //perform login
            performLoginAuth();
        } else {
            userID = sharedpreferences.getString("userID", "");
            loadUserProfile(userID);
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case id.timeline:
                        Fragment timelineFragment = Timeline.newInstance();
                        functions.LoadFragment(timelineFragment, getString(string.Timeline), Frontpage.this, true,false);
                        return true;

                    case id.messages:
                        showMessagesPage();
                        return true;
                }
                return false;
            }
        });

        //GETTING REFERENCe To the activity
        frontpageActivity = Frontpage.this;

    }

    public void performSearch(String userID, String keyword, Boolean onQuery) {

        this.keyword = keyword;
        this.onQuery = onQuery;
        if (!onQuery) {
            functions.showProgress(lottie);
        }

        HashMap<String, String> postData = new HashMap<>();
        postData.put("keyword", keyword);
        postData.put("userID", userID);

        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                Log.i("response", response.toString());
                functions.hideProgress(lottie);

                if (!functions.isJsonArray(response)) {
                    functions.showSnackBarError(getString(R.string.no_search_result) + keyword, findViewById(android.R.id.content), getApplicationContext());
                    return;
                }

                if (functions.isJsonArray(response)) {
                    Log.i("response", response);
                    Fragment searchFragment = Search.newInstance(keyword, response);
                    functions.LoadFragment(searchFragment, getString(string.search), Frontpage.this, false,false);
                    functions.showSnackBar(getString(R.string.we_found_result) + keyword, findViewById(android.R.id.content), getApplicationContext());

                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                Toast.makeText(getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });
        networkController.PostMethod(data.search_Api, postData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Fragment searchFragment = Search.newInstance("", "");
                functions.LoadFragment(searchFragment, getString(string.search), Frontpage.this, false,false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.equals("")) {
                    performSearch(Frontpage.userID, query, false);
                }
                functions.hideSoftKeyboard(Frontpage.this);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (!query.equals("")) {
                    performSearch(Frontpage.userID, query, true);

                }

                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    public void showNotifications() {
        Fragment notificationFragment = Notification.newInstance();
        functions.LoadFragment(notificationFragment, "Notification", Frontpage.this, false,false);
    }


    public void showFriendsPage() {
        Fragment friendsFragment = Friends.newInstance(userID);
        functions.LoadFragment(friendsFragment, getString(string.Friends), Frontpage.this, false,false);
    }

    public void showProfilePage() {
        Fragment profileFragment = Profile.newInstance(userID, "");
        functions.LoadFragment(profileFragment, getString(string.profile), Frontpage.this, false,false);
    }

    public void showMessagesPage() {
        Fragment messagesFragment = Messages.newInstance();
        functions.LoadFragment(messagesFragment, getString(string.messages), Frontpage.this, false,false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case id.notifications:
                showNotifications();
                break;
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
        finish();
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

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    public void loadUserProfile(String userID) {
        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", userID);


        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                //Load profile picture thumb after profile loads.
                functions.hideProgress(lottie);

                if (functions.isJsonObject(response.toString())) {
                    Log.i("Json", response);
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String responseCode = jsonObject.get("responseCode").toString();

                    if (responseCode.equals("1")) {
                        //Start the PUSH SERVICE after successful Login
                        Intent intent = new Intent(Frontpage.this, PushNotificationService.class);
                        startService(intent);

                        userLoggedIn = true;

                        Fragment timelineFragment = Timeline.newInstance();
                        functions.LoadFragment(timelineFragment, "posts", Frontpage.this,true ,false);

                        String username = jsonObject.getString("username").toString();
                        String email = jsonObject.getString("email").toString();
                        avatarLink = jsonObject.getString("avatar").toString();
                        firstName = jsonObject.get("fname").toString();
                        lastName = jsonObject.get("lname").toString();
                        nickName = jsonObject.get("nickname").toString();
                        middleName = jsonObject.get("mname").toString();
                        userRank = jsonObject.get("rank").toString();
                        userTitle = jsonObject.get("title").toString();
                        deactivated = jsonObject.get("deactivated").toString();
                        deactivated_reason = jsonObject.get("deactivated_reason").toString();
                        banned = jsonObject.get("banned").toString();
                        banned_reason = jsonObject.get("banned_reason").toString();
                        functions.loadProfilePictureDrawableThumb(avatarLink, imgNavProfilePicture);
                        txtNavViewUsername.setText(username);
                        txtNavViewUserEmail.setText(email);
                        functions.loadProfilePictureDrawableThumb(avatarLink, imgNavProfilePicture);
                        if (banned.equals("1")) {
                            showAlertDialog("", banned_reason, false, true);
                        }

                        if (deactivated.equals("1")) {
                            showAlertDialog("", deactivated, false, true);
                        }


                    }

                    if (responseCode.equals("0")) {
                        Toast.makeText(getApplicationContext(), getString(R.string.invalid_profile), Toast.LENGTH_LONG).show();
                        logOut();
                    }

                }

                if (!functions.isJsonObject(response.toString())) {
                    Log.i("Profile Json error", response.toString());
                    Toast.makeText(getApplicationContext(), getString(R.string.please_login_again), Toast.LENGTH_SHORT).show();
                    logOut();

                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                Toast.makeText(getApplicationContext(), getString(string.network_something_wrong), Toast.LENGTH_SHORT).show();
                logOut();

            }
        });

        networkController.PostMethod(data.profile_Api, postData);
    }

    public void showAlertDialog(String title, String Message, Boolean Cancelable, Boolean logout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setCancelable(Cancelable)
                .setMessage(Message)
                .setPositiveButton(getString(string.Continue), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (logout) {
                            logOut();
                        }
                    }
                })
                .show();
    }

    public void performLoginAuth() {
        functions.showProgress(lottie);
        loginUsername = sharedpreferences.getString("username", "");
        loginPassword = sharedpreferences.getString("password", "");

        HashMap<String, String> postData = new HashMap<>();
        postData.put("username", loginUsername);
        postData.put("password", loginPassword);

        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) {
                if (!functions.isJsonObject(response)) {
                    functions.showSnackBarError(getString(string.something_wrong), findViewById(android.R.id.content), getApplicationContext());
                    return;
                }
                if (functions.isJsonObject(response)) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String response_code = jsonResponse.get("responseCode").toString();

                        if (response_code.equals("1")) {
                            userID = jsonResponse.getString("userID");
                            //LOAD PROFILE
                            //functions.showSnackBar(getString(string.loginSuccessful), findViewById(android.R.id.content), getApplicationContext());
                            loadUserProfile(userID);
                        }

                        if (response_code.equals("0")) {
                            functions.hideProgress(lottie);
                            String errorMsg = jsonResponse.get("message").toString();
                            functions.showSnackBarError(errorMsg, findViewById(android.R.id.content), getApplicationContext());
                            logOut();
                        }
                    } catch (JSONException e) {
                    }
                }
            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
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
            case id.settings:
                Settings settings = new Settings();
                functions.LoadFragment(settings, "", Frontpage.this, false,false);
                break;

            case id.myNotifications:
                if (userLoggedIn) {
                    showNotifications();
                } else {
                    functions.showSnackBarError(getString(R.string.not_logged_in), findViewById(android.R.id.content), getApplicationContext());
                }
                break;

            case id.timeline:
                if (userLoggedIn) {
                    Fragment timelineFragment = Timeline.newInstance();
                    functions.LoadFragment(timelineFragment, "timelinePosts", Frontpage.this, true,false);
                } else {
                    functions.showSnackBarError(getString(R.string.not_logged_in), findViewById(android.R.id.content), getApplicationContext());
                }
                break;


            case id.profile:
                if (userLoggedIn) {
                    showProfilePage();
                } else {
                    functions.showSnackBarError(getString(R.string.not_logged_in), findViewById(android.R.id.content), getApplicationContext());
                }
                break;

            case id.friends:
                if (userLoggedIn) {
                    showFriendsPage();
                } else {
                    functions.showSnackBarError(getString(R.string.not_logged_in), findViewById(android.R.id.content), getApplicationContext());
                }
                break;

            case id.messages:
                if (userLoggedIn) {
                    showMessagesPage();
                } else {
                    functions.showSnackBarError(getString(R.string.not_logged_in), findViewById(android.R.id.content), getApplicationContext());
                }
                break;


            case R.id.closeapp:
                finishAffinity();
                break;
        }

        drawerLayout.closeDrawer(sideNavView);
        return false;
    }
}
