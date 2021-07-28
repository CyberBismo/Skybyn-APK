package social.app.wesocial;

import android.util.Patterns;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import static android.util.Patterns.EMAIL_ADDRESS;

public class Functions {

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


}
