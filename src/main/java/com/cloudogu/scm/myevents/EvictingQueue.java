package com.cloudogu.scm.myevents;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingQueue;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

public final class EvictingQueue<E> extends ForwardingQueue<E> implements Serializable {

  private final ArrayDeque<E> delegate;

  @VisibleForTesting
  final int maxSize;

  private static final long serialVersionUID = 0L;

  private EvictingQueue(int maxSize) {
    Preconditions.checkArgument(maxSize >= 0, "maxSize (%s) must >= 0", maxSize);
    this.delegate = new ArrayDeque<E>(maxSize);
    this.maxSize = maxSize;
  }

  public static <E> EvictingQueue<E> create(int maxSize) {
    return new EvictingQueue<E>(maxSize);
  }

  protected Queue<E> delegate() {
    return this.delegate;
  }

  public Iterator<E> descendingIterator() {
    return delegate.descendingIterator();
  }

  @CanIgnoreReturnValue
  public boolean add(E e) {
    Preconditions.checkNotNull(e);
    if (this.maxSize == 0) {
      return true;
    } else {
      if (this.size() == this.maxSize) {
        this.delegate.remove();
      }
      this.delegate.add(e);
      return true;
    }
  }

}
