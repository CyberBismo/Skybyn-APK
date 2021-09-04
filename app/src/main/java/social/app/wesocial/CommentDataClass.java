package social.app.wesocial;

public class CommentDataClass {
    private String commentID;
    private String userID;
    private String username;
    private String avatarLink;
    private String date;
    private String content;
    private String likes;
    private String iLike;
    private String postID;

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
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

    public CommentDataClass(String commentID, String userID, String username,
                            String avatarLink, String date, String content,String likes, String iLike,String postID) {
        this.commentID = commentID;
        this.avatarLink = avatarLink;
        this.date = date;
        this.iLike = iLike;
        this.username = username;
        this.content = content;
        this.userID = userID;
        this.likes = likes;
        this.postID = postID;


    }
}
