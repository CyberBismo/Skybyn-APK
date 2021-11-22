package social.app.wesocial;

public class FriendsDataClass {
    private String friendID;
    private String friendUsername;
    private String friendAvatarLink;
    private String friendNickname;

    public String getFriendOnline() {
        return friendOnline;
    }

    public void setFriendOnline(String friendOnline) {
        this.friendOnline = friendOnline;
    }

    private String friendOnline;

    public String getFriendNickname() {
        return friendNickname;
    }

    public void setFriendNickname(String friendNickname) {
        this.friendNickname = friendNickname;
    }

    public String getFriendID() {
        return friendID;
    }

    public void setFriendID(String friendID) {
        this.friendID = friendID;
    }


    public String getFriendUsername() {
        return friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public String getFriendAvatarLink() {
        return friendAvatarLink;
    }

    public void setFriendAvatarLink(String friendAvatarLink) {
        this.friendAvatarLink = friendAvatarLink;
    }


    public FriendsDataClass(String friendID, String friendAvatarLink, String friendNickname, String friendUsername, String friendOnline) {
        this.friendID = friendID;
        this.friendAvatarLink = friendAvatarLink;
        this.friendUsername = friendUsername;
        this.friendNickname = friendNickname;
        this.friendOnline = friendOnline;


    }
}
