package com.qmetric.hibernate.model;

import com.qmetric.hibernate.PersistenceStrategy;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;

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
    @Id @GeneratedValue(generator = "system-uuid") @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @Version @Column(nullable = false) @SuppressWarnings({"UnusedDeclaration"})
    private int version;

    public String getId()
    {
        return id;
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