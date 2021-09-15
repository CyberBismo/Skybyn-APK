package social.app.wesocial;

public class Data {

    public String version_url = "https://wesocial.space/apk/version.php";
    public String apk_name = "WeSocial.apk";
    public String apk_url = "https://wesocial.space/apk/".concat(apk_name);
    public String login_Api = "https://wesocial.space/api/login.php";
    public String like_Api = "https://wesocial.space/api/like.php";
    public String deletePost_Api = "https://wesocial.space/api/delPost.php";
    public String deleteComment_Api = "https://wesocial.space/api/delComment.php";
    public String editComment_Api = "https://wesocial.space/api/editComment.php";
    public String editPost_Api = "https://wesocial.space/api/editPost.php";
    public String registration_Api = "https://wesocial.space/api/reg.php";
    public String verifyEmail_Api = "https://wesocial.space/api/verify.php";
    public String profile_Api = "https://wesocial.space/api/profile.php";
    public String search_Api = "https://wesocial.space/api/search.php";
    public String notification_Api = "https://wesocial.space/api/noti.php";
    public String post_Api = "https://wesocial.space/api/post.php";
    public String timeline_Api = "https://wesocial.space/api/timeline.php";
    public String friends_Api = "https://wesocial.space/api/friends.php";
    public String add_friend_Api = "https://wesocial.space/api/friendAdd.php";
    public String remove_friend_Api = "https://wesocial.space/api/friendRem.php";
    public String unblock_friend_Api = "https://wesocial.space/api/friendUnblock.php";
    public String accept_friend_Api = "https://wesocial.space/api/friendAcc.php";
    public String comments_Api = "https://wesocial.space/api/comments.php";
    public String send_comment_Api = "https://wesocial.space/api/comment.php";
    public String forgotPassword_Api = "https://wesocial.space/api/forgot.php";
    public String login_auth = "login_auth";
    public String fingerprint_auth = "fingerprint_auth";
    public Integer maxPostDisplayLength = 700;
    public Integer maxCommentPostLength = 2000;
    public String add_friend_action="add";
    public String block_friend_action="block";
    public String unblock_friend_action="unblock";
    public String accept_friend_action="accept";
    public String remove_friend_action="remove";


    public String generateProfilePictureThumb(String userID, String thumb_name){
        return  "https://wesocial.space/sources/users/avatars/"+userID+"/"+thumb_name;
    }

    public String getApkName(){
        return "WeSocial.apk";
    }
}
