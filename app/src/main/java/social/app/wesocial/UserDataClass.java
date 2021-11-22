package social.app.wesocial;

public class UserDataClass {
    private String userID;
    private String isFriend;
    private String Username;
    private String userAvatarLink;
    private String userNickname;

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    private String isOnline;

    public String getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(String isFriend) {
        this.isFriend = isFriend;
    }


    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        this.Username = username;
    }

    public String getUserAvatarLink() {
        return userAvatarLink;
    }

    public void setUserAvatarLink(String userAvatarLink) {
        this.userAvatarLink = userAvatarLink;
    }

    public UserDataClass(String userID, String userAvatarLink, String userNickname, String Username, String Friends, String isOnline) {
        this.userID = userID;
        this.userAvatarLink = userAvatarLink;
        this.Username = Username;
        this.userNickname = userNickname;
        this.isFriend = Friends;
        this.isOnline = isOnline;


    }
}
