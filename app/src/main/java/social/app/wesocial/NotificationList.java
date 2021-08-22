package social.app.wesocial;

import java.util.Date;

public class NotificationList {
    private String Content;
    private String avatarLink;
    private String date;
    private String title;

    public NotificationList(String Content, String avatarLink, String date,String title) {
        this.Content = Content;
        this.avatarLink = avatarLink;
        this.date = date;
        this.title = title;
    }
    public String getContent() {
        return Content;
    }
    public void setContent(String Content) {
        this.Content = Content;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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