package com.andrew_eells.persistence.infrastructure;

import com.andrew_eells.persistence.infrastructure.model.Parent;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * AbstractPersistentObject unit test cases.
 */
public class AbstractPersistentObjectImplTest

{
    @Test
    public void shouldBeAbleToDistinguishObjects() throws Exception
    {
        final AbstractPersistentObjectImpl parent = new Parent();
        parent.setCreated(new DateTime());
        parent.setLastModified(new DateTime());
        parent.setVersion(1);

        final AbstractPersistentObjectImpl parent2 = new Parent();
        parent.setCreated(new DateTime(System.currentTimeMillis() + 1000));
        parent.setLastModified(new DateTime(System.currentTimeMillis() + 1000));
        parent.setVersion(2);

        assertThat(parent, not(parent2));
    }
}