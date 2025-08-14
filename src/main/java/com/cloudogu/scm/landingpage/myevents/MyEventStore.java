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

import com.cloudogu.scm.landingpage.config.Context;
import com.cloudogu.scm.landingpage.config.LandingpageConfig;
import com.google.common.collect.ImmutableList;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import sonia.scm.plugin.PluginLoader;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.store.QueryableMutableStore;
import sonia.scm.store.QueryableStore;

import java.util.Iterator;
import java.util.List;

@Slf4j
@Singleton
public class MyEventStore {

  private final MyEventStoreFactory storeFactory;
  private final ClassLoader uberClassLoader;
  private final ConfigurationStore<LandingpageConfig> globalConfigStore;

  @Inject
  public MyEventStore(MyEventStoreFactory storeFactory, PluginLoader pluginLoader, ConfigurationStoreFactory configurationStoreFactory) {
    this.storeFactory = storeFactory;
    this.uberClassLoader = pluginLoader.getUberClassLoader();
    this.globalConfigStore = configurationStoreFactory
      .withType(LandingpageConfig.class)
      .withName(Context.STORE_NAME)
      .build();
  }

  public void add(MyEvent event) {
    withUberClassLoader(() -> {
      try (QueryableMutableStore<MyEvent> store = storeFactory.getMutable()) {
        log.debug("Adding event of type {}", event.getType());
        store.put(event);
      }
    });
  }

  public void markRepositoryEventsAsDeleted(String repository) {
    log.debug("Setting deleted for events of repository {}", repository);
    withUberClassLoader(() -> {
      try (QueryableMutableStore<MyEvent> store = storeFactory.getMutable()) {
        store.getAll().forEach((id, event) -> {
          if (
            (event instanceof MyRepositoryEvent) &&
              ((MyRepositoryEvent) event).getRepository().equals(repository)
          ) {
            ((MyRepositoryEvent) event).setDeleted(true);
            store.put(id, event);
          }
          if (
            (event instanceof RepositoryRenamedEventSubscriber.RepositoryRenamedEvent) &&
              ((RepositoryRenamedEventSubscriber.RepositoryRenamedEvent) event).getNewRepository().equals(repository)
          ) {
            ((RepositoryRenamedEventSubscriber.RepositoryRenamedEvent) event).setDeleted(true);
            store.put(id, event);
          }
        });
      }
    });
  }

  public List<MyEvent> getEvents() {
    Subject subject = SecurityUtils.getSubject();

    ImmutableList.Builder<MyEvent> builder = ImmutableList.builder();
    withUberClassLoader(() -> {
      try (QueryableStore<MyEvent> store = storeFactory.get()) {
        List<MyEvent> events = store
          .query()
          .orderBy(MyEventQueryFields.INTERNAL_ID, QueryableStore.Order.DESC)
          .findAll();

        Iterator<MyEvent> iterator = events.iterator();
        int i = 0;
        while (i < 20 && iterator.hasNext()) {
          MyEvent next = iterator.next();
          if (subject.isPermitted(next.getPermission())) {
            builder.add(next);
            ++i;
          }
        }
      }
    });

    return builder.build();
  }

  public void cleanup() {
    log.debug("Cleanup of myevents triggered");
    try (QueryableMutableStore<MyEvent> store = storeFactory.getMutable()) {
      store.query()
        .orderBy(MyEventQueryFields.INTERNAL_ID, QueryableStore.Order.DESC)
        .retain(globalConfigStore.getOptional().orElseGet(LandingpageConfig::new).getMyEventsStoreSize());
    }
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
}


