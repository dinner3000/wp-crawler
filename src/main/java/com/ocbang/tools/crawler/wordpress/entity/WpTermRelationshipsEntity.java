package com.ocbang.tools.crawler.wordpress.entity;

import javax.persistence.*;

@Entity
@Table(name = "wp_term_relationships", schema = "wordpress", catalog = "")
@IdClass(WpTermRelationshipsEntityPK.class)
public class WpTermRelationshipsEntity {
    private long objectId;
    private long termTaxonomyId;
    private int termOrder;

    @Id
    @Column(name = "object_id")
    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    @Id
    @Column(name = "term_taxonomy_id")
    public long getTermTaxonomyId() {
        return termTaxonomyId;
    }

    public void setTermTaxonomyId(long termTaxonomyId) {
        this.termTaxonomyId = termTaxonomyId;
    }

    @Basic
    @Column(name = "term_order")
    public int getTermOrder() {
        return termOrder;
    }

    public void setTermOrder(int termOrder) {
        this.termOrder = termOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WpTermRelationshipsEntity that = (WpTermRelationshipsEntity) o;

        if (objectId != that.objectId) return false;
        if (termTaxonomyId != that.termTaxonomyId) return false;
        if (termOrder != that.termOrder) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (objectId ^ (objectId >>> 32));
        result = 31 * result + (int) (termTaxonomyId ^ (termTaxonomyId >>> 32));
        result = 31 * result + termOrder;
        return result;
    }
}
