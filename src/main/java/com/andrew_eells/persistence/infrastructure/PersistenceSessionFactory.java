// Copyright (c) 2010, www.andrew-eells.com. All rights reserved.

package com.andrew_eells.persistence.infrastructure;

import org.hibernate.Session;

/**
 * Wrapper around hibernate <code>SessionFactoryUtils</code> to defeat static cling and enable testing.
 *
 * http://googletesting.blogspot.com/2008/06/defeat-static-cling.html
 */
public interface PersistenceSessionFactory
{
    Session getSession();
}