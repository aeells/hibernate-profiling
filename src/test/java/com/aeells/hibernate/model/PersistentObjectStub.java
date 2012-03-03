/**
 * Copyright (c) 2012 Andrew Eells
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.aeells.hibernate.model;

import com.aeells.hibernate.profiling.HibernateProfiled;

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