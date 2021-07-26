package social.app.wesocial;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.aghamiri.fastdl.DownloadManager;
import com.aghamiri.fastdl.OnDownloadProgressListener;
import com.android.volley.VolleyError;

import org.jetbrains.annotations.Nullable;

import java.util.InputMismatchException;

import kotlin.jvm.Throws;

import static social.app.wesocial.R.*;


public class Frontpage extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    DrawerLayout drawerLayout;
    Data data = new Data();
    Functions functions = new Functions();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_front_page);
        sharedpreferences = getSharedPreferences(getString(string.app_name), Context.MODE_PRIVATE);

        configureToolbarAndDrawer();
        checkAppUpdate();
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

    private void checkAppUpdate(){
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
        Toast.makeText(getApplicationContext(), "CURRENT VERSIONCODE:"+currentVersion.toString(), Toast.LENGTH_LONG).show();
        NetworkController networkController = new NetworkController(getApplicationContext(), new NetworkController.IResult() {
            @Override
            public void notifySuccess(String response) {
                try{
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                Integer serverVersion = Integer.valueOf(response);

                if (serverVersion > currentVersion) {
                    Toast.makeText(getApplicationContext(), getString(R.string.downloading_latest), Toast.LENGTH_SHORT).show();
                    //DOWNLOAD AND INSTALL
                    String downloadLink = data.apk_url;
                    String downloadPath = data.apk_download_path;
                    DownloadManager dlManager = new DownloadManager(downloadLink, downloadPath, new OnDownloadProgressListener() {
                        @Override
                        public void percent(int i) {

                        }

                        @Override
                        public void downloadStart() {
                            Toast.makeText(getApplicationContext(), getString(R.string.app_updating), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void downloadFail(@Nullable String s) {

                        }

                        @Override
                        public void downloadCancel() {

                        }

                        @Override
                        public void downloadedSuccess() {
                            Toast.makeText(getApplicationContext(), getString(R.string.app_updated), Toast.LENGTH_LONG).show();
                        }
                    }
                    );
                }
                }catch(Exception e){


                }
            }

            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(getApplicationContext(),getString(R.string.network_something_wrong),Toast.LENGTH_SHORT).show();
            }
        });
        networkController.GetMethod(data.version_url);

    }
}

