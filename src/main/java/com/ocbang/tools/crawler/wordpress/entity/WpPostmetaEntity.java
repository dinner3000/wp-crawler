package com.ocbang.tools.crawler.wordpress.entity;

import javax.persistence.*;

@Entity
@Table(name = "wp_postmeta", schema = "wordpress", catalog = "")
public class WpPostmetaEntity {
    private long metaId;
    private long postId;
    private String metaKey;
    private String metaValue;

    @Id
    @Column(name = "meta_id")
    public long getMetaId() {
        return metaId;
    }

    public void setMetaId(long metaId) {
        this.metaId = metaId;
    }

    @Basic
    @Column(name = "post_id")
    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    @Basic
    @Column(name = "meta_key")
    public String getMetaKey() {
        return metaKey;
    }

    public void setMetaKey(String metaKey) {
        this.metaKey = metaKey;
    }

    @Basic
    @Column(name = "meta_value")
    public String getMetaValue() {
        return metaValue;
    }

    public void setMetaValue(String metaValue) {
        this.metaValue = metaValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WpPostmetaEntity that = (WpPostmetaEntity) o;

        if (metaId != that.metaId) return false;
        if (postId != that.postId) return false;
        if (metaKey != null ? !metaKey.equals(that.metaKey) : that.metaKey != null) return false;
        if (metaValue != null ? !metaValue.equals(that.metaValue) : that.metaValue != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (metaId ^ (metaId >>> 32));
        result = 31 * result + (int) (postId ^ (postId >>> 32));
        result = 31 * result + (metaKey != null ? metaKey.hashCode() : 0);
        result = 31 * result + (metaValue != null ? metaValue.hashCode() : 0);
        return result;
    }
}
