package social.app.wesocial;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import kotlin.TypeCastException;

public class Functions  {


    public Boolean isJsonObject(String json){
        return json.startsWith("{"); }

    public Boolean isJsonArray(String json){
        return json.startsWith("["); }

    public Boolean validateEmail(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches(); }

      public void  showProgress(LottieAnimationView lottieview) {
        lottieview.setVisibility(LottieAnimationView.VISIBLE);
        lottieview.bringToFront();
        lottieview.setAnimation(R.raw.wesocialdot);
        lottieview.setAlpha(0.85f);
        lottieview.setRepeatMode(LottieDrawable.REVERSE);
        lottieview.setRepeatCount(9999999);
        lottieview.setEnabled(false);
        lottieview.setClickable(false);
        lottieview.playAnimation();

    }

    public void  showFingerPrintPrompt(LottieAnimationView lottieview) {
        lottieview.setVisibility(LottieAnimationView.VISIBLE);
        lottieview.bringToFront();
        lottieview.setAnimation(R.raw.fingerprint2);
        lottieview.setAlpha(0.9f);
        lottieview.setRepeatMode(LottieDrawable.REVERSE);
        lottieview.setRepeatCount(9999999);
        lottieview.playAnimation();

    }

    public void hideProgress(LottieAnimationView lottieview)
    { lottieview.setVisibility(LottieAnimationView.GONE);
    lottieview.pauseAnimation();

    }



    /*public interface alertDialogListener {
        void DialogPositive(DialogInterface dialogInterface, int i) ;

        void DialogNegative(DialogInterface dialogInterface, int i);
    }

    public void showAlertDialog(alertDialogListener alertDialogListener,Context context,String Title, String message,String positiveText, String negativeText) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(message)
                .setTitle(Title)
                .setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialogListener.DialogPositive(dialogInterface, i);
                    }
                });
        alertDialog.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                alertDialogListener.DialogNegative(dialog, i);
            }
        }).show();

    }

*/
    public void  loadProfilePictureThumb(String thumbNailLink, View imv){
                Glide.with(imv)
                .load(thumbNailLink)
                .placeholder(R.drawable.profile_gray)
                .error(R.drawable.profile_gray)
                        .into((ImageView) imv);




    }

    public void  showSnackBar( String msg ,View view,Context context) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(context, R.color.main_colour))
                .setTextColor(ContextCompat.getColor(context, R.color.white))
                .show();
    }

    public void  showSnackBarError( String msg ,View view,Context context) {

        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(context, R.color.red))
                .setTextColor(ContextCompat.getColor(context, R.color.white))
                .show();
    }

    public void  loadNotificationThumb(String thumbNailLink, View imv){
                Glide.with(imv)
                .load(thumbNailLink)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .into((ImageView) imv);

    }

    public void LoadFragment(Fragment fragment, String fragString, Activity activity, Boolean isTimeline) {
        FragmentContainerView fragmentContainerView = activity.findViewById(R.id.fragmentContainerView);
        FragmentActivity fragActivity = (FragmentActivity) activity;
        FragmentManager manager = fragActivity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(R.id.fragmentContainerView, fragment, fragString);
        manager.popBackStack();
        transaction.addToBackStack(null);
        transaction.setReorderingAllowed(true);
        Frontpage.isTimeline = isTimeline;
        CoordinatorLayout bottomLayout = activity.findViewById(R.id.bottomLayout);

        if (isTimeline){
            bottomLayout.setVisibility(View.VISIBLE);
        }else{

            bottomLayout.setVisibility(View.INVISIBLE);
        }

        transaction.commit();

    }


    public String  convertUnixToDateAndTime(Long UnixDateLong) {
        try {
            Date date = new Date(UnixDateLong*1000L); // convert seconds to milliseconds
            SimpleDateFormat dateFormat = new SimpleDateFormat("E,dd-MMM-yyyy hh.mm aa"); // the format of your date
            String formattedDate = dateFormat.format(date);
            return formattedDate;
        }catch (TypeCastException e) {
            return  "Loading ...";

        }
        }

    public  void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }
    public String  convertUnixToDateAndTimeNoGMT(Long UnixDateLong) {
        try {
            Date date = new Date(UnixDateLong*1000L); // convert seconds to milliseconds
            SimpleDateFormat dateFormat = new SimpleDateFormat("E,dd-MMM-yyyy hh.mm aa"); // the format of your date
            String formattedDate = dateFormat.format(date);
            return formattedDate;
        }catch (TypeCastException e) {
            return  "Loading ...";

        }
    }
    }




