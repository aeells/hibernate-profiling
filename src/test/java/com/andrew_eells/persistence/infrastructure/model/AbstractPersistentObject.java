package com.andrew_eells.persistence.infrastructure.model;

import com.andrew_eells.persistence.infrastructure.PersistenceStrategy;
import com.andrew_eells.persistence.infrastructure.query.Queryable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * A reference implementation of PersistenceStrategy.
 */
@MappedSuperclass
public abstract class AbstractPersistentObject implements PersistenceStrategy
{
    @Id @GeneratedValue(generator = "system-uuid") @GenericGenerator(name = "system-uuid", strategy = "uuid") @Queryable("id")
    private String id;

    @Version @Column(nullable = false) @SuppressWarnings({"UnusedDeclaration"})
    private int version;

    @Column(nullable = false) @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    private DateTime created = new DateTime();

    @Column(nullable = false, name = "LAST_MODIFIED") @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    private DateTime lastModified = new DateTime();

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public DateTime getCreated()
    {
        return created;
    }

    public void setCreated(final DateTime created)
    {
        this.created = created;
    }

    public DateTime getLastModified()
    {
        return lastModified;
    }

    @Override public boolean isCreatable()
    {
        return true;
    }

    @Override public boolean isUpdateable()
    {
        return true;
    }

    @Override public boolean isDeletable()
    {
        return true;
    }

    public void setLastModified(final DateTime lastModified)
    {
        this.lastModified = lastModified;
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion(final int version)
    {
        this.version = version;
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(final Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }
}