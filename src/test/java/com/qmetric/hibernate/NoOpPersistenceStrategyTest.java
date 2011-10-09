package com.qmetric.hibernate;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public final class NoOpPersistenceStrategyTest
{
    @Test
    public void assertNoOpContract()
    {
        final NoOpPersistenceStrategy noOpPersistenceStrategy = new NoOpPersistenceStrategy();

        assertThat(noOpPersistenceStrategy.isCreatable(), equalTo(false));
        assertThat(noOpPersistenceStrategy.isDeletable(), equalTo(false));
        assertThat(noOpPersistenceStrategy.isUpdateable(), equalTo(false));
    }
}