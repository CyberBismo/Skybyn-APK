package social.app.wesocial;

import android.content.Context;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;

import androidx.core.content.ContextCompat;

public class Functions  {

    public Boolean isJsonObject(String json){
        return json.startsWith("{");

        }

    public Boolean isJsonArray(String json){
        return json.startsWith("[");

    }

    public Boolean validateEmail(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

      public void  showProgress(LottieAnimationView lottieview) {
        lottieview.setVisibility(LottieAnimationView.VISIBLE);
        lottieview.bringToFront();
        lottieview.setAnimation(R.raw.wesocialloading2);
        lottieview.setAlpha(0.8f);
        lottieview.loop(true);
        lottieview.playAnimation();

    }

    public void hideProgress(LottieAnimationView lottieview)
    { lottieview.setVisibility(LottieAnimationView.INVISIBLE);

    }

    public void  loadProfilePictureThumb(String thumbNailLink, View imv){
        Data data= new Data();
        Glide.with(imv)
                .load(thumbNailLink)
                .placeholder(R.drawable.profile_gray)
                .error(R.drawable.profile_gray)
                .into((ImageView) imv);



    }

}
