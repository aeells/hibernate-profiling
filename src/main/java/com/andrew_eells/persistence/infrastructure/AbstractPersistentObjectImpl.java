// Copyright (c) 2007, Xbridge Ltd. All Rights Reserved.
package com.andrew_eells.persistence.infrastructure;

import com.andrew_eells.persistence.infrastructure.query.Queryable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import java.util.Date;
import java.util.UUID;

/**
 * AbstractPersistentObjectImpl is the abstract base class for domain model. It provides a all objects with an identifier, a verison to support optimistic
 * locking, a date when the object was created and one when it was last updated.
 * <p/>
 * Last update date is intended to rely on a database trigger - there is no code to actually set it correctly on the Java layer.
 * <p/>
 * NOTE: The majority of the methods within this class should be final.  Please see package level comments for a description why.
 */
@MappedSuperclass
public abstract class AbstractPersistentObjectImpl implements PersistenceStrategy
{
    // will be overriden with data read from database on reconstitution
    @Id
    @Queryable("id")
    private String id = UUID.randomUUID().toString();

    // int (instead of Integer) would be not nullable anyway, set by Hibernate using reflection
    @Version @Column(nullable = false) @SuppressWarnings({"UnusedDeclaration"})
    private int version;

    // will be overrriden with data read from database
    @Column(nullable = false)
    private Date created = new Date();

    // will be overrriden with data read from database
    @Column(nullable = false, name = "LAST_MODIFIED")
    private Date lastModified = new Date();

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(final Date created)
    {
        this.created = created;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    @Override public void setLastModified(final Date lastModified)
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

    /**
     * Concatenate id and fully qualified classname. If used in AbstractPersistentObjectImpl.equals() String.equals should return immediately if ids are
     * not the same.
     *
     * @return Unique object identifier.
     */
    private String getInternalId()
    {
        return id + ":" + getClass().getName();
    }

    @Override public boolean equals(final Object o)
    {
        return this == o ||
               ((o instanceof AbstractPersistentObjectImpl) && ((id != null) && getInternalId().equals(((AbstractPersistentObjectImpl) o).getInternalId())));
    }

    /**
     * we are not using getInternalId() (but id itself) as this is still fulfilling the Object.hashCode() contract.
     * NOTE: Method should be 'final', see package level comments.
     */
    @Override public int hashCode()
    {
        return (id != null) ? id.hashCode() : super.hashCode();
    }

    @Override public String toString()
    {
        return getClass().getName() + "[id=" + id + "]";
    }
}