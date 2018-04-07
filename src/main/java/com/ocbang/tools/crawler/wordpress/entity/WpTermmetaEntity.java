package com.ocbang.tools.crawler.wordpress.entity;

import javax.persistence.*;

@Entity
@Table(name = "wp_termmeta", schema = "wordpress", catalog = "")
public class WpTermmetaEntity {
    private long metaId;
    private long termId;
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
    @Column(name = "term_id")
    public long getTermId() {
        return termId;
    }

    public void setTermId(long termId) {
        this.termId = termId;
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

        WpTermmetaEntity that = (WpTermmetaEntity) o;

        if (metaId != that.metaId) return false;
        if (termId != that.termId) return false;
        if (metaKey != null ? !metaKey.equals(that.metaKey) : that.metaKey != null) return false;
        if (metaValue != null ? !metaValue.equals(that.metaValue) : that.metaValue != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (metaId ^ (metaId >>> 32));
        result = 31 * result + (int) (termId ^ (termId >>> 32));
        result = 31 * result + (metaKey != null ? metaKey.hashCode() : 0);
        result = 31 * result + (metaValue != null ? metaValue.hashCode() : 0);
        return result;
    }
}
