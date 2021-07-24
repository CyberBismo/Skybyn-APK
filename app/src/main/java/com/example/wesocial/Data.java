package com.example.wesocial;

public class Data {
    public String version_url = "https://wesocial.space/apk/version.php";
    public String apk_url = "https://wesocial.space/apk/WeSocial.apk";
    public String forgotpassword_url = "https://wesocial.space/mob_api?forgot=";
    //public String verifyAccount_url = "https://wesocial.space/mob_api?forgot=";

    public String register_url (){
        return "https://wesocial.space/mob_api?register&email";
    }

    public String verifyAccountUrl(){
        return "https://wesocial.space/mob_api?verify=";
    }

    public String getApkName(){
        return "WeSocial.apk";

    }
}
