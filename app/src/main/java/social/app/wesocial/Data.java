package social.app.wesocial;

public class Data {
    public String version_url = "https://wesocial.space/apk/version.php";
    public String apk_name = "WeSocial.apk";
    public String apk_url = "https://wesocial.space/apk/".concat(apk_name);
    public String apk_download_path = "/sdcard/";
    public String forgotpassword_url = "https://wesocial.space/api";
    public String login_url = "https://wesocial.space/api";
    public String sign_ur = "https://wesocial.space/api";
    //public String verifyAccount_url = "https://wesocial.space/api?forgot=";

    public String register_url (){
        return "https://wesocial.space/api";
    }

    public String verifyAccountUrl(){
        return "https://wesocial.space/api";
    }

    public String getApkName(){
        return "WeSocial.apk";

    }
}
