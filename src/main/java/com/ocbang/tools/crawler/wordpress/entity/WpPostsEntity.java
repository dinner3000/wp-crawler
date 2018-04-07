package com.ocbang.tools.crawler.wordpress.entity;

import java.sql.Timestamp;

public class WpPostsEntity {
    private long id;
    private long postAuthor;
    private Timestamp postDate;
    private Timestamp postDateGmt;
    private String postContent;
    private String postTitle;
    private String postExcerpt;
    private String postStatus;
    private String commentStatus;
    private String pingStatus;
    private String postPassword;
    private String postName;
    private String toPing;
    private String pinged;
    private Timestamp postModified;
    private Timestamp postModifiedGmt;
    private String postContentFiltered;
    private long postParent;
    private String guid;
    private int menuOrder;
    private String postType;
    private String postMimeType;
    private long commentCount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPostAuthor() {
        return postAuthor;
    }

    public void setPostAuthor(long postAuthor) {
        this.postAuthor = postAuthor;
    }

    public Timestamp getPostDate() {
        return postDate;
    }

    public void setPostDate(Timestamp postDate) {
        this.postDate = postDate;
    }

    public Timestamp getPostDateGmt() {
        return postDateGmt;
    }

    public void setPostDateGmt(Timestamp postDateGmt) {
        this.postDateGmt = postDateGmt;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostExcerpt() {
        return postExcerpt;
    }

    public void setPostExcerpt(String postExcerpt) {
        this.postExcerpt = postExcerpt;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }

    public String getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
    }

    public String getPingStatus() {
        return pingStatus;
    }

    public void setPingStatus(String pingStatus) {
        this.pingStatus = pingStatus;
    }

    public String getPostPassword() {
        return postPassword;
    }

    public void setPostPassword(String postPassword) {
        this.postPassword = postPassword;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getToPing() {
        return toPing;
    }

    public void setToPing(String toPing) {
        this.toPing = toPing;
    }

    public String getPinged() {
        return pinged;
    }

    public void setPinged(String pinged) {
        this.pinged = pinged;
    }

    public Timestamp getPostModified() {
        return postModified;
    }

    public void setPostModified(Timestamp postModified) {
        this.postModified = postModified;
    }

    public Timestamp getPostModifiedGmt() {
        return postModifiedGmt;
    }

    public void setPostModifiedGmt(Timestamp postModifiedGmt) {
        this.postModifiedGmt = postModifiedGmt;
    }

    public String getPostContentFiltered() {
        return postContentFiltered;
    }

    public void setPostContentFiltered(String postContentFiltered) {
        this.postContentFiltered = postContentFiltered;
    }

    public long getPostParent() {
        return postParent;
    }

    public void setPostParent(long postParent) {
        this.postParent = postParent;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public int getMenuOrder() {
        return menuOrder;
    }

    public void setMenuOrder(int menuOrder) {
        this.menuOrder = menuOrder;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getPostMimeType() {
        return postMimeType;
    }

    public void setPostMimeType(String postMimeType) {
        this.postMimeType = postMimeType;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WpPostsEntity that = (WpPostsEntity) o;

        if (id != that.id) return false;
        if (postAuthor != that.postAuthor) return false;
        if (postParent != that.postParent) return false;
        if (menuOrder != that.menuOrder) return false;
        if (commentCount != that.commentCount) return false;
        if (postDate != null ? !postDate.equals(that.postDate) : that.postDate != null) return false;
        if (postDateGmt != null ? !postDateGmt.equals(that.postDateGmt) : that.postDateGmt != null) return false;
        if (postContent != null ? !postContent.equals(that.postContent) : that.postContent != null) return false;
        if (postTitle != null ? !postTitle.equals(that.postTitle) : that.postTitle != null) return false;
        if (postExcerpt != null ? !postExcerpt.equals(that.postExcerpt) : that.postExcerpt != null) return false;
        if (postStatus != null ? !postStatus.equals(that.postStatus) : that.postStatus != null) return false;
        if (commentStatus != null ? !commentStatus.equals(that.commentStatus) : that.commentStatus != null)
            return false;
        if (pingStatus != null ? !pingStatus.equals(that.pingStatus) : that.pingStatus != null) return false;
        if (postPassword != null ? !postPassword.equals(that.postPassword) : that.postPassword != null) return false;
        if (postName != null ? !postName.equals(that.postName) : that.postName != null) return false;
        if (toPing != null ? !toPing.equals(that.toPing) : that.toPing != null) return false;
        if (pinged != null ? !pinged.equals(that.pinged) : that.pinged != null) return false;
        if (postModified != null ? !postModified.equals(that.postModified) : that.postModified != null) return false;
        if (postModifiedGmt != null ? !postModifiedGmt.equals(that.postModifiedGmt) : that.postModifiedGmt != null)
            return false;
        if (postContentFiltered != null ? !postContentFiltered.equals(that.postContentFiltered) : that.postContentFiltered != null)
            return false;
        if (guid != null ? !guid.equals(that.guid) : that.guid != null) return false;
        if (postType != null ? !postType.equals(that.postType) : that.postType != null) return false;
        if (postMimeType != null ? !postMimeType.equals(that.postMimeType) : that.postMimeType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (postAuthor ^ (postAuthor >>> 32));
        result = 31 * result + (postDate != null ? postDate.hashCode() : 0);
        result = 31 * result + (postDateGmt != null ? postDateGmt.hashCode() : 0);
        result = 31 * result + (postContent != null ? postContent.hashCode() : 0);
        result = 31 * result + (postTitle != null ? postTitle.hashCode() : 0);
        result = 31 * result + (postExcerpt != null ? postExcerpt.hashCode() : 0);
        result = 31 * result + (postStatus != null ? postStatus.hashCode() : 0);
        result = 31 * result + (commentStatus != null ? commentStatus.hashCode() : 0);
        result = 31 * result + (pingStatus != null ? pingStatus.hashCode() : 0);
        result = 31 * result + (postPassword != null ? postPassword.hashCode() : 0);
        result = 31 * result + (postName != null ? postName.hashCode() : 0);
        result = 31 * result + (toPing != null ? toPing.hashCode() : 0);
        result = 31 * result + (pinged != null ? pinged.hashCode() : 0);
        result = 31 * result + (postModified != null ? postModified.hashCode() : 0);
        result = 31 * result + (postModifiedGmt != null ? postModifiedGmt.hashCode() : 0);
        result = 31 * result + (postContentFiltered != null ? postContentFiltered.hashCode() : 0);
        result = 31 * result + (int) (postParent ^ (postParent >>> 32));
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + menuOrder;
        result = 31 * result + (postType != null ? postType.hashCode() : 0);
        result = 31 * result + (postMimeType != null ? postMimeType.hashCode() : 0);
        result = 31 * result + (int) (commentCount ^ (commentCount >>> 32));
        return result;
    }
}
