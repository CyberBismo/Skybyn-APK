package social.app.wesocial;

import static social.app.wesocial.R.drawable;
import static social.app.wesocial.R.id;
import static social.app.wesocial.R.layout;
import static social.app.wesocial.R.string;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.mikepenz.actionitembadge.library.ActionItemBadge;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;


public class Frontpage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static SharedPreferences sharedpreferences;
    public static Boolean gottenToken = false;
    public static String userID;
    public static String loadedMessagesJson = "";
    public static String loginUsername, loginPassword;
    public static String firstName, lastName, middleName, nickName, avatarLink, userTitle, userRank, banned, banned_reason, visible, deactivated, deactivated_reason, aboutMe;
    public static Activity frontpageActivity;
    public static SearchView searchView;
    public static String current_chat_user;
    public static Boolean isTimeline;
    public static String username = "", email = "";
    public static String notificationToken = "";
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected static boolean isVisible = false;
    TextView notificationBadge;
    DrawerLayout drawerLayout;
    Data data = new Data();
    Functions functions;
    String loginAction = "";
    DownloadManager downloadManager;
    LottieAnimationView lottie;
    long downLoadId;
    Integer unreadNotifications = 0;
    ImageView imgNavProfilePicture;
    View navHeaderView;
    NavigationView sideNavView;
    TextView txtNavViewUsername;
    TextView txtNavViewUserEmail;
    TextView txtSideNavNotifications;
    Boolean userLoggedIn = false;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton fab;
    Fragment timelineFragment;
    Menu activityTopMenuItem;
    int friendRequestsCount = 0;
    private String keyword;
    private Boolean onQuery;

    @Override
    public void onResume() {
        super.onResume();
        setVisible(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        setVisible(false);
    }


    void retrieveIntentChatNotificationData() {
        Intent i = getIntent();
        String frienduserID = "";
        if (i.hasExtra("friendID")) {
            frienduserID = i.getStringExtra("friendID");
            showMessages(frienduserID);
            return;
        } else {

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_front_page);

        //INIT FIREBASE and Functions
        functions = new Functions(getApplicationContext());

        sharedpreferences = getSharedPreferences(getString(string.app_name), Context.MODE_PRIVATE);

        lottie = findViewById(id.frontpageProgressView);
        sideNavView = findViewById(id.sideNavView);
        navHeaderView = sideNavView.getHeaderView(0);
        bottomNavigationView = (BottomNavigationView) findViewById(id.bottomNavigationView);
        fab = (FloatingActionButton) findViewById(id.fab);
        sideNavView.setNavigationItemSelectedListener(this);
        imgNavProfilePicture = navHeaderView.findViewById(R.id.imgNavViewProfilePicture);
        txtNavViewUsername = navHeaderView.findViewById(R.id.txtNavViewUsername);
        txtNavViewUserEmail = navHeaderView.findViewById(id.txtNavViewEmail);
        TextView txtNavViewCreateNewPage = navHeaderView.findViewById(id.txtNavViewCreateNewPage);

        txtNavViewCreateNewPage.setOnClickListener(view -> {
            drawerLayout.closeDrawer(GravityCompat.START);

            Fragment createNewPage= CreatePage.newInstance("","");
            functions.LoadFragment(createNewPage,"",this,false,false);

        });

        imgNavProfilePicture.setOnClickListener(view -> showProfilePage());

        setVisible(true);
        //Fab On CLick
        fab.setOnClickListener(view -> {
            Fragment sharePostFragment = SharePost.newInstance();
            functions.LoadFragment(sharePostFragment, "sharepost", Frontpage.this, false, false);
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case id.timeline:
                        showTimeline();
                        return true;

                    case id.messages:
                        showMessages("");
                        return true;
                }
                return false;
            }
        });

        //GETTING REFERENCe To the activity
        frontpageActivity = Frontpage.this;
        Intent intent = getIntent();

        if (intent.hasExtra("loginAction")) {
            loginAction = intent.getStringExtra("loginAction");
            if (loginAction.equals(data.fingerprint_auth)) {
                //perform login
                performLoginAuth();
            } else {
                userID = sharedpreferences.getString("userID", "");
                loadUserProfile(userID);
            }
        }
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
                    functions.LoadFragment(searchFragment, "search", Frontpage.this, false, false);
                    functions.showSnackBar(getString(R.string.we_found_result) + keyword, findViewById(android.R.id.content), getApplicationContext());

                }

            }

            @Override
            public void notifyError(VolleyError error) {
                functions.hideProgress(lottie);
                Toast.makeText(getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });
        networkController.PostMethod(data.search_API, postData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);

        //you can add some logic (hide it if the count == 0)
        activityTopMenuItem = menu;


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.topnav_menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment searchFragment = Search.newInstance("", "");
                functions.LoadFragment(searchFragment, "search", Frontpage.this, false, false);
            }
        });


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                return true;
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
        functions.LoadFragment(notificationFragment, "notification", Frontpage.this, false, false);
    }

    public void showFriendsPage() {
        Fragment friendsFragment = Friends.newInstance(userID);
        functions.LoadFragment(friendsFragment, "friends", Frontpage.this, false, false);
    }

    public void showProfilePage() {
        functions.loadTimeLineUserProfile(userID, Frontpage.this, getApplicationContext());
    }

    public void showMessages(String friendID) {
        Fragment messagesFragment = Messages.newInstance(loadedMessagesJson, friendID);
        functions.LoadFragment(messagesFragment, "messages", Frontpage.this, false, false);
    }

    public void showPages() {
        Fragment BrowsePages = social.app.wesocial.BrowsePages.newInstance();
        functions.LoadFragment(BrowsePages, "pages", Frontpage.this, false, false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case id.topnav_friends:
                showFriendsPage();
                break;

            case id.topnav_notifications:
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

    private void loadMessagesRequests() throws JSONException {
        //functions.showProgressNoBackground(lottie);
        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);


        NetworkController networkController = new NetworkController(getApplicationContext(),
                new NetworkController.IResult() {
                    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
                    @Override
                    public void notifySuccess(String response) throws JSONException {
                        functions.hideProgress(lottie);
                        if (functions.isJsonArray(response)) {
                            Timber.i(response);
                            loadedMessagesJson = response;

                        }
                        if (!functions.isJsonArray(response)) {

                        }

                    }


                    @Override
                    public void notifyError(VolleyError error) {
                        functions.hideProgress(lottie);

                    }
                });

        networkController.PostMethod(data.showMessages_API, postData);
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

    private void showNotificationBadge(int value, Menu menu) {
        if (isVisible) {
        if (value > 0) {
            ActionItemBadge.update(this, menu.findItem(id.topnav_notifications), getDrawable(drawable.notifications), ActionItemBadge.BadgeStyles.RED, value);
        } else {
            value = 0;
            ActionItemBadge.update(this, menu.findItem(id.topnav_notifications), getDrawable(drawable.notifications), ActionItemBadge.BadgeStyles.GREY, value);
        }
    }
    }

    private void showFriendRequestsBadge(int value, Menu menu) {
        if (isVisible) {
            if (value > 0) {
                ActionItemBadge.update(this, menu.findItem(id.topnav_friends), getDrawable(drawable.friends), ActionItemBadge.BadgeStyles.RED, value);
            } else {
                value = 0;
                ActionItemBadge.update(this, menu.findItem(id.topnav_friends), getDrawable(drawable.friends), ActionItemBadge.BadgeStyles.GREY, value);
            }
        }
    }

    public void loadFriendsRequests() {
        functions.showProgress(lottie);
        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);
        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void notifySuccess(String response) throws JSONException {
                functions.hideProgress(lottie);

                if (functions.isJsonArray(response)) {
                    String friendUsername;
                    String friendNickname;
                    String friendID;
                    String friendAvatarLink;

                    JSONArray jsonArray = new JSONArray(response);
                    ArrayList<FriendsDataClass> friendsDataClass = new ArrayList<>();
                    JSONObject jsonObject;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        friendID = (String) jsonObject.get("friend_id");
                        friendAvatarLink = jsonObject.get("avatar").toString();
                        friendNickname = (String) jsonObject.get("nickname");
                        friendUsername = (String) jsonObject.get("username");
                        String friendOnline = (String) jsonObject.get("online");
                        friendRequestsCount++;
                    }
                    if (isVisible) {
                        showFriendRequestsBadge(friendRequestsCount, activityTopMenuItem);
                    }

                }

            }

            @Override
            public void notifyError(VolleyError error) {

                Toast.makeText(getApplicationContext(), getString(R.string.network_something_wrong), Toast.LENGTH_SHORT).show();
            }
        });

        networkController.PostMethod(data.list_friendRequests_API, postData);
    }

    void loadNotificationsRequests() throws JSONException {

        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", Frontpage.userID);

        NetworkController networkController1 = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                if (functions.isJsonArray(response)) {
                    unreadNotifications = 0;
                    String notificationContent;
                    String notificationTitle;
                    String notificationDate;
                    String notificationID;
                    String notificationAvatarLink;
                    String notificationType;
                    String notificationRead;

                    JSONArray jsonArray = new JSONArray(response);
                    ArrayList<NotificationDataClass> notifications = new ArrayList<>();
                    JSONObject jsonObject;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        notificationContent = (String) jsonObject.get("content");
                        notificationAvatarLink = (String) jsonObject.get("avatar");
                        notificationDate = (String) jsonObject.get("date").toString();
                        notificationDate = functions.convertUnixToDateAndTime(Long.valueOf(notificationDate));
                        notificationTitle = (String) jsonObject.get("title");
                        notificationID = (String) jsonObject.get("notiID");
                        notificationRead = (String) jsonObject.get("read");
                        notificationType = (String) jsonObject.get("type");

                        if (Integer.valueOf(notificationRead) == 0) {
                            unreadNotifications = unreadNotifications + 1;
                        }
                    }
                    if (isVisible) {
                        showNotificationBadge(unreadNotifications, activityTopMenuItem);
                    }
                }


            }

            @Override
            public void notifyError(VolleyError error) {

            }
        });

        networkController1.PostMethod(data.list_notification_API, postData);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void loadUserProfile(String myUserID) {
        HashMap<String, String> postData = new HashMap<>();
        postData.put("userID", myUserID);

        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) throws JSONException {
                //Load profile picture thumb after profile loads.
                functions.hideProgress(lottie);

                if (functions.isJsonObject(response.toString())) {
                    Log.i("Json", response);
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String responseCode = jsonObject.get("responseCode").toString();

                    if (responseCode.equals("0")) {
                        Toast.makeText(getApplicationContext(), getString(R.string.invalid_profile), Toast.LENGTH_LONG).show();
                        logOut();
                    }

                    if (responseCode.equals("1")) {
                        //Start the PUSH SERVICE after successful Login
                        Intent intent = new Intent(Frontpage.this, PushNotificationService.class);
                        startService(intent);
                        userLoggedIn = true;

                        ExecutorService service = Executors.newFixedThreadPool(4);
                        service.submit(new Runnable() {
                            public void run() {
                                //Load Messages
                                try {
                                    loadMessagesRequests();
                                    loadNotificationsRequests();
                                    loadFriendsRequests();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        showTimeline();

                        username = jsonObject.getString("username").toString();
                        email = jsonObject.getString("email").toString();
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
                        aboutMe = jsonObject.get("bio").toString();
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

                        if (loginAction.equals(data.intent_auth)) {
                            retrieveIntentChatNotificationData();
                        }


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
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), getString(string.network_something_wrong), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                loadUserProfile(myUserID);
                            }
                        });

                snackbar.show();

            }
        });

        networkController.PostMethod(data.profile_API, postData);
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
                            configureToolbarAndDrawer();
                            loadUserProfile(userID);
                        }

                        if (response_code.equals("0")) {
                            functions.hideProgress(lottie);
                            String errorMsg = jsonResponse.get("message").toString();
                            functions.ShowToast(errorMsg);
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
        networkController.PostMethod(data.login_API, postData);
    }

    public void showTimeline() {
        timelineFragment = Timeline.newInstance();
        functions.LoadFragment(timelineFragment, "timeline", Frontpage.this, true, false);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout:
                logOut();
                break;
            case id.settings:
                Settings settings = new Settings();
                functions.LoadFragment(settings, "settings", Frontpage.this, false, false);
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
                    showTimeline();
                } else {
                    functions.showSnackBarError(getString(R.string.not_logged_in), findViewById(android.R.id.content), getApplicationContext());
                }
                break;


            case id.pages:
                if (userLoggedIn) {
                    showPages();
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
                    showMessages("");
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

    public void updateNavView(NavigationView navView, int resId, String count) {
        MenuItem item = navView.getMenu().findItem(resId); //ex. R.id.nav_item_friends
        MenuItemCompat.setActionView(item, layout.notification_badge_layout);
        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);
        TextView tv = (TextView) notifCount.findViewById(R.id.textMenuItemCount);

        if (count != null) {
            tv.setText(count);
        } else {
            tv.setText("");
            item.setEnabled(false);
        }
    }

}
