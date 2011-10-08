package com.qmetric.hibernate.model;

import com.qmetric.hibernate.PersistenceStrategy;

public class PersistentObjectStub extends AbstractPersistentObject
{
    public boolean create;

    public boolean update;

    public boolean delete;

    private PersistenceStrategy foreignKeyRef;

    private String reference;

    public PersistentObjectStub()
    {
    }

    public PersistentObjectStub(final PersistenceStrategy foreignKeyRef)
    {
        this.foreignKeyRef = foreignKeyRef;
    }

    public PersistentObjectStub(final String reference)
    {
        this.reference = reference;
    }

    @Override
    public boolean isCreatable()
    {
        return create;
    }

    @Override
    public boolean isUpdateable()
    {
        return update;
    }

    @Override
    public boolean isDeletable()
    {
        return delete;
    }

    public PersistenceStrategy getForeignKeyRef()
    {
        return foreignKeyRef;
    }

    public String getReference()
    {
        return reference;
    }
}