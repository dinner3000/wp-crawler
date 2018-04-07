package com.ocbang.tools.crawler.wordpress.entity;

import javax.persistence.*;

@Entity
@Table(name = "wp_terms", schema = "wordpress", catalog = "")
public class WpTermsEntity {
    private long termId;
    private String name;
    private String slug;
    private long termGroup;

    @Id
    @Column(name = "term_id")
    public long getTermId() {
        return termId;
    }

    public void setTermId(long termId) {
        this.termId = termId;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "slug")
    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Basic
    @Column(name = "term_group")
    public long getTermGroup() {
        return termGroup;
    }

    public void setTermGroup(long termGroup) {
        this.termGroup = termGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WpTermsEntity that = (WpTermsEntity) o;

        if (termId != that.termId) return false;
        if (termGroup != that.termGroup) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (slug != null ? !slug.equals(that.slug) : that.slug != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (termId ^ (termId >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (slug != null ? slug.hashCode() : 0);
        result = 31 * result + (int) (termGroup ^ (termGroup >>> 32));
        return result;
    }
}
