package com.ocbang.tools.crawler.wordpress.entity;

import javax.persistence.*;

@Entity
@Table(name = "wp_term_taxonomy", schema = "wordpress", catalog = "")
public class WpTermTaxonomyEntity {
    private long termTaxonomyId;
    private long termId;
    private String taxonomy;
    private String description;
    private long parent;
    private long count;

    @Id
    @Column(name = "term_taxonomy_id")
    public long getTermTaxonomyId() {
        return termTaxonomyId;
    }

    public void setTermTaxonomyId(long termTaxonomyId) {
        this.termTaxonomyId = termTaxonomyId;
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
    @Column(name = "taxonomy")
    public String getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(String taxonomy) {
        this.taxonomy = taxonomy;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "parent")
    public long getParent() {
        return parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }

    @Basic
    @Column(name = "count")
    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WpTermTaxonomyEntity that = (WpTermTaxonomyEntity) o;

        if (termTaxonomyId != that.termTaxonomyId) return false;
        if (termId != that.termId) return false;
        if (parent != that.parent) return false;
        if (count != that.count) return false;
        if (taxonomy != null ? !taxonomy.equals(that.taxonomy) : that.taxonomy != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (termTaxonomyId ^ (termTaxonomyId >>> 32));
        result = 31 * result + (int) (termId ^ (termId >>> 32));
        result = 31 * result + (taxonomy != null ? taxonomy.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) (parent ^ (parent >>> 32));
        result = 31 * result + (int) (count ^ (count >>> 32));
        return result;
    }
}
