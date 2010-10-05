// Copyright (c) 2010, www.andrew-eells.com. All rights reserved.

package com.andrew_eells.persistence.infrastructure.query;

/**
 * Represents intended query sort key field and order information.
 */
public final class SortKeyInfo {

    /**
     * Represents query sort key order.
     */
    public enum Order {

        /**
         * Ascending order.
         */
        ASC,

        /**
         * Descending order.
         */
        DESC,

        /**
         * No explicit order so default database sort (or natural) order will
         * be used.
         */
        NONE
    }

    /**
     * Sort key field.
     */
    private final String field;

    /**
     * Sort key order.
     */
    private final Order order;

    /**
     * Convenience factory method for ascending sort key creation.
     *
     * @param field Field name.
     * @return Ascending order sort key info.
     */
    public static SortKeyInfo ascending(final String field) {
        return new SortKeyInfo(field, Order.ASC);
    }

    /**
     * Convenience factory method for descending sort key creation.
     *
     * @param field Field name.
     * @return Descending order sort key info.
     */
    public static SortKeyInfo descending(final String field) {
        return new SortKeyInfo(field, Order.DESC);
    }

    /**
     * Convenience factory method for default sort order key creation.
     *
     * @return Sort key info with default sort order.
     */
    public static SortKeyInfo none() {
        return new SortKeyInfo(null, Order.NONE);
    }

    /**
     * Constructor.
     *
     * @param field Field name.
     * @param order Sort key order.
     */
    SortKeyInfo(final String field, final Order order) {
        this.field = field;
        this.order = order;
    }

    /**
     * Returns this sort key field name.
     * @return Field name.
     */
    public String getField() {
        return field;
    }

    /**
     * Returns this sort key order information.
     * @return Sort key order.
     */
    public Order getOrder() {
        return order;
    }
}
