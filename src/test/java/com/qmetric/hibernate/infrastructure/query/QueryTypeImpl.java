package com.qmetric.hibernate.infrastructure.query;

public class QueryTypeImpl implements QueryType
{
    public static final String PK_ID = "id";

    public static final String CUSTOMER_ID = "customerId";

    public static final String EMAIL_ADDRESS = "email";

    /**
     * Indicates query based on primary key id.
     */
    public static final QueryType PK_QUERY = new QueryTypeImpl(PK_ID);

    /**
     * Indicates query based on customer id.
     */
    public static final QueryType CUSTOMER_QUERY = new QueryTypeImpl(CUSTOMER_ID);

    /**
     * Indicates query based on e-mail address.
     */
    public static final QueryType EMAIL_ADDRESS_QUERY = new QueryTypeImpl(EMAIL_ADDRESS);

    private String fieldName;

    private QueryTypeImpl(final String fieldName)
    {
        this.fieldName = fieldName;
    }

    public String getFieldName()
    {
        return fieldName;
    }
}