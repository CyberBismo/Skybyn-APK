package social.app.wesocial;

public class MessageListDataClass {
    private String content;
    private String avatarLink;
    private String date;
    private String friendID;
    private String msgID;
    private String userNickName;
    private String userName;
    private String userID;

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    private String online;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    private String ID;

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public MessageListDataClass(String msgID,String Content, String avatarLink, String date, String friendID, String userNickName, String userID,String userName,String online) {
        this.content = Content;
        this.avatarLink = avatarLink;
        this.date = date;
        this.friendID = friendID;
        this.userNickName = userNickName;
        this.userName = userName;
        this.userID = userID;
        this.msgID = msgID;
        this.online = online;
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