// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate.service;

import com.qmetric.hibernate.PersistenceStrategy;
import org.apache.commons.lang.Validate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Standardised database repository access implementation.
 */
public class PersistenceServiceImpl implements PersistenceService<PersistenceStrategy>
{
    private final HibernateTemplate hibernateTemplate;

    public PersistenceServiceImpl(final HibernateTemplate hibernateTemplate)
    {
        this.hibernateTemplate = hibernateTemplate;
    }

    @Override public final void create(final PersistenceStrategy model)
    {
        if (model != null && model.isCreatable())
        {
            saveOrUpdate(model);
        }
    }

    @Override public final void update(final PersistenceStrategy model)
    {
        if (model != null && model.isUpdateable())
        {
            saveOrUpdate(model);
        }
    }

    @Override public final void delete(final PersistenceStrategy model)
    {
        if (model != null && model.isDeletable())
        {
            hibernateTemplate.delete(model);
        }
    }

    @Override public void flush()
    {
        hibernateTemplate.flush();
    }

    public PersistenceStrategy findByPrimaryKey(final Class<? extends PersistenceStrategy> daoClass, final String pkFieldName, final String pk)
    {
        validateDaoFieldAccess(daoClass, pkFieldName);

        final DetachedCriteria criteria = DetachedCriteria.forClass(daoClass).add(Restrictions.eq(pkFieldName, pk));
        //noinspection unchecked
        return DataAccessUtils.uniqueResult((List<PersistenceStrategy>) hibernateTemplate.findByCriteria(criteria));
    }

    public List<PersistenceStrategy> findByForeignKey(final Class<? extends PersistenceStrategy> daoClass, final String fkFieldName, final PersistenceStrategy fk)
    {
        validateDaoFieldAccess(daoClass, fkFieldName);

        final DetachedCriteria criteria = DetachedCriteria.forClass(daoClass).add(Restrictions.eq(fkFieldName, fk));
        //noinspection unchecked
        return (List<PersistenceStrategy>) hibernateTemplate.findByCriteria(criteria);
    }

    public final PersistenceStrategy findUnique(final Class<? extends PersistenceStrategy> daoClass, final Criterion... criterion)
    {
        validateDaoClassAccess(daoClass);

        //noinspection unchecked
        return DataAccessUtils.uniqueResult(findCollection(daoClass, criterion));
    }

    public final List<PersistenceStrategy> findCollection(final Class<? extends PersistenceStrategy> daoClass, final Criterion... criterion)
    {
        validateDaoClassAccess(daoClass);

        final DetachedCriteria dc = addCriteria(daoClass, criterion);

        //noinspection unchecked
        return (List<PersistenceStrategy>) hibernateTemplate.findByCriteria(dc);
    }

    private DetachedCriteria addCriteria(final Class<? extends PersistenceStrategy> daoClass, final Criterion... criterion)
    {
        DetachedCriteria dc = DetachedCriteria.forClass(daoClass);

        for (final Criterion c : criterion)
        {
            dc = dc.add(c);
        }

        return dc;
    }

    private void validateDaoFieldAccess(final Class clazz, final String... fieldName)
    {
        validateDaoClassAccess(clazz);

        Validate.isTrue(getAllFields(new ArrayList<String>(), clazz).containsAll(Arrays.asList(fieldName)));
    }

    private void validateDaoClassAccess(final Class clazz)
    {
        Validate.notNull(clazz);
    }

    private List<String> getAllFields(List<String> fields, final Class clazz)
    {
        fields.addAll(getFieldNamesFrom(Arrays.asList(clazz.getDeclaredFields())));

        if (clazz.getSuperclass() != null)
        {
            fields = getAllFields(fields, clazz.getSuperclass());
        }

        return fields;
    }

    private List<String> getFieldNamesFrom(final List<Field> fields)
    {
        final List<String> fieldNames = new ArrayList<String>();
        for (final Field field : fields)
        {
            fieldNames.add(field.getName());
        }

        return fieldNames;
    }

    private void saveOrUpdate(final PersistenceStrategy model)
    {
        if (hibernateTemplate.contains(model))
        {
            hibernateTemplate.merge(model);
        }
        else
        {
            hibernateTemplate.saveOrUpdate(model);
        }
    }
}