// Copyright (c) 2010, www.andrew-eells.com. All rights reserved.

package com.andrew_eells.persistence.infrastructure.query;

import com.andrew_eells.persistence.infrastructure.PersistenceStrategy;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Query types.
 */
public enum QueryType
{
    /**
     * Indicates primary key query.
     */
    PRIMARY_KEY,

    /**
     * Indicates foreign key query.
     */
    FOREIGN_KEY,

    /**
     * Indicates query based on customer id.
     */
    CUSTOMER_ID,

    /**
     * Indicates query based on tracking id.
     */
    TRACKING_ID,

    /**
     * Indicates query based on e-mail address.
     */
    EMAIL_ADDRESS,

    /**
     * Indicates query based on customer first name.
     */
    FIRST_NAME,

    /**
     * Indicates query based on customer last name.
     */
    LAST_NAME,

    /**
     * Indicates query based on customer phone number.
     */
    PHONE_NUMBER_PRIMARY,

    /**
     * Indicates query based on customer phone number 2.
     */
    PHONE_NUMBER_SECONDARY;

    /**
     * Identifies field name of class to be queried on.
     *
     * @param persistentClass Class of the object to be queried for.
     * @param type Query type.
     * @return Field name.
     */
    public static QueryKeyInfo spec(final Class<? extends PersistenceStrategy> persistentClass, final QueryType type)
    {
        final List<Field> fields = getAllFields(new ArrayList<Field>(), persistentClass);
        for (final Field field : fields)
        {
            if (field.isAnnotationPresent(Queryable.class))
            {
                if (field.getAnnotation(Queryable.class).value() == type)
                {
                    final boolean caseSensitive = field.getAnnotation(Queryable.class).isCaseSensitive();
                    return new QueryKeyInfo(field.getName(), caseSensitive);
                }
            }
        }

        throw new IllegalStateException("unable to query class " + persistentClass.getName() + " by type " + type.name());
    }

    private static List<Field> getAllFields(List<Field> fields, final Class<?> type)
    {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        // recurse the AbstractPersistentObjectImpl fields incase this is a PRIMARY_KEY query
        if (type.getSuperclass() != null)
        {
            fields = getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }
}