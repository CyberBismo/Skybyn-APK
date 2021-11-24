package social.app.wesocial;

public class Data {

    // API shortener
    public String domain = "https://wesocial.space/";
    public String API = domain + "api/";
    public String commentAPI = API + "comment/";
    public String friendAPI = API + "friend/";
    public String messageAPI = API + "message/";
    public String notificationAPI = API + "notification/";
    public String postAPI = API + "post/";
    public String pageAPI = API + "page/";

    public String version_url = domain + "apk/version.php";
    public String apk_name = "WeSocial.apk";
    public String apk_url = domain + "apk/".concat(apk_name);
    public String login_API = API + "login.php";
    public String like_API = API + "like.php";

    // General API
    public String registration_API = API + "reg.php";
    public String verifyEmail_API = API + "verify.php";
    public String profile_API = API + "profile.php";
    public String search_API = API + "search.php";

    // Notification API
    public String list_notification_API = notificationAPI + "list.php";
    public String read_notification_API = notificationAPI + "read.php";
    public String delete_notification_API = notificationAPI + "delete.php";
    public String create_notification_API = notificationAPI + "create.php";

    // Messages API
    public String showMessages_API = messageAPI + "messages.php";
    public String showFullMessages_API = messageAPI + "chat.php";
    public String sendMessage_API = messageAPI + "add.php";

    // Timeline API
    public String addPost_API = postAPI + "add.php";
    public String timeline_API = postAPI + "timeline.php";
    public String user_timeline_API = postAPI + "user-timeline.php";
    public String deletePost_API = postAPI + "delete.php";
    public String editPost_API = API + "edit.php";

    // Friends API
    public String add_friend_action = "add";
    public String block_friend_action = "block";
    public String unblock_friend_action = "unblock";
    public String accept_friend_action = "accept";
    public String remove_friend_action = "remove";

    public String list_friends_API = friendAPI + "friends.php";
    public String add_friend_API = friendAPI + "add.php";
    public String decline_friend_API = friendAPI + "decline.php";
    public String list_friendRequests_API = friendAPI + "requests.php";
    public String list_blockedFriends_API = friendAPI + "blocked.php";
    public String remove_friend_API = friendAPI + "remove.php";
    public String unblock_friend_API = friendAPI + "unblock.php";
    public String block_friend_API = friendAPI + "block.php";
    public String accept_friend_API = friendAPI + "accept.php";
    public String cancel_friend_API = friendAPI + "cancel.php";

    // Comments API
    public String list_comments_API = commentAPI + "list.php";
    public String add_comment_API = commentAPI + "add.php";
    public String deleteComment_API = commentAPI + "delete.php";
    public String editComment_API = commentAPI + "edit.php";

    // Page API
    public String page_search_API = pageAPI + "search.php";
    //  POST Parameters:
    //      name

    public String page_list_API = pageAPI + "list.php";
    //  POST Parameters:
    //      userID

    public String page_member_of_API = pageAPI + "member_of.php";
    //  POST Parameters:
    //      userID

    public String page_mypages_API = pageAPI + "mypages.php";
    //  POST Parameters:
    //      userID

    public String create_page_API = pageAPI + "create.php";
    //  POST Parameters:
    //      userID
    //      name
    //      desc
    //      password
    //      special = group (2), private (1) or public (0)

    public String page_content_API = pageAPI + "content.php";
    //  POST Parameters:
    //      pageID
    //      userID

    public String page_timeline_API = pageAPI + "timeline.php";
    //  POST Parameters:
    //      userID
    //      pageID

    public String page_join_API = pageAPI + "join.php";
    //  POST Parameters:
    //      userID
    //      pageID
    public String page_leave_API = pageAPI + "leave.php";
    //  POST Parameters:
    //      userID
    //      pageID

    public String page_gallery_API = pageAPI + "gallery.php";
    //  POST Parameters:
    //      pageID

    public String page_members_API = pageAPI + "members.php";
    //  POST Parameters:
    //      pageID

    public String requestSuccessful = "1";
    public String requestFailed = "0";

    public String forgotPassword_API = API + "forgot.php";
    public String updatePassword_API = API + "profile_password.php";
    public String sendEmailToken_API = API + "profile_email.php";
    public String updateEmail_API = API + "profile_email_update.php";
    public String updateToken_API = API + "token.php";
    public String update_profile_API = API + "profile_update.php";

    public String login_auth = "login_auth";
    public String fingerprint_auth = "fingerprint_auth";
    public String intent_auth = "intent_auth";
    public Integer maxPostDisplayLength = 400;
    public Integer maxCommentPostLength = 2000;

    public String qr_generator_API = API + "qr_gen.php";
    //  POST Parameters:
    //      data
    public String qr_check_API = API + "qr_check.php";
    //  POST Parameters:
    //      code (scanned code)
    public String qr_updater_API = API + "qr_update.php";
    //  POST Parameters:
    //      code (scanned code)
    //      login (userID)

    public String getApkName() {
        return apk_name;
    }
}
