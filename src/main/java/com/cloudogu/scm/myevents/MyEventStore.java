/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.cloudogu.scm.myevents;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Iterator;
import java.util.List;

@Singleton
public class MyEventStore {

  private final String STORE_NAME = "myevents";

  private final ConfigurationStore<StoreEntry> store;

  @Inject
  public MyEventStore(ConfigurationStoreFactory storeFactory) {
    this.store = storeFactory.withType(StoreEntry.class).withName(STORE_NAME).build();
  }

  public synchronized void add(MyEvent event) {
    StoreEntry storeEntry = getStoreEntry();
    storeEntry.events.add(event);
    store.set(storeEntry);
  }

  public List<MyEvent> getEvents() {
    Subject subject = SecurityUtils.getSubject();

    ImmutableList.Builder<MyEvent> builder = ImmutableList.builder();
    StoreEntry storeEntry = getStoreEntry();
    int i = 0;

    Iterator<MyEvent> myEventIterator = storeEntry.events.descendingIterator();

    while (i < 20 && myEventIterator.hasNext()) {
      MyEvent next = myEventIterator.next();
      if (subject.isPermitted(next.getPermission())) {
        builder.add(next);
        i++;
      }
    }

    return builder.build();
  }

  private StoreEntry getStoreEntry() {
    StoreEntry storeEntry = store.get();
    if (storeEntry == null) {
      storeEntry = new StoreEntry();
    }
    return storeEntry;
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  @Getter
  static class StoreEntry {
    private EvictingQueue<MyEvent> events = EvictingQueue.create(1000);
  }
}


