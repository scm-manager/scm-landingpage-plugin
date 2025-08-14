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
import jakarta.inject.Inject;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import org.w3c.dom.Element;
import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.plugin.PluginLoader;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.store.QueryableMutableStore;
import sonia.scm.version.Version;

@Extension
public class QueryableStoreUpdateStep implements UpdateStep {

  private static final String STORE_NAME = "myevents";

  private final ClassLoader uberClassLoader;
  private final MyEventStoreFactory myEventStoreFactory;
  private final ConfigurationStore<LegacyStoreEntry> legacyStore;

  @Inject
  public QueryableStoreUpdateStep(MyEventStoreFactory myEventStoreFactory, ConfigurationStoreFactory legacyStoreFactory, PluginLoader pluginLoader) {
    this.myEventStoreFactory = myEventStoreFactory;
    this.legacyStore = legacyStoreFactory.withType(LegacyStoreEntry.class).withName(STORE_NAME).build();
    this.uberClassLoader = pluginLoader.getUberClassLoader();
  }

  @Override
  public void doUpdate() {
    withUberClassLoader(() -> {
      legacyStore.getOptional().ifPresent(storeEntry -> {
        try (QueryableMutableStore<MyEvent> queryableStore = myEventStoreFactory.getMutable()) {
          queryableStore.transactional(() -> {
            storeEntry.getEvents().forEach(queryableStore::put);
            return true;
          });
        }
      });
    });
  }

  @Override
  public Version getTargetVersion() {
    return Version.parse("3.1.0");
  }

  @Override
  public String getAffectedDataType() {
    return "com.cloudogu.scm.landingpage.myevents.MyEvent";
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

  @XmlRootElement(name = "storeEntry")
  @XmlAccessorType(XmlAccessType.FIELD)
  @Getter
  static class LegacyStoreEntry {
    @XmlJavaTypeAdapter(MyEventXmlAdapter.class)
    private EvictingQueue<MyEvent> events = EvictingQueue.create(1000);
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  static class LegacyMyEventStoreEntry {

    private Class type;

    @XmlAnyElement
    private Element payload;
  }

  static class MyEventXmlAdapter extends XmlAdapter<LegacyMyEventStoreEntry, MyEvent> {

    private static final LoadingCache<Class, JAXBContext> cache = CacheBuilder.newBuilder()
      .maximumSize(100)
      .build(
        new CacheLoader<>() {
          @Override
          public JAXBContext load(Class c) throws JAXBException {
            return JAXBContext.newInstance(c);
          }
        });

    @Override
    public MyEvent unmarshal(LegacyMyEventStoreEntry myEventStoreEntry) throws Exception {
      JAXBContext jaxbContext = cache.get(myEventStoreEntry.type);
      return (MyEvent) jaxbContext.createUnmarshaller().unmarshal(myEventStoreEntry.payload);
    }

    @Override
    public LegacyMyEventStoreEntry marshal(MyEvent event) {
      throw new RuntimeException("Should never be called. It is only implemented to satisfy the interface");
    }
  }
}
