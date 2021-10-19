package social.app.wesocial;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;

import kotlin.TypeCastException;
import timber.log.Timber;

public class Functions {
    public static FrameLayout fragmentContainerViewFrame;
    Integer initial_height = null;
    CoordinatorLayout bottomLayout;
    ConstraintLayout fragmentConstraintLayout;
    String oldFragString;
    Context context;
    private View t_View;

    public Functions(Context context) {
        this.context = context;
    }

    public Boolean isJsonObject(String json) {
        return json.startsWith("{");
    }

    public Boolean isJsonArray(String json) {
        return json.startsWith("[") && json.endsWith("]");
    }

    public Boolean validateEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    void loadTimeLineUserProfile(String userID, Activity activity, Context context) {
        Intent i = new Intent(context, userTimelineActivity.class);
        i.putExtra("userID", userID);
        activity.startActivity(i);
    }


    public void showProgress(LottieAnimationView lottieview) {
        lottieview.setVisibility(LottieAnimationView.VISIBLE);
        lottieview.bringToFront();
        lottieview.setAnimation(R.raw.wesocialdot);
        lottieview.setBackgroundColor(ContextCompat.getColor(lottieview.getContext(), R.color.white));
        lottieview.setAlpha(0.9f);
        lottieview.setRepeatMode(LottieDrawable.REVERSE);
        lottieview.setRepeatCount(9999999);
        lottieview.setEnabled(false);
        lottieview.setClickable(false);
        lottieview.playAnimation();

    }

    public void showProgressNoBackground(LottieAnimationView lottieview) {
        lottieview.setVisibility(LottieAnimationView.VISIBLE);
        lottieview.bringToFront();
        lottieview.setBackgroundColor(ContextCompat.getColor(lottieview.getContext(), R.color.transparent_full));
        lottieview.setAnimation(R.raw.wesocialdot);
        lottieview.setAlpha(0.85f);
        lottieview.setRepeatMode(LottieDrawable.REVERSE);
        lottieview.setRepeatCount(9999999);
        lottieview.setEnabled(false);
        lottieview.setClickable(false);
        lottieview.playAnimation();

    }


    public void ShowToast(Object string) {
        Toast.makeText(context, string.toString(), Toast.LENGTH_LONG).show();
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

    public void loadProfilePictureDrawableThumb(Object thumbNailLink, View imv) {
        Glide.with(imv)
                .load(thumbNailLink)
                .placeholder(R.drawable.profile_gray)
                .error(R.drawable.profile_gray)
                .into((ImageView) imv);

    }

    public void showSnackBar(String msg, View view, Context context) {

        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(context, R.color.dark_gray_2))
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

    public void setOtherFragmentHeight() {
        //INCREASE THE HEIGHt
        bottomLayout.setVisibility(View.INVISIBLE);
        Frontpage.isTimeline = false;
        fragmentContainerViewFrame.getLayoutParams().height = ConstraintLayout.LayoutParams.MATCH_PARENT;

    }


    public void setTimelineFragmentHeight() {
        //MAINTAIN CURRENT HEIGHT
        bottomLayout.setVisibility(View.VISIBLE);
        bottomLayout.bringToFront();
        Frontpage.isTimeline = true;
        fragmentContainerViewFrame.getLayoutParams().height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
    }

    public void removeFragment(Fragment fragment, Activity activity) {
        FragmentActivity fragActivity = (FragmentActivity) activity;
        FragmentManager manager = fragActivity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fragment);
        Timber.w("REMOVED");
        transaction.commit();

    }

    public void LoadFragment(Fragment fragment, String fragString, Activity activity, Boolean isTimeline, Boolean isFullChat) {
        FragmentActivity fragActivity = (FragmentActivity) activity;
        FragmentManager manager = fragActivity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragmentContainerViewFrame = activity.findViewById(R.id.fragmentContainerViewFrame);
        fragmentConstraintLayout = activity.findViewById(R.id.fragmentConstraintLayout);
        fragmentConstraintLayout.requestLayout();
        bottomLayout = activity.findViewById(R.id.bottomLayout);

        /*if (fragString.equalsIgnoreCase(oldFragString)) {

       }*/

        if (initial_height == null) {
            initial_height = fragmentConstraintLayout.getLayoutParams().height;
        }

        if (isTimeline) {
            setTimelineFragmentHeight();
        } else {
            setOtherFragmentHeight();

        }

        transaction.replace(R.id.fragmentContainerViewFrame, fragment, fragString);
        transaction.addToBackStack(null);
        transaction.setReorderingAllowed(true);
        transaction.commit();
        oldFragString = fragString;
    }


    public String convertUnixToDateAndTime(Long UnixDateLong) {
        try {
            Date date = new Date(UnixDateLong * 1000L); // convert seconds to milliseconds
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMM. dd yyyy hh:mm a"); // the format of your date

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
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMM. dd yyyy hh:mm a"); // the format of your date

            return dateFormat.format(date);
        } catch (TypeCastException e) {
            return "Loading ...";

        }
    }
}




