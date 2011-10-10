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

    void addCriterion(final SimpleExpression expression)
    {
        this.criteria.add(expression);
    }

    void addOrder(final Order order)
    {
        this.criteria.addOrder(order);
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

        public Builder sort()
        {
            query.addOrder(null);
            return this;
        }

        public HibernateQueryWrapper build()
        {
            this.validateCriteriaFields();
            return query;
        }

        // does not validate isAssignable from PersistenceStrategy as compile-time generics present
        public void validateCriteriaFields()
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