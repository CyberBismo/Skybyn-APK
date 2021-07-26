package social.app.wesocial

import android.app.Activity
import android.content.Context
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
//import androidx.core.view.isVisible
//import com.airbnb.lottie.LottieAnimationView
//import com.airbnb.lottie.LottieDrawable
//import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
//import com.thecode.aestheticdialogs.AestheticDialog
//import com.thecode.aestheticdialogs.DialogStyle
//import com.thecode.aestheticdialogs.DialogType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

 public object MyFunctions {

    fun isJsonObject(json: String): Boolean {
        return json.startsWith("{") }

    fun validateEmail(email: String, length: Int): Boolean {
        return email.length > length == true and Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String, length: Int): Boolean {
        return password.length >= length
    }

  /*  fun glideLoadProfilePicture(view:ImageView, image_url:String){
        Glide.with(view)
            .load(MyAppData.serverProfilePicturesFolder()+image_url)
            .placeholder(R.drawable.user_gray)
            .into(view)
    }
*/
    fun isJsonArray(json: String): Boolean {
        return json.startsWith("[") }

  /*  fun showProgress(lottieview:LottieAnimationView) {
        lottieview.isVisible = true
        lottieview.bringToFront()
        lottieview.setAnimation(R.raw.ball_loading)
        lottieview.alpha= 0.8F
        lottieview.repeatCount = LottieDrawable.INFINITE

        lottieview.playAnimation()

    }
    fun showProgressTwo(lottieview:LottieAnimationView,Int:Int) {
        lottieview.isVisible = true
        lottieview.bringToFront()
        lottieview.setAnimation(Int)
        lottieview.alpha= 0.8F
        lottieview.repeatCount = LottieDrawable.INFINITE
        lottieview.playAnimation()

    }


    fun hideProgress(lottieview:LottieAnimationView) {
        lottieview.pauseAnimation()
        lottieview.isVisible = false
    }
*/
    fun showSnackBar(msg: String,view: View,context:Context) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(context, R.color.green))
            .setTextColor(ContextCompat.getColor(context, R.color.white))
            .show()
    }

    fun showSnackBarError(msg: String,view:View,context: Context) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(context, R.color.white))
            .setBackgroundTint(ContextCompat.getColor(context, R.color.red))
            .show()
    }



    fun currencyformat(amount:Int):String{
        val str:String = NumberFormat.getInstance(Locale.ENGLISH).format(amount.toLong())
        return  str
    }


    fun convertUnixToDate(UnixDate:Long): String? {
        val SDF:SimpleDateFormat
        val Date:Long = UnixDate * 1000L
        SDF = SimpleDateFormat("dd\nMMM\n\nyyyy")
        return SDF.format(Date)
    }

    fun convertUnixToDateAndTime(UnixDate:Long): String? {
        val SDF:SimpleDateFormat
        val Date:Long = UnixDate * 1000L
        SDF = SimpleDateFormat("dd,MMM,yyyy HH:MM:SS")
        return SDF.format(Date)
    }

  /*  fun SuccessDialog(activity: Activity,message:String,title:String){
        AestheticDialog.Builder(activity,DialogStyle.FLASH,DialogType.SUCCESS).show()
    }
    fun InfoDialog(activity: Activity,message:String,title:String){
        AestheticDialog.Builder(activity,DialogStyle.RAINBOW,DialogType.WARNING).show()
    }


    fun FailureDialog(activity: Activity,message:String,title:String){
        AestheticDialog.Builder(activity,DialogStyle.FLASH,DialogType.ERROR).show()
    }
*/

}

