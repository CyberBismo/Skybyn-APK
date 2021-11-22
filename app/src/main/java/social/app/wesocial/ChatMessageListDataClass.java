package social.app.wesocial;

public class ChatMessageListDataClass {
    private String content;
    private String avatarLink;
    private String date;
    private String msgID;
    private String friendID;
    private String userID;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;

    private String online;

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    private String ID;

    public ChatMessageListDataClass(String msgID, String Content, String avatarLink, String date, String friendID, String userID, String username) {
        this.content = Content;
        this.avatarLink = avatarLink;
        this.date = date;
        this.friendID = friendID;
        this.userID = userID;
        this.msgID = msgID;
        this.username = username;

    }

    public String getContent() {
        return content;
    }

    public void setContent(String Content) {
        this.content = Content;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public String getFriendID() {
        return friendID;
    }

    public void setFriendID(String friendID) {
        this.friendID = friendID;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}