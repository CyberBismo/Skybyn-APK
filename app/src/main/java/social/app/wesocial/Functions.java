package social.app.wesocial;

import android.content.Context;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;

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

    public void  loadProfilePictureThumb(String thumbNailLink, View imv){
        Data data= new Data();
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
        Data data= new Data();
        Glide.with(imv)
                .load(thumbNailLink)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .into((ImageView) imv);

    }


    public String  convertUnixToDateAndTime(Long UnixDateLong) {
        SimpleDateFormat SDF;
        Long Date = UnixDateLong * 1000L;
        SDF = new SimpleDateFormat("dd-MMM-yyyy HH:MM:SS");
        return SDF.format(Date);
    }



}
