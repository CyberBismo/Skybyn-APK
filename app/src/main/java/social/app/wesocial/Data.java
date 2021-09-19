package social.app.wesocial;

public class Data {

    public String version_url = "https://wesocial.space/apk/version.php";
    public String apk_name = "WeSocial.apk";
    public String apk_url = "https://wesocial.space/apk/".concat(apk_name);
    public String login_Api = "https://wesocial.space/api/login.php";
    public String like_Api = "https://wesocial.space/api/like.php";

    public String registration_Api = "https://wesocial.space/api/reg.php";
    public String verifyEmail_Api = "https://wesocial.space/api/verify.php";
    public String profile_Api = "https://wesocial.space/api/profile.php";
    public String search_Api = "https://wesocial.space/api/search.php";
    //  notification Api
    public String list_notification_Api = "https://wesocial.space/api/notification/list.php";
    public String read_notification_Api = "https://wesocial.space/api/notification/read.php";
    public String delete_notification_Api = "https://wesocial.space/api/notification/delete.php";
    public String create_notification_Api = "https://wesocial.space/api/notification/create.php";
    // Timeline Api
    public String addPost_Api = "https://wesocial.space/api/post/add.php";
    public String timeline_Api = "https://wesocial.space/api/post/timeline.php";
    public String deletePost_Api = "https://wesocial.space/api/post/delete.php";
    public String editPost_Api = "https://wesocial.space/api/edit.php";
    //  FRIENDS Api
    public String add_friend_action = "add";
    public String block_friend_action = "block";
    public String unblock_friend_action = "unblock";
    public String accept_friend_action = "accept";
    public String remove_friend_action = "remove";

    public String list_friends_Api = "https://wesocial.space/api/friend/friends.php";
    public String add_friend_Api = "https://wesocial.space/api/friend/add.php";
    public String decline_friend_Api = "https://wesocial.space/api/friend/decline.php";
    public String list_friendRequests_Api = "https://wesocial.space/api/friend/requests.php";
    public String list_blockedFriends_Api = "https://wesocial.space/api/friend/blocked.php";
    public String remove_friend_Api = "https://wesocial.space/api/friend/remove.php";
    public String unblock_friend_Api = "https://wesocial.space/api/friend/unblock.php";
    public String block_friend_Api = "https://wesocial.space/api/friend/block.php";
    public String accept_friend_Api = "https://wesocial.space/api/friend/accept.php";

    // COMMENTS Api
    public String list_comments_Api = "https://wesocial.space/api/comments/list.php";
    public String add_comment_Api = "https://wesocial.space/api/comments/add.php";
    public String deleteComment_Api = "https://wesocial.space/api/comments/delete.php";
    public String editComment_Api = "https://wesocial.space/api/comments/edit.php";


    public String forgotPassword_Api = "https://wesocial.space/api/forgot.php";
    public String login_auth = "login_auth";
    public String fingerprint_auth = "fingerprint_auth";
    public Integer maxPostDisplayLength = 700;
    public Integer maxCommentPostLength = 2000;



    public String generateProfilePictureThumb(String userID, String thumb_name) {
        return "https://wesocial.space/sources/users/avatars/" + userID + "/" + thumb_name;
    }

    public String getApkName() {
        return "WeSocial.apk";
    }
}
