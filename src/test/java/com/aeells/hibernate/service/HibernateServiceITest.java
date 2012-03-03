/**
 * Copyright (c) 2012 Andrew Eells
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.aeells.hibernate.service;

import com.aeells.hibernate.model.Child;
import com.aeells.hibernate.model.Parent;
import com.qmetric.testing.hamcrest.matchers.CollectionMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class) @ContextConfiguration(locations = {"classpath:spring-test-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true) @Transactional
public class HibernateServiceITest
{
    @Autowired
    private HibernateService<Parent> hibernateService;

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Test
    public void shouldSaveEntity()
    {
        final Parent parent = new Parent(1);
        persistAndEvict(parent);

        final Parent loadedParent = hibernateService.findById(Parent.class, parent.getId());

        assertThat(parent.getId(), notNullValue());
        assertThat(loadedParent, equalTo(parent));
    }

    @Test
    public void shouldSaveAssociatedEntity()
    {
        final Parent parent = new Parent(1);
        parent.addChild(new Child(1, parent));
        persistAndEvict(parent);

        final Parent loadedParent = hibernateService.findById(Parent.class, parent.getId());

        fullyAssertObjectGraph(parent, loadedParent);
    }

    @Test
    public void shouldPersistEntityAndThenAddAssociateObject()
    {
        final Parent parent = new Parent(1);
        persistAndEvict(parent);

        parent.addChild(new Child(1, parent));
        persistAndEvict(parent);

        final Parent loadedParent = hibernateService.findById(Parent.class, parent.getId());

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
        hibernateService.create(parent);

        hibernateTemplate.flush();
        hibernateTemplate.evict(parent);
    }
}