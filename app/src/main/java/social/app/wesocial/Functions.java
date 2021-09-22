package social.app.wesocial;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;

import kotlin.TypeCastException;

public class Functions {

    public static FragmentContainerView fragmentContainerView;

    public Boolean isJsonObject(String json) {
        return json.startsWith("{");
    }

    public Boolean isJsonArray(String json) {
        return json.startsWith("[");
    }

    public Boolean validateEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void showProgress(LottieAnimationView lottieview) {
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

    public void ShowToast(Context context, Object string) {
        Toast.makeText(context, (String) string.toString(), Toast.LENGTH_LONG).show();
    }

    public void showFingerPrintPrompt(LottieAnimationView lottieview) {
        lottieview.setVisibility(LottieAnimationView.VISIBLE);
        lottieview.bringToFront();
        lottieview.setAnimation(R.raw.fingerprint2);
        lottieview.setAlpha(0.9f);
        lottieview.setRepeatMode(LottieDrawable.REVERSE);
        lottieview.setRepeatCount(9999999);
        lottieview.playAnimation();

    }

    public void hideProgress(LottieAnimationView lottieview) {
        lottieview.setVisibility(LottieAnimationView.GONE);
        lottieview.pauseAnimation();

    }

    public void loadProfilePictureDrawableThumb(String thumbNailLink, View imv) {
        Glide.with(imv)
                .load(thumbNailLink)
                .placeholder(R.drawable.profile_gray)
                .error(R.drawable.profile_gray)
                .into((ImageView) imv);

    }

    public void showSnackBar(String msg, View view, Context context) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(context, R.color.main_colour))
                .setTextColor(ContextCompat.getColor(context, R.color.white))
                .show();
    }

    public void showSnackBarError(String msg, View view, Context context) {

        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(context, R.color.red))
                .setTextColor(ContextCompat.getColor(context, R.color.white))
                .show();
    }

    public void loadNotificationThumb(Object thumbNailLink, View imv, Boolean friendRequest) {
        if (friendRequest) {
            Glide.with(imv)
                    .load(thumbNailLink)
                    .placeholder(R.drawable.friend_request)
                    .error(R.drawable.friend_request)
                    .into((ImageView) imv);

        } else {

            Glide.with(imv)
                    .load(thumbNailLink)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .into((ImageView) imv);

        }
    }

    public void LoadFragment(Fragment fragment, String fragString, Activity activity, Boolean isTimeline) {
        FragmentActivity fragActivity = (FragmentActivity) activity;

        fragmentContainerView = activity.findViewById(R.id.fragmentContainerView);
        CoordinatorLayout bottomLayout = activity.findViewById(R.id.bottomLayout);

        if (isTimeline) {
            bottomLayout.setVisibility(View.VISIBLE);
            Frontpage.isTimeline = true;
        } else {
            fragmentContainerView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            bottomLayout.setVisibility(View.INVISIBLE);
            Frontpage.isTimeline = false;
        }


        FragmentManager manager = fragActivity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();


        transaction.replace(R.id.fragmentContainerView, fragment, fragString);
        transaction.addToBackStack(null);

        transaction.commit();

    }

    public String convertUnixToDateAndTime(Long UnixDateLong) {
        try {
            Date date = new Date(UnixDateLong * 1000L); // convert seconds to milliseconds
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("E,dd-MMM-yyyy hh.mm aa"); // the format of your date


            return dateFormat.format(date);
        } catch (TypeCastException e) {
            return "Loading ...";

        }
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

    public String convertUnixToDateAndTimeNoGMT(Long UnixDateLong) {
        try {
            Date date = new Date(UnixDateLong * 1000L); // convert seconds to milliseconds
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("E,dd-MMM-yyyy hh.mm aa"); // the format of your date
            return dateFormat.format(date);
        } catch (TypeCastException e) {
            return "Loading ...";

        }
    }
}




