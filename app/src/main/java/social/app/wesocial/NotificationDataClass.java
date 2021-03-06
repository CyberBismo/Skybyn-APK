package social.app.wesocial;

public class NotificationDataClass {
    private String Content;
    private String avatarLink;
    private String date;
    private String title;
    private String type;
    private String read;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    private String ID;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public NotificationDataClass(String Content, String avatarLink, String date, String title, String type, String ID, String read) {
        this.Content = Content;
        this.avatarLink = avatarLink;
        this.date = date;
        this.title = title;
        this.type = type;
        this.ID = ID;
        this.read = read;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
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