// Copyright (c) 2010, www.andrew-eells.com. All rights reserved.

package com.andrew_eells.persistence.infrastructure.query;

import com.andrew_eells.persistence.infrastructure.PersistenceStrategy;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Specification implementation to decouple requesting client from Hibernate implementation details.
 * <p/>
 * See the pattern spec pdf on Martin Fowler's {@link -link http://www.martinfowler.com/apsupp/spec.pdf site}.
 * <p/>
 * Note that this specification enforces client must provide <code>QueryType</code> and not <code>String</code> parameters to a query, which would
 * otherwise break object encapsulation. This is achieved via the <code>Queryable</code> annotation.
 */
public class QuerySpecificationImpl implements QuerySpecification
{
    private Map<QueryKeyInfo, Object> queryParams = new LinkedHashMap<QueryKeyInfo, Object>();

    private SortKeyInfo sortKey;

    private QuerySpecificationOperator queryOperator;

    private Class<? extends PersistenceStrategy> persistentClass;

    /**
     * Constructor.
     * <p/>
     * Use for querying with single field constraint with default ordering.
     *
     * @param persistentClass Class of the object to be queried for.
     * @param queryClause Query clause.
     */
    public QuerySpecificationImpl(final Class<? extends PersistenceStrategy> persistentClass, QueryClause queryClause)
    {
        this(persistentClass, Arrays.asList(queryClause), SortKeyInfo.none(), QuerySpecificationOperator.NONE);
    }

    /**
     * Constructor.
     * <p/>
     * Use for querying with single field constraint where a specific ordering is required.
     *
     * @param persistentClass Class of the object to be queried for.
     * @param queryClause Query clause.
     * @param sortKey the key to sort on.
     */
    public QuerySpecificationImpl(final Class<? extends PersistenceStrategy> persistentClass, QueryClause queryClause, final SortKeyInfo sortKey)
    {
        this(persistentClass, Arrays.asList(queryClause), sortKey, QuerySpecificationOperator.NONE);
    }

    /**
     * Constructor.
     * <p/>
     * Use for querying with multiple field constraints where no ordering is required.
     *
     * @param persistentClass Class of the object to be queried for.
     * @param queryClauses Query clauses.
     * @param queryOperator Query operator.
     */
    public QuerySpecificationImpl(final Class<? extends PersistenceStrategy> persistentClass, final List<QueryClause> queryClauses,
                                  final QuerySpecificationOperator queryOperator)
    {
        this(persistentClass, queryClauses, SortKeyInfo.none(), queryOperator);
    }

    /**
     * Constructor
     * <p/>
     * Use for querying with multiple field constraints where ordering is required.
     *
     * @param persistentClass Class of the object to be queried for.
     * @param queryClauses Query clauses
     * @param sortKey sortkey
     * @param queryOperator query specification operator
     */
    public QuerySpecificationImpl(final Class<? extends PersistenceStrategy> persistentClass, final List<QueryClause> queryClauses,
                                  final SortKeyInfo sortKey, QuerySpecificationOperator queryOperator)
    {
        // check we have a complete specification
        Validate.notEmpty(queryClauses, "query specification invalid on empty or null queryParams!");
        Validate.notNull(sortKey, "Query specification invalid on null sortKey");

        this.persistentClass = persistentClass;
        this.sortKey = sortKey;

        if (queryClauses.size() > 1)
        {
            // if we have more than one query clause and if so enforce a valid query specification operator
            Validate.isTrue(queryOperator != null && queryOperator != QuerySpecificationOperator.NONE,
                            "Query specification invalid on null queryOperator - queries with more than 1 parameter must have a OR/AND query operator");
        }
        else
        {
            // if we only have a single query clause then always set the query specification to none
            queryOperator = QuerySpecificationOperator.NONE;
        }

        this.queryOperator = queryOperator;

        // build query params
        for (final QueryClause queryClause : queryClauses)
        {
            this.queryParams.put(spec(persistentClass, queryClause), queryClause.getFieldValue());
        }
    }

    /**
     * Identifies field name of class to be queried on.
     *
     * @param persistentClass Class of the object to be queried for.
     * @param queryClause Query clause.
     * @return Field name.
     */
    private QueryKeyInfo spec(final Class<? extends PersistenceStrategy> persistentClass, final QueryClause queryClause)
    {
        final List<Field> fields = getAllFields(new ArrayList<Field>(), persistentClass);
        for (final Field field : fields)
        {
            if (field.isAnnotationPresent(Queryable.class))
            {
                final QueryableField fieldType = field.getAnnotation(Queryable.class).fieldType();
                final boolean caseSensitive = field.getAnnotation(Queryable.class).isCaseSensitive();
                if (fieldType.equals(QueryableField.FOREIGN_KEY) || field.getAnnotation(Queryable.class).value().equals(queryClause.getFieldName()))
                {
                    return new QueryKeyInfo(field.getName(), caseSensitive, queryClause.getOperator());
                }
            }
        }

        throw new IllegalStateException("unable to query class " + persistentClass.getName() + " by type " + queryClause.getFieldName());
    }

    private List<Field> getAllFields(List<Field> fields, final Class<?> type)
    {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        // recurse the AbstractPersistentObject fields incase this is a PRIMARY_KEY query
        if (type.getSuperclass() != null)
        {
            fields = getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    @Override
    public QuerySpecificationOperator getQueryOperator()
    {
        return queryOperator;
    }

    @Override
    public Map<QueryKeyInfo, Object> getQueryParams()
    {
        return queryParams;
    }

    @Override
    public Class<? extends PersistenceStrategy> getPersistentClass()
    {
        return persistentClass;
    }

    @Override
    public SortKeyInfo getSortKey()
    {
        return sortKey;
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(final Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }
}