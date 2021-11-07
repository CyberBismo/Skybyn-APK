package social.app.wesocial;

public class BrowsePageDataClass {
    private String pageID;
    private String pageName;
    private String pageAvatarLink;
    private String pageLock;
    private String pageDesc;

    public String getPageLock() {
        return pageLock;
    }

    public void setPageLock(String pageLock) {
        this.pageLock = pageLock;
    }

    public String getPageID() {
        return pageID;
    }

    public void setPageID(String pageID) {
        this.pageID = pageID;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPageAvatarLink() {
        return pageAvatarLink;
    }

    public void setPageAvatarLink(String pageAvatarLink) {
        this.pageAvatarLink = pageAvatarLink;
    }

    public String getPageDesc() {
        return pageDesc;
    }

    public void setPageDesc(String pageDesc) {
        this.pageDesc = pageDesc;
    }

    public BrowsePageDataClass(String pageID, String pageAvatarLink, String pageLock, String pageName,String pageDesc) {
        this.pageID = pageID;
        this.pageAvatarLink = pageAvatarLink;
        this.pageName = pageName;
        this.pageLock = pageLock;
        this.pageDesc = pageDesc;



    }
}
