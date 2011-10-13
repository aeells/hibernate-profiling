package com.qmetric.hibernate.model;

import com.qmetric.hibernate.profiling.HibernateProfiled;

@HibernateProfiled public class PersistentObjectStub extends AbstractPersistentObject
{
    public boolean create;

    public boolean update;

    public boolean delete;

    private PersistentObjectStub foreignKeyRef;

    private String fieldName;

    public PersistentObjectStub()
    {
    }

    public PersistentObjectStub(final PersistentObjectStub foreignKeyRef)
    {
        this.foreignKeyRef = foreignKeyRef;
    }

    public PersistentObjectStub(final String fieldName)
    {
        this.fieldName = fieldName;
    }

    @Override
    public boolean isCreateAllowed()
    {
        return create;
    }

    @Override
    public boolean isUpdateAllowed()
    {
        return update;
    }

    @Override
    public boolean isDeleteAllowed()
    {
        return delete;
    }

    public PersistentObjectStub getForeignKeyRef()
    {
        return foreignKeyRef;
    }

    public String getFieldName()
    {
        return fieldName;
    }
}