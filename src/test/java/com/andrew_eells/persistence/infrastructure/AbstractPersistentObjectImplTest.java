package com.andrew_eells.persistence.infrastructure;

import junit.framework.TestCase;

import java.util.Date;

/**
 * AbstractPersistentObject unit test cases.
 */
public class AbstractPersistentObjectImplTest extends TestCase
{
    /**
     * Test creation of a user to db.
     *
     * @throws Exception Exception.
     */
    public void testIdCreation() throws Exception
    {
        final AbstractPersistentObjectImpl obj1 = new TestPersistentObject();

        assertTrue("Unexpected ID length", obj1.getId().length() == 36);
    }

    /**
     * Test creation of a user to db.
     *
     * @throws Exception Exception.
     */
    public void testEquality() throws Exception
    {
        final AbstractPersistentObjectImpl obj1 = new TestPersistentObject();
        obj1.setCreated(new Date());
        obj1.setLastModified(new Date());
        obj1.setVersion(1);

        final AbstractPersistentObjectImpl obj2 = new TestPersistentObject();
        obj1.setCreated(new Date(System.currentTimeMillis() + 1000));
        obj1.setLastModified(new Date(System.currentTimeMillis() + 1000));
        obj1.setVersion(2);

        assertNotSame("Objects should not be equal (massive odds against it anyway)", obj1, obj2);

        obj2.setId(obj1.getId());
        assertEquals("Objects not equal", obj1, obj2);
        assertEquals("Objects hashcodes not equal", obj1.hashCode(), obj2.hashCode());

        assertEquals("Objects ids not equal", obj1.getId(), obj2.getId());
        assertNotSame("Objects created equal", obj1.getCreated(), obj2.getCreated());
        assertNotSame("Objects lastModified equal", obj1.getLastModified(), obj2.getLastModified());
        assertNotSame("Objects version equal", obj1.getVersion(), obj2.getVersion());
    }

    /**
     * Concrete implementation of AbstractPersistentObject, purely used for testing purposes.
     */
    private class TestPersistentObject extends AbstractPersistentObjectImpl
    {
        @Override public boolean isCreate()
        {
            return true;
        }

        @Override public boolean isUpdate()
        {
            return true;
        }

        @Override public boolean isDelete()
        {
            return true;
        }
    }
}