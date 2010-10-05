// Copyright (c) 2010, www.andrew-eells.com. All rights reserved.

package com.andrew_eells.persistence.infrastructure;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("persistenceSessionFactory") @Transactional
public final class PersistenceSessionFactoryImpl implements PersistenceSessionFactory
{
    private final SessionFactory sessionFactory;

    @Autowired
    public PersistenceSessionFactoryImpl(final SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    @Override public Session getSession()
    {
        return SessionFactoryUtils.getSession(sessionFactory, false);
    }
}