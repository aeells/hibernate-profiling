package com.andrew_eells.persistence.infrastructure;

import com.andrew_eells.persistence.infrastructure.model.Child;
import com.andrew_eells.persistence.infrastructure.model.Parent;
import com.andrew_eells.persistence.infrastructure.query.QueryClause;
import com.andrew_eells.persistence.infrastructure.query.QueryClauseOperator;
import com.andrew_eells.persistence.infrastructure.query.QuerySpecificationImpl;
import com.andrew_eells.persistence.service.PersistenceService;
import com.qmetric.hamcrest.matchers.CollectionMatcher;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class) @ContextConfiguration(locations = {"classpath:spring-test-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true) @Transactional
    public class AbstractPersistentObjectImplITest
{
    @Autowired
    private PersistenceService service;

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    public void shouldSaveEntity()
    {
        Parent parent = new Parent(1);

        persistAndEvict(parent);

        final Parent loadedParent = (Parent) service.readUnique(new QuerySpecificationImpl(Parent.class, new QueryClause("id", parent.getId(), QueryClauseOperator.EQ)));

        assertThat(parent.getId(), notNullValue());
        assertThat(loadedParent, equalTo(parent));
    }

    @Test
    public void shouldSaveAssociatedEntity()
    {
        Parent parent = createFullGraphPersistAndEvict();

        final Parent loadedParent = (Parent) service.readUnique(new QuerySpecificationImpl(Parent.class, new QueryClause("id", parent.getId(), QueryClauseOperator.EQ)));

        fullyAssertObjectGraph(parent, loadedParent);
    }

    @Test
    public void shouldPersistEntityAndThenAddAssociateObject()
    {
        Parent parent = new Parent(1);

        persistAndEvict(parent);

        Child child = new Child(1, parent);

        parent.addChild(child);

        persistAndEvict(parent);

        final Parent loadedParent = (Parent) service.readUnique(new QuerySpecificationImpl(Parent.class, new QueryClause("id", parent.getId(), QueryClauseOperator.EQ)));

        fullyAssertObjectGraph(parent, loadedParent);
    }

    private void fullyAssertObjectGraph(final Parent parent, final Parent loadedParent)
    {
        assertThat(parent.getId(), notNullValue());

        assertThat(parent.getChildren().get(0), notNullValue());

        assertThat(parent.getChildren().get(0).getId(), notNullValue());

        assertThat(loadedParent.getChildren(), CollectionMatcher.containsOnly(parent.getChildren()));
    }

    private Parent createFullGraphPersistAndEvict()
    {
        Parent parent = new Parent(1);
        Child child = new Child(1, parent);

        parent.addChild(child);

        persistAndEvict(parent);

        return parent;
    }

    private void persistAndEvict(final Parent parent)
    {
        service.create(parent);

        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().evict(parent);
    }
}
