package social.app.wesocial;

import android.os.Environment;

public class Data {
    public String version_url = "https://wesocial.space/apk/version.php";
    public String apk_name = "WeSocial.apk";
    public String apk_url = "https://wesocial.space/apk/".concat(apk_name);
    public String apk_download_path = Environment.getDownloadCacheDirectory().getPath();

    public String server_Api = "https://wesocial.space/api";

    public String getApkName(){
        return "WeSocial.apk";

    }
}
