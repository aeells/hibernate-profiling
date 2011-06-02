package com.qmetric.hibernate.infrastructure.query;

import org.junit.Test;

import java.util.ArrayList;

public class QuerySpecificationImplTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNullQueryParameters() {
        new QuerySpecificationImpl(null, new ArrayList<QueryClause>(), QuerySpecificationOperator.AND);
    }
}
