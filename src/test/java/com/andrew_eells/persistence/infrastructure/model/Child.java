package com.andrew_eells.persistence.infrastructure.model;

import com.andrew_eells.persistence.infrastructure.AbstractPersistentObject;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Child extends AbstractPersistentObject
{
    @ManyToOne @JoinColumn(name = "PARENT_ID")
    private Parent parent;

    public Child() {}

    public Child(final int version, final Parent parent)
    {
        super.setVersion(version);
        this.parent = parent;
    }

    @Override public boolean isCreate()
    {
        return true;
    }

    @Override public boolean isUpdate()
    {
        return true;
    }

    @Override public boolean isDelete()
    {
        return true;
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }
}
