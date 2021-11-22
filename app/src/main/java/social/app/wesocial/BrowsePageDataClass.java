package social.app.wesocial;

public class BrowsePageDataClass {
    private String pageID;
    private String pageName;
    private String pageAvatarLink;
    private String pageLock;
    private String pageDesc;
    String pageMembers;
    String pageAmIAMember;


    public String getPageMembers() {
        return pageMembers;
    }

    public void setPageMembers(String pageMembers) {
        this.pageMembers = pageMembers;
    }

    public String getPageAmIAMember() {
        return pageAmIAMember;
    }

    public void setPageAmIAMember(String pageAmIAMember) {
        this.pageAmIAMember = pageAmIAMember;
    }


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

    public BrowsePageDataClass(String pageID, String pageAvatarLink, String pageLock, String pageName, String pageDesc, String pageMembers, String pageAmIAMember) {
        this.pageID = pageID;
        this.pageAvatarLink = pageAvatarLink;
        this.pageName = pageName;
        this.pageLock = pageLock;
        this.pageDesc = pageDesc;
        this.pageMembers = pageMembers;
        this.pageAmIAMember = pageAmIAMember;


    }
}
