package social.app.wesocial;

public class TimelineDataClass {
    private String postID;
    private String userID;
    private String username;
    private String avatarLink;
    private String date;
    private String content;
    private String likes;
    private String iLike;
    private String comments_count;


    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarLink() {
        return avatarLink;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getComments_count() {
        return comments_count;
    }

    public void setComments_count(String comments_count) {
        this.comments_count = comments_count;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getiLike() {
        return iLike;
    }

    public void setiLike(String iLike) {
        this.iLike = iLike;
    }

    public TimelineDataClass(String postID  , String userID, String username,
                             String avatarLink, String date, String content, String comments_count, String likes, String iLike) {
        this.postID = postID;
        this.avatarLink = avatarLink;
        this.date = date;
        this.iLike = iLike;
        this.username = username;
        this.content = content;
        this.userID = userID;
        this.likes = likes;
        this.comments_count = comments_count;

    }
}
