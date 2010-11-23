package com.andrew_eells.persistence.service;

import com.andrew_eells.persistence.infrastructure.model.Child;
import com.andrew_eells.persistence.infrastructure.model.Parent;
import com.andrew_eells.persistence.infrastructure.query.QueryClause;
import com.andrew_eells.persistence.infrastructure.query.QuerySpecificationImpl;
import com.andrew_eells.persistence.infrastructure.query.QueryTypeImpl;
import com.qmetric.hamcrest.matchers.CollectionMatcher;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.andrew_eells.persistence.infrastructure.query.QueryClauseOperator.EQ;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class) @ContextConfiguration(locations = {"classpath:spring-test-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true) @Transactional
public class PersistenceServiceITest
{
    @Autowired
    private PersistenceService<Parent> service;

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    public void shouldSaveEntity()
    {
        final Parent parent = new Parent(1);
        persistAndEvict(parent);

        final Parent loadedParent = service.readUnique(new QuerySpecificationImpl(Parent.class, new QueryClause(QueryTypeImpl.PK_QUERY, parent.getId(), EQ)));

        assertThat(parent.getId(), notNullValue());
        assertThat(loadedParent, equalTo(parent));
    }

    @Test
    public void shouldSaveAssociatedEntity()
    {
        final Parent parent = new Parent(1);
        parent.addChild(new Child(1, parent));
        persistAndEvict(parent);

        final Parent loadedParent = service.readUnique(new QuerySpecificationImpl(Parent.class, new QueryClause(QueryTypeImpl.PK_QUERY, parent.getId(), EQ)));

        fullyAssertObjectGraph(parent, loadedParent);
    }

    @Test
    public void shouldSaveAssociatedEntityUsingForeignKeyClause()
    {
        final Parent parent = new Parent(1);
        parent.addChild(new Child(1, parent));
        persistAndEvict(parent);

        final Parent loadedParent = service.readUnique(new QuerySpecificationImpl(Parent.class, new QueryClause(QueryTypeImpl.PK_QUERY, parent.getId(), EQ)));

        fullyAssertObjectGraph(parent, loadedParent);
    }

    @Test
    public void shouldPersistEntityAndThenAddAssociateObject()
    {
        final Parent parent = new Parent(1);
        persistAndEvict(parent);

        parent.addChild(new Child(1, parent));
        persistAndEvict(parent);

        final Parent loadedParent = service.readUnique(new QuerySpecificationImpl(Parent.class, new QueryClause(QueryTypeImpl.PK_QUERY, parent.getId(), EQ)));

        fullyAssertObjectGraph(parent, loadedParent);
    }

    private void fullyAssertObjectGraph(final Parent parent, final Parent loadedParent)
    {
        assertThat(parent.getId(), notNullValue());
        assertThat(parent.getChildren().get(0), notNullValue());
        assertThat(parent.getChildren().get(0).getId(), notNullValue());
        assertThat(loadedParent.getChildren(), CollectionMatcher.containsOnly(parent.getChildren()));
    }

    private void persistAndEvict(final Parent parent)
    {
        service.create(parent);

        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().evict(parent);
    }
}