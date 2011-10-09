package com.qmetric.hibernate.service;

import com.qmetric.hibernate.PersistenceStrategy;
import com.qmetric.hibernate.model.PersistentObjectStub;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import static com.qmetric.hibernate.service.PersistenceQuery.Builder.queryFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;

public final class PersistenceQueryTest
{
    @Test
    public void basicBuild()
    {
        final PersistenceQuery query = queryFor(PersistentObjectStub.class).build();

        //noinspection unchecked
        assertThat((Class<PersistentObjectStub>) query.getDaoClass(), equalTo(PersistentObjectStub.class));
        assertNotNull(query);
    }

    @Test
    public void primaryKeyBuild()
    {
        final PersistenceQuery query = queryFor(PersistentObjectStub.class).withPrimaryKey("id", "a").build();
        final DetachedCriteria criteria = query.getCriteria();

        //noinspection unchecked
        assertThat((Class<PersistentObjectStub>) query.getDaoClass(), equalTo(PersistentObjectStub.class));
        assertThat(criteria.toString(), containsString("[id=a]"));
    }

    @Test
    public void foreignKeyBuild()
    {
        final PersistenceStrategy foreignKeyRef = new PersistentObjectStub();
        final PersistenceStrategy primaryObject = new PersistentObjectStub(foreignKeyRef);
        final PersistenceQuery query = queryFor(PersistentObjectStub.class).withForeignKey("reference", foreignKeyRef).build();
        final DetachedCriteria criteria = query.getCriteria();

        //noinspection unchecked
        assertThat((Class<PersistentObjectStub>) query.getDaoClass(), equalTo(PersistentObjectStub.class));
        assertThat(criteria.toString(), containsString("[reference=" + foreignKeyRef.toString() + "]"));
    }
}