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

package com.aeells.hibernate.service;

import com.aeells.hibernate.Createable;
import com.aeells.hibernate.Deleteable;
import com.aeells.hibernate.Updateable;
import org.apache.commons.lang.Validate;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.util.List;

import static org.springframework.dao.support.DataAccessUtils.uniqueResult;

/**
 * Standardised database repository access implementation.
 */
public final class HibernateServiceImpl<T> implements HibernateService
{
    private final HibernateTemplate hibernateTemplate;

    public HibernateServiceImpl(final HibernateTemplate hibernateTemplate)
    {
        this.hibernateTemplate = hibernateTemplate;
    }

    @Override public final void create(final Createable model)
    {
        if (model != null && model.isCreateAllowed())
        {
            hibernateTemplate.saveOrUpdate(model);
        }
    }

    @Override public final void update(final Updateable model)
    {
        if (model != null && model.isUpdateAllowed())
        {
            hibernateTemplate.saveOrUpdate(model);
        }
    }

    @Override public final void delete(final Deleteable model)
    {
        if (model != null && model.isDeleteAllowed())
        {
            hibernateTemplate.delete(model);
        }
    }

    public final T findById(final Class daoClass, final String id)
    {
        Validate.notNull(daoClass, "class cannot be null!");
        Validate.notEmpty(id, "id cannot be empty!");

        //noinspection unchecked
        return (T) hibernateTemplate.get(daoClass, id);
    }

    public final T findUnique(final DetachedCriteria criteria)
    {
        Validate.notNull(criteria, "criteria cannot be null!");

        //noinspection unchecked
        return uniqueResult((List<T>) hibernateTemplate.findByCriteria(criteria));
    }

    public final T findFirstOrderedBy(final DetachedCriteria criteria)
    {
        Validate.notNull(criteria, "criteria cannot be null!");

        //noinspection unchecked
        return uniqueResult((List<T>) hibernateTemplate.findByCriteria(criteria, 0, 1));
    }

    public final List<T> find(final DetachedCriteria criteria)
    {
        Validate.notNull(criteria, "criteria cannot be null!");

        //noinspection unchecked
        return (List<T>) hibernateTemplate.findByCriteria(criteria);
    }

    public final List<T> find(final DetachedCriteria criteria, final int firstResult, final int maxResults)
    {
        Validate.notNull(criteria, "criteria cannot be null!");

        //noinspection unchecked
        return (List<T>) hibernateTemplate.findByCriteria(criteria, firstResult, maxResults);
    }
}