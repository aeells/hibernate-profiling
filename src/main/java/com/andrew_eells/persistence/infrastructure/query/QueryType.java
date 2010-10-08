// Copyright (c) 2010, www.andrew-eells.com. All rights reserved.

package com.andrew_eells.persistence.infrastructure.query;

/**
 * Query types.
 */
public final class QueryType
{
    /**
     * Indicates primary key query.
     */
    public static final String PRIMARY_KEY = "id";

    /**
     * Indicates foreign key query.
     */
//    public static final String FOREIGN_KEY = "";

    /**
     * Indicates query based on customer id.
     */
    public static final String CUSTOMER_ID = "customerId";

    /**
     * Indicates query based on tracking id.
     */
    public static final String TRACKING_ID = "trackingId";

    /**
     * Indicates query based on e-mail address.
     */
    public static final String EMAIL_ADDRESS = "email";

    /**
     * Indicates query based on customer first name.
     */
    public static final String FIRST_NAME = "firstName";

    /**
     * Indicates query based on customer last name.
     */
    public static final String LAST_NAME = "lastName";

    /**
     * Indicates query based on customer phone number.
     */
    public static final String PHONE_NUMBER_PRIMARY = "phoneNumberPrimary";

    /**
     * Indicates query based on customer phone number 2.
     */
    public static final String PHONE_NUMBER_SECONDARY = "phoneNumberSecondary";
}