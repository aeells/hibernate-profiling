// Copyright (c) 2011, QMetric Group Limited. All rights reserved.

package com.qmetric.hibernate;

import org.apache.commons.lang.Validate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class HibernateQueryWrapper
{
    private final Class<? extends PersistenceStrategy> daoClass;

    private DetachedCriteria criteria;

    private QueryLimit limit = QueryLimit.NULL_IMPL;

    private HibernateQueryWrapper(final Class<? extends PersistenceStrategy> daoClass)
    {
        this.daoClass = daoClass;
        this.criteria = DetachedCriteria.forClass(daoClass);
    }

    public Class<? extends PersistenceStrategy> getDaoClass()
    {
        return daoClass;
    }

    public DetachedCriteria getCriteria()
    {
        return criteria;
    }

    public QueryLimit getLimit()
    {
        return limit;
    }

    void addCriterion(final SimpleExpression expression)
    {
        this.criteria.add(expression);
    }

    void addOrder(final Order order)
    {
        this.criteria.addOrder(order);
    }

    void addLimit(final int firstResult, final int maxResults)
    {
        this.limit = new QueryLimit(firstResult, maxResults);
    }

    public static final class Builder
    {
        private final HibernateQueryWrapper query;

        private final Class daoClass;

        // not possible to validate once criteria object built so maintain separate state
        private List<String> fieldNames = new ArrayList<String>();

        public Builder(final Class<? extends PersistenceStrategy> daoClass)
        {
            this.daoClass = daoClass;
            this.query = new HibernateQueryWrapper(daoClass);
        }

        public static Builder queryFor(final Class<? extends PersistenceStrategy> daoClass)
        {
            Validate.notNull(daoClass);

            return new Builder(daoClass);
        }

        public Builder withPrimaryKey(final String fieldName, final String fieldValue)
        {
            return with(fieldName, fieldValue);
        }

        public Builder withForeignKey(final String fieldName, final Object fieldValue)
        {
            return with(fieldName, fieldValue);
        }

        public Builder withField(final String fieldName, final Object fieldValue)
        {
            return with(fieldName, fieldValue);
        }

        private Builder with(final String fieldName, final Object fieldValue)
        {
            this.fieldNames.add(fieldName);
            query.addCriterion(Restrictions.eq(fieldName, fieldValue));
            return this;
        }

        public Builder sortAsc(final String fieldName)
        {
            query.addOrder(Order.asc(fieldName));
            return this;
        }

        public Builder sortDesc(final String fieldName)
        {
            query.addOrder(Order.desc(fieldName));
            return this;
        }

        public Builder limit(final int firstResult, final int maxResults)
        {
            query.addLimit(firstResult, maxResults);
            return this;
        }

        public HibernateQueryWrapper build()
        {
            this.validateCriteriaFields();
            return query;
        }

        // does not validate isAssignable from PersistenceStrategy as compile-time generics present
        private void validateCriteriaFields()
        {
            Validate.isTrue(getAllFields(new ArrayList<String>(), daoClass).containsAll(fieldNames));
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
    }
}