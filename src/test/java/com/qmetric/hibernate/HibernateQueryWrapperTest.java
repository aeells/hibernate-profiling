package com.qmetric.hibernate;

import com.qmetric.hibernate.model.PersistentObjectStub;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import static com.qmetric.hibernate.HibernateQueryWrapper.Builder.queryFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;

public final class HibernateQueryWrapperTest
{
    @Test
    public void basicBuild()
    {
        final HibernateQueryWrapper query = queryFor(PersistentObjectStub.class).build();

        //noinspection unchecked
        assertThat((Class<PersistentObjectStub>) query.getDaoClass(), equalTo(PersistentObjectStub.class));
        assertNotNull(query);
    }

    @Test
    public void primaryKeyBuild()
    {
        final HibernateQueryWrapper query = queryFor(PersistentObjectStub.class).withPrimaryKey("id", "a").build();
        final DetachedCriteria criteria = query.getCriteria();

        //noinspection unchecked
        assertThat((Class<PersistentObjectStub>) query.getDaoClass(), equalTo(PersistentObjectStub.class));
        assertThat(criteria.toString(), containsString("[id=a]"));
    }

    @Test
    public void foreignKeyBuild()
    {
        final PersistenceStrategy foreignKeyRef = new PersistentObjectStub();
        final HibernateQueryWrapper query = queryFor(PersistentObjectStub.class).withForeignKey("fieldName", foreignKeyRef).build();
        final DetachedCriteria criteria = query.getCriteria();

        //noinspection unchecked
        assertThat((Class<PersistentObjectStub>) query.getDaoClass(), equalTo(PersistentObjectStub.class));
        assertThat(criteria.toString(), containsString("[fieldName=" + foreignKeyRef.toString() + "]"));
    }

    @Test
    public void fieldReferenceBuild()
    {
        final PersistenceStrategy foreignKeyRef = new PersistentObjectStub();
        final HibernateQueryWrapper query = queryFor(PersistentObjectStub.class).withField("fieldName", foreignKeyRef).build();
        final DetachedCriteria criteria = query.getCriteria();

        //noinspection unchecked
        assertThat((Class<PersistentObjectStub>) query.getDaoClass(), equalTo(PersistentObjectStub.class));
        assertThat(criteria.toString(), containsString("[fieldName=" + foreignKeyRef.toString() + "]"));
    }

    @Test
    public void sortAscending()
    {
        // there is no real test here, other than enforcing the API. cannot get reference to underlying criteria impl...
        final HibernateQueryWrapper query = queryFor(PersistentObjectStub.class).sortAsc("fieldName").build();

        //noinspection unchecked
        assertThat((Class<PersistentObjectStub>) query.getDaoClass(), equalTo(PersistentObjectStub.class));
        assertNotNull(query);
    }

    @Test
    public void sortDescending()
    {
        // there is no real test here, other than enforcing the API. cannot get reference to underlying criteria impl...
        final HibernateQueryWrapper query = queryFor(PersistentObjectStub.class).sortDesc("fieldName").build();

        //noinspection unchecked
        assertThat((Class<PersistentObjectStub>) query.getDaoClass(), equalTo(PersistentObjectStub.class));
        assertNotNull(query);
    }

    @Test
    public void limitResults()
    {
        final HibernateQueryWrapper query = queryFor(PersistentObjectStub.class).limit(0, 10).build();

        //noinspection unchecked
        assertThat(query.getLimit(), equalTo(new QueryLimit(0, 10)));
    }

    @Test
    public void noOpLimitResults()
    {
        final HibernateQueryWrapper query = queryFor(PersistentObjectStub.class).build();

        //noinspection unchecked
        assertThat(query.getLimit(), equalTo(QueryLimit.NULL_IMPL));
    }
}