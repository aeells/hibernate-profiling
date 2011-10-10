package com.qmetric.hibernate.model;

import com.qmetric.hibernate.PersistenceStrategy;
import com.qmetric.hibernate.profiling.PersistenceProfiled;

@PersistenceProfiled public class PersistentObjectStub extends AbstractPersistentObject
{
    public boolean create;

    public boolean update;

    public boolean delete;

    private PersistenceStrategy foreignKeyRef;

    private String fieldName;

    public PersistentObjectStub()
    {
    }

    public PersistentObjectStub(final PersistenceStrategy foreignKeyRef)
    {
        this.foreignKeyRef = foreignKeyRef;
    }

    public PersistentObjectStub(final String fieldName)
    {
        this.fieldName = fieldName;
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

    public String getFieldName()
    {
        return fieldName;
    }
}