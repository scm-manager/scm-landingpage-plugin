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

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
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


