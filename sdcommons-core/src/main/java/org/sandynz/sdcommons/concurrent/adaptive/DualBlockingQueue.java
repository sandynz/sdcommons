/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sandynz.sdcommons.concurrent.adaptive;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Dual {@link BlockingQueue}, include a base queue and a spare queue.
 * It's designed for adaptive executor.
 *
 * @author sandynz
 */
public class DualBlockingQueue<E> implements BlockingQueue<E> {

    /**
     * Underlying base queue.
     */
    private final BlockingQueue<E> baseQueue;

    /**
     * Underlying spare queue.
     * Element will be added to this queue when {@code baseQueue} is full.
     */
    private final BlockingQueue<E> spareQueue;

    public DualBlockingQueue(BlockingQueue<E> baseQueue, BlockingQueue<E> spareQueue) {
        if (baseQueue == null || spareQueue == null) {
            throw new NullPointerException("any queue null");
        }
        this.baseQueue = baseQueue;
        this.spareQueue = spareQueue;
    }

    @Override
    public boolean add(E e) {
        boolean b = baseQueue.add(e);
        return b || spareQueue.add(e);
    }

    @Override
    public boolean offer(E e) {
        boolean b = baseQueue.offer(e);
        return b || spareQueue.offer(e);
    }

    @Override
    public void put(E e) throws InterruptedException {
        boolean b = baseQueue.offer(e);
        if (!b) {
            spareQueue.put(e);
        }
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        long t1 = timeout / 2;
        if (t1 == 0) {
            t1 = timeout;
        }
        boolean b = baseQueue.offer(e, t1, unit);
        if (b) {
            return true;
        }
        long t2 = timeout - t1;
        return spareQueue.offer(e, t2, unit);
    }

    @Override
    public E take() throws InterruptedException {
        // just take from baseQueue, for pool core threads
        return baseQueue.take();
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        long t1 = timeout / 2;
        if (t1 == 0) {
            t1 = timeout;
        }
        E e = baseQueue.poll(t1, unit);
        if (e != null) {
            return e;
        }
        long t2 = timeout - t1;
        e = spareQueue.poll(t2, unit);
        return e != null ? e : baseQueue.poll();
    }

    @Override
    public int remainingCapacity() {
        return baseQueue.remainingCapacity() + spareQueue.remainingCapacity();
    }

    @Override
    public boolean remove(Object o) {
        return baseQueue.remove(o) || spareQueue.remove(o);
    }

    @Override
    public boolean contains(Object o) {
        return baseQueue.contains(o) || spareQueue.contains(o);
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove() {
        E e = this.poll();
        if (e != null) {
            return e;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public E poll() {
        E e = baseQueue.poll();
        return e != null ? e : spareQueue.poll();
    }

    @Override
    public E element() {
        E e = this.peek();
        if (e != null) {
            return e;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public E peek() {
        E e = baseQueue.peek();
        return e != null ? e : spareQueue.peek();
    }

    @Override
    public int size() {
        return baseQueue.size() + spareQueue.size();
    }

    @Override
    public boolean isEmpty() {
        return baseQueue.isEmpty() && spareQueue.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        baseQueue.clear();
        spareQueue.clear();
    }

}
