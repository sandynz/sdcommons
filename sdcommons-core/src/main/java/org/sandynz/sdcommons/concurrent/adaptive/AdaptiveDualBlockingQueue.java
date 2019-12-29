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
 * Adaptive dual {@link BlockingQueue}, include a base queue and a spare queue.
 * <p>
 * It's designed for adaptive executor, not for general usage.
 * <p>
 * In general, {@code baseQueue} should be owned my a dedicated thread pool,
 * {@code spareQueue} should be shared by several thread pools.
 * <p>
 * Example:
 * TODO
 *
 * @author sandynz
 */
public class AdaptiveDualBlockingQueue implements BlockingQueue<Runnable> {

    /**
     * Underlying base queue.
     */
    private final BlockingQueue<Runnable> baseQueue;

    /**
     * Underlying spare queue.
     * Element will be added to this queue when {@code baseQueue} is full.
     */
    private final BlockingQueue<Runnable> spareQueue;

    public AdaptiveDualBlockingQueue(BlockingQueue<Runnable> baseQueue, BlockingQueue<Runnable> spareQueue) {
        if (baseQueue == null || spareQueue == null) {
            throw new NullPointerException("any queue null");
        }
        this.baseQueue = baseQueue;
        this.spareQueue = spareQueue;
    }

    @Override
    public boolean add(Runnable e) {
        boolean b = baseQueue.add(e);
        return b || spareQueue.add(e);
    }

    @Override
    public boolean offer(Runnable e) {
        boolean b = baseQueue.offer(e);
        return b || spareQueue.offer(e);
    }

    @Override
    public void put(Runnable e) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offer(Runnable e, long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable take() throws InterruptedException {
        //boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;
        //Runnable r = timed ? workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) : workQueue.take();
        // just take from baseQueue, for pool core threads
        return baseQueue.take();
    }

    @Override
    public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException {
        //boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;
        //Runnable r = timed ? workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) : workQueue.take();
        long leftInNano = unit.toNanos(timeout);
        long sliceInNano = TimeUnit.MILLISECONDS.toNanos(5);
        if (leftInNano < sliceInNano) {
            sliceInNano = leftInNano / 2;
        }
        for (int round = 1; leftInNano > 0; ++round) {
            BlockingQueue<Runnable> queue = (round & 1) == 1 ? baseQueue : spareQueue;
            Runnable e = queue.poll(Math.min(sliceInNano, leftInNano), TimeUnit.NANOSECONDS);
            if (e != null) {
                return e;
            }
            leftInNano -= sliceInNano;
        }
        return null;
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
    public int drainTo(Collection<? super Runnable> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int drainTo(Collection<? super Runnable> c, int maxElements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable remove() {
        Runnable e = this.poll();
        if (e != null) {
            return e;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public Runnable poll() {
        Runnable e = baseQueue.poll();
        return e != null ? e : spareQueue.poll();
    }

    @Override
    public Runnable element() {
        Runnable e = this.peek();
        if (e != null) {
            return e;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public Runnable peek() {
        Runnable e = baseQueue.peek();
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
    public Iterator<Runnable> iterator() {
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
    public boolean addAll(Collection<? extends Runnable> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeIf(Predicate<? super Runnable> filter) {
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
