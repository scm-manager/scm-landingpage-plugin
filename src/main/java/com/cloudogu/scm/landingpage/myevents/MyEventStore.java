/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package com.cloudogu.scm.landingpage.myevents;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sonia.scm.plugin.PluginLoader;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javax.xml.XMLConstants;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Iterator;
import java.util.List;

@Singleton
public class MyEventStore {

  private static final String STORE_NAME = "myevents";

  private final ConfigurationStore<StoreEntry> store;
  private final ClassLoader uberClassLoader;

  @Inject
  public MyEventStore(ConfigurationStoreFactory storeFactory, PluginLoader pluginLoader) {
    this.store = storeFactory.withType(StoreEntry.class).withName(STORE_NAME).build();
    this.uberClassLoader = pluginLoader.getUberClassLoader();
  }

  public synchronized void add(MyEvent event) {
    withUberClassLoader(() -> {
      StoreEntry storeEntry = getStoreEntry();
      storeEntry.events.add(event);
      store.set(storeEntry);
    });
  }

  public synchronized void markRepositoryEventsAsDeleted(String repository) {
    withUberClassLoader(() -> {
      StoreEntry storeEntry = getStoreEntry();
      storeEntry.events.forEach(it -> {
        if (
          (it instanceof MyRepositoryEvent) &&
            ((MyRepositoryEvent) it).getRepository().equals(repository)
        ) {
          ((MyRepositoryEvent) it).setDeleted(true);
        }
        if (
          (it instanceof RepositoryRenamedEventSubscriber.RepositoryRenamedEvent) &&
            ((RepositoryRenamedEventSubscriber.RepositoryRenamedEvent) it).getNewRepository().equals(repository)
        ) {
          ((RepositoryRenamedEventSubscriber.RepositoryRenamedEvent) it).setDeleted(true);
        }
      });
      store.set(storeEntry);
    });
  }

  public List<MyEvent> getEvents() {
    Subject subject = SecurityUtils.getSubject();

    ImmutableList.Builder<MyEvent> builder = ImmutableList.builder();
    withUberClassLoader(() -> {
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
    });
    return builder.build();
  }

  private void withUberClassLoader(Runnable runnable) {
    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(uberClassLoader);
    try {
      runnable.run();
    } finally {
      Thread.currentThread().setContextClassLoader(contextClassLoader);
    }
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
    @XmlJavaTypeAdapter(MyEventXmlAdapter.class)
    private EvictingQueue<MyEvent> events = EvictingQueue.create(1000);
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  public static class MyEventStoreEntry {

    private Class type;

    @XmlAnyElement
    private Element payload;
  }

  public static class MyEventXmlAdapter extends XmlAdapter<MyEventStoreEntry, MyEvent> {

    private static final LoadingCache<Class, JAXBContext> cache = CacheBuilder.newBuilder()
      .maximumSize(1000)
      .build(
        new CacheLoader<Class, JAXBContext>() {
          @Override
          public JAXBContext load(Class c) throws JAXBException {
            return JAXBContext.newInstance(c);
          }
        });

    @Override
    public MyEvent unmarshal(MyEventStoreEntry myEventStoreEntry) throws Exception {
      JAXBContext jaxbContext = cache.get(myEventStoreEntry.type);
      return (MyEvent) jaxbContext.createUnmarshaller().unmarshal(myEventStoreEntry.payload);
    }

    @Override
    public MyEventStoreEntry marshal(MyEvent event) throws Exception {
      MyEventStoreEntry entry = new MyEventStoreEntry();
      entry.type = event.getClass();

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
      factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
      factory.setAttribute(XMLConstants.FEATURE_SECURE_PROCESSING, true);

      Document document = factory.newDocumentBuilder().newDocument();

      JAXBContext jaxbContext = cache.get(event.getClass());
      jaxbContext.createMarshaller().marshal(event, document);

      entry.payload = document.getDocumentElement();

      return entry;
    }
  }
}


