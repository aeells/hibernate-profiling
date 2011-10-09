package com.qmetric.hibernate.service;

import com.qmetric.hibernate.model.Child;
import com.qmetric.hibernate.model.Parent;
import com.qmetric.testing.hamcrest.matchers.CollectionMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.qmetric.hibernate.HibernateQueryWrapper.Builder.queryFor;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class) @ContextConfiguration(locations = {"classpath:spring-test-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true) @Transactional
public class PersistenceServiceITest
{
    @Autowired
    private PersistenceService<Parent> persistenceService;

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Test
    public void shouldSaveEntity()
    {
        final Parent parent = new Parent(1);
        persistAndEvict(parent);

        final Parent loadedParent = persistenceService.findUnique(queryFor(Parent.class).withPrimaryKey("id", parent.getId()).build());

        assertThat(parent.getId(), notNullValue());
        assertThat(loadedParent, equalTo(parent));
    }

    @Test
    public void shouldSaveAssociatedEntity()
    {
        final Parent parent = new Parent(1);
        parent.addChild(new Child(1, parent));
        persistAndEvict(parent);

        final Parent loadedParent = persistenceService.findUnique(queryFor(Parent.class).withPrimaryKey("id", parent.getId()).build());

        fullyAssertObjectGraph(parent, loadedParent);
    }

    @Test
    public void shouldPersistEntityAndThenAddAssociateObject()
    {
        final Parent parent = new Parent(1);
        persistAndEvict(parent);

        parent.addChild(new Child(1, parent));
        persistAndEvict(parent);

        final Parent loadedParent = persistenceService.findUnique(queryFor(Parent.class).withPrimaryKey("id", parent.getId()).build());

        fullyAssertObjectGraph(parent, loadedParent);
    }

    // todo aeells - requires full database integration tests...

    private void fullyAssertObjectGraph(final Parent parent, final Parent loadedParent)
    {
        assertThat(parent.getId(), notNullValue());
        assertThat(parent.getChildren().get(0), notNullValue());
        assertThat(parent.getChildren().get(0).getId(), notNullValue());
        assertThat(loadedParent.getChildren(), CollectionMatcher.containsOnly(parent.getChildren()));
    }

    private void persistAndEvict(final Parent parent)
    {
        persistenceService.create(parent);

        hibernateTemplate.flush();
        hibernateTemplate.evict(parent);
    }
}