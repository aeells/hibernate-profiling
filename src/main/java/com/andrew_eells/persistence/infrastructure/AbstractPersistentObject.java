package com.andrew_eells.persistence.infrastructure;

import com.andrew_eells.persistence.infrastructure.query.Queryable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * AbstractPersistentObject is the abstract base class for domain model. It provides a all objects with an identifier, a verison to support optimistic locking, a date when the
 * object was created and one when it was last updated.
 * <p/>
 * Last update date is intended to rely on a database trigger - there is no code to actually set it correctly on the Java layer.
 * <p/>
 * NOTE: The majority of the methods within this class should be final.  Please see package level comments for a description why.
 */
@MappedSuperclass
public abstract class AbstractPersistentObject implements PersistenceStrategy {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Queryable("id")
    private String id;

    // int (instead of Integer) would be not nullable anyway, set by Hibernate using reflection

    @Version
    @Column(nullable = false)
    @SuppressWarnings({"UnusedDeclaration"})
    private int version;

    @Column(nullable = false)
    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    private DateTime created = new DateTime();

    @Column(nullable = false, name = "LAST_MODIFIED")
    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    private DateTime lastModified = new DateTime();

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(final DateTime created) {
        this.created = created;
    }

    public DateTime getLastModified() {
        return lastModified;
    }

    @Override
    public void setLastModified(final DateTime lastModified) {
        this.lastModified = lastModified;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }
}