package com.andrew_eells.persistence.infrastructure.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Entity
public class Parent extends AbstractPersistentObject
{
    private static final String[] EXCLUDED_FIELDS = {"children"};

    @OneToMany(mappedBy = "parent", fetch = LAZY, cascade = {ALL})
    private List<Child> children = new ArrayList<Child>();

    public Parent()
    {
    }

    public Parent(final int version)
    {
        super.setVersion(version);
    }

    public void addChild(final Child child)
    {
        this.children.add(child);
    }

    public List<Child> getChildren()
    {
        return Collections.unmodifiableList(this.children);
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this, EXCLUDED_FIELDS);
    }

    @Override
    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj, EXCLUDED_FIELDS);
    }

    @Override
    public String toString()
    {
        final ReflectionToStringBuilder builder = new ReflectionToStringBuilder(this);
        builder.setExcludeFieldNames(EXCLUDED_FIELDS);
        return builder.toString();
    }
}