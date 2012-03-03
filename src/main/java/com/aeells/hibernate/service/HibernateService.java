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
import org.hibernate.criterion.DetachedCriteria;

import java.util.List;

/**
 * Standardised database access specification.
 */
public interface HibernateService<T>
{
    void create(final Createable object);

    void update(final Updateable object);

    void delete(final Deleteable object);

    T findById(final Class daoClass, final String id);

    T findUnique(final DetachedCriteria criteria);

    T findFirstOrderedBy(final DetachedCriteria criteria);

    List<T> find(final DetachedCriteria criteria);

    List<T> find(final DetachedCriteria criteria, final int firstResult, final int maxResults);
}