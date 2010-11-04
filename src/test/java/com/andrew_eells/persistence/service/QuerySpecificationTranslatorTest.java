package com.andrew_eells.persistence.service;

import com.andrew_eells.persistence.infrastructure.model.AbstractPersistentObject;
import com.andrew_eells.persistence.infrastructure.query.*;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class QuerySpecificationTranslatorTest {
    private QuerySpecificationTranslator translator;

    @Before
    public void context() {
        translator = new QuerySpecificationTranslator();
    }

    @Test
    public void test() {

        QuerySpecification querySpecification = new QuerySpecificationImpl(PersistentObjectStub.class, new QueryClause(QueryType.CUSTOMER_ID, "12345678", QueryClauseOperator.EQ));
        DetachedCriteria criteria = translator.translate(querySpecification);

        DetachedCriteria expected = DetachedCriteria.forEntityName(PersistentObjectStub.class.getName());
        SimpleExpression simpleExpression = Restrictions.eq(QueryType.CUSTOMER_ID, "12345678");
        expected.add(simpleExpression);

        assertThat(criteria, equalTo(expected)); 
    }

    class PersistentObjectStub extends AbstractPersistentObject {
        @Queryable(value = QueryType.CUSTOMER_ID, isCaseSensitive = true)
        public String customerId;

        @Queryable(value = QueryType.EMAIL_ADDRESS, isCaseSensitive = false)
        public String email;

        public boolean create;

        public boolean update;

        public boolean delete;

    }
}
