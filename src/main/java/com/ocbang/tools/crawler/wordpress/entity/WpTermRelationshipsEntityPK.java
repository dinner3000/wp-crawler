package com.ocbang.tools.crawler.wordpress.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class WpTermRelationshipsEntityPK implements Serializable {
    private long objectId;
    private long termTaxonomyId;

    @Column(name = "object_id")
    @Id
    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    @Column(name = "term_taxonomy_id")
    @Id
    public long getTermTaxonomyId() {
        return termTaxonomyId;
    }

    public void setTermTaxonomyId(long termTaxonomyId) {
        this.termTaxonomyId = termTaxonomyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WpTermRelationshipsEntityPK that = (WpTermRelationshipsEntityPK) o;

        if (objectId != that.objectId) return false;
        if (termTaxonomyId != that.termTaxonomyId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (objectId ^ (objectId >>> 32));
        result = 31 * result + (int) (termTaxonomyId ^ (termTaxonomyId >>> 32));
        return result;
    }
}
