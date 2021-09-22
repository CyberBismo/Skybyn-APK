package social.app.wesocial;

public class Data {

    public String domain = "https://wesocial.space/";
    public String API = domain+"api/";
    public String commentAPI = API+"comment/";
    public String friendAPI = API+"friend/";
    public String messageAPI = API+"message/";
    public String notificationAPI = API+"notification/";
    public String postAPI = API+"post/";

    public String version_url = domain+"apk/version.php";
    public String apk_name = "WeSocial.apk";
    public String apk_url = domain+"apk/".concat(apk_name);
    public String login_Api = API+"login.php";
    public String like_Api = API+"like.php";

    public String registration_Api = API+"reg.php";
    public String verifyEmail_Api = API+"verify.php";
    public String profile_Api = API+"profile.php";
    public String search_Api = API+"search.php";
    //  notification Api
    public String list_notification_Api = notificationAPI+"list.php";
    public String showMessages_API = messageAPI+"messages.php";
    public String read_notification_Api = notificationAPI+"read.php";
    public String delete_notification_Api = notificationAPI+"delete.php";
    public String create_notification_Api = notificationAPI+"create.php";
    // Timeline Api
    public String addPost_Api = postAPI+"add.php";
    public String timeline_Api = postAPI+"timeline.php";
    public String deletePost_Api = postAPI+"delete.php";
    public String editPost_Api = API+"edit.php";
    //  FRIENDS Api
    public String add_friend_action = "add";
    public String block_friend_action = "block";
    public String unblock_friend_action = "unblock";
    public String accept_friend_action = "accept";
    public String remove_friend_action = "remove";

    public String list_friends_Api = friendAPI+"friends.php";
    public String add_friend_Api = friendAPI+"add.php";
    public String decline_friend_Api = friendAPI+"decline.php";
    public String list_friendRequests_Api = friendAPI+"requests.php";
    public String list_blockedFriends_Api = friendAPI+"blocked.php";
    public String remove_friend_Api = friendAPI+"remove.php";
    public String unblock_friend_Api = friendAPI+"unblock.php";
    public String block_friend_Api = friendAPI+"block.php";
    public String accept_friend_Api = friendAPI+"accept.php";
    public String cancel_friend_Api = friendAPI+"cancel.php";

    // COMMENTS Api
    public String list_comments_Api = commentAPI+"list.php";
    public String add_comment_Api = commentAPI+"add.php";
    public String deleteComment_Api = commentAPI+"delete.php";
    public String editComment_Api = commentAPI+"edit.php";


    public String forgotPassword_Api = API+"forgot.php";
    public String login_auth = "login_auth";
    public String fingerprint_auth = "fingerprint_auth";
    public Integer maxPostDisplayLength = 700;
    public Integer maxCommentPostLength = 2000;


    public String getApkName() {
        return "WeSocial.apk";
    }
}
