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

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Collection;

class CollectionMatcher<T> extends TypeSafeMatcher<Collection<? extends T>>
{
    private final Collection<? extends T> expected;

    private CollectionMatcher(Collection<? extends T> expectedCollection)
    {
        this.expected = expectedCollection;
    }

    public static <T, Y extends T> CollectionMatcher<Y> containsOnly(Collection<Y> expected)
    {
        return new CollectionMatcher<Y>(expected);
    }

    public void describeTo(Description description)
    {
        description.appendValue(expected);
        description.appendText("Collections didn't match");
    }

    @Override
    public boolean matchesSafely(Collection<? extends T> actual)
    {
        return actual.containsAll(expected) && expected.size() == actual.size();
    }
}