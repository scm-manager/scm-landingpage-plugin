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
import org.github.sdorra.jse.ShiroExtension;
import org.github.sdorra.jse.SubjectAware;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.plugin.PluginLoader;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.store.InMemoryByteConfigurationStoreFactory;
import sonia.scm.store.QueryableStoreExtension;
import sonia.scm.user.User;

import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class, QueryableStoreExtension.class, ShiroExtension.class})
@QueryableStoreExtension.QueryableTypes(MyEvent.class)
class MyEventStoreTest {

  private MyEventStore myEventStore;
  private final ConfigurationStoreFactory configStoreFactory = new InMemoryByteConfigurationStoreFactory();

  @Mock
  private PluginLoader pluginLoader;

  @BeforeEach
  void setUpObjectUnderTesting(MyEventStoreFactory eventStoreFactory) {
    myEventStore = new MyEventStore(eventStoreFactory, pluginLoader, configStoreFactory);
  }

  @Test
  @SubjectAware(value = "Trainer Red", permissions = "allowed")
  void shouldStoreEvent() {
    MyEvent event = new MyEvent(MyEvent.class.getSimpleName(), "allowed");

    myEventStore.add(event);

    List<MyEvent> events = myEventStore.getEvents();
    assertThat(events).hasSize(1);
    assertThat(events.get(0)).isEqualTo(event);
  }

  @Test
  @SubjectAware(value = "Trainer Red", permissions = "allowed")
  void shouldOnlyGetPermittedEvents() {
    MyEvent event1 = new MyEvent(MyEvent.class.getSimpleName(), "allowed");
    MyEvent event2 = new MyEvent(MyEvent.class.getSimpleName(), "forbidden");

    myEventStore.add(event1);
    myEventStore.add(event2);

    List<MyEvent> events = myEventStore.getEvents();
    assertThat(events).hasSize(1);
    assertThat(events.get(0)).isEqualTo(event1);
  }

  @Test
  @SubjectAware(value = "Trainer Red", permissions = "allowed")
  void shouldOnlyGet20Events() {
    for (int i = 0; i <= 40; i++) {
      MyEvent event = new MyEvent(MyEvent.class.getSimpleName(), "allowed");
      myEventStore.add(event);
    }

    List<MyEvent> events = myEventStore.getEvents();
    assertThat(events).hasSize(20);
  }

  @Test
  @SubjectAware(value = "Trainer Red", permissions = "*")
  void shouldGetLatestEvents() {
    for (int i = 0; i <= 40; i++) {
      MyEvent event = new MyEvent(MyEvent.class.getSimpleName(), "allowed" + i);
      myEventStore.add(event);
    }

    List<MyEvent> events = myEventStore.getEvents();
    assertThat(events).hasSize(20);

    Iterator<MyEvent> iterator = events.iterator();
    for (int i = 40; i > 20; --i) {
      assertThat(iterator.next().getPermission()).isEqualTo("allowed" + i);
    }
  }

  @Test
  @SubjectAware(value = "Trainer Red", permissions = "repository:1:read")
  void shouldKeepPushEventSpecificProperties() {
    User trillian = new User("trillian", "Trillian", "tricia@hitchhiker.org");
    myEventStore.add(
      new RepositoryPushEventSubscriber.PushEvent(
        "repository:1:read",
        "repo/1",
        trillian.getName(),
        trillian.getDisplayName(),
        trillian.getMail(),
        1
      )
    );

    List<MyEvent> events = myEventStore.getEvents();

    assertThat(events).hasSize(1);
    MyEvent event = events.get(0);
    assertThat(event.getType()).isEqualTo(RepositoryPushEventSubscriber.PushEvent.class.getSimpleName());
    assertThat(event.getPermission()).isEqualTo("repository:1:read");

    assertThat(event).isInstanceOf(RepositoryPushEventSubscriber.PushEvent.class);
    RepositoryPushEventSubscriber.PushEvent pushEvent = (RepositoryPushEventSubscriber.PushEvent) event;
    assertThat(pushEvent.getRepository()).isEqualTo("repo/1");
    assertThat(pushEvent.isDeleted()).isFalse();
    assertThat(pushEvent.getAuthorName()).isEqualTo(trillian.getName());
    assertThat(pushEvent.getAuthorDisplayName()).isEqualTo(trillian.getDisplayName());
    assertThat(pushEvent.getAuthorMail()).isEqualTo(trillian.getMail());
    assertThat(pushEvent.getChangesets()).isEqualTo(1);
  }


  @Test
  @SubjectAware(value = "Trainer Red", permissions = "allowed")
  void shouldMarkRepositoryEventsAsDeleted() {
    MyEvent event = new MyRepositoryEvent(MyRepositoryEvent.class.getSimpleName(), "allowed", "myRepo");
    MyEvent otherEvent = new MyRepositoryEvent(MyRepositoryEvent.class.getSimpleName(), "allowed", "myOtherRepo");
    MyEvent myRenamedEvent = new RepositoryRenamedEventSubscriber.RepositoryRenamedEvent("allowed", "myRepo", "myOtherRepo", "admin", "admin@scm-manager.org");
    MyEvent myOtherRenamedEvent = new RepositoryRenamedEventSubscriber.RepositoryRenamedEvent("allowed", "myRepoOther", "myRepo", "admin", "admin@scm-manager.org");

    myEventStore.add(event);
    myEventStore.add(otherEvent);
    myEventStore.add(myRenamedEvent);
    myEventStore.add(myOtherRenamedEvent);

    myEventStore.markRepositoryEventsAsDeleted("myOtherRepo");

    List<MyEvent> events = myEventStore.getEvents();
    assertThat(events).contains(
      new MyRepositoryEvent(MyEvent.class.getSimpleName(), "allowed", "myOtherRepo", true),
      new RepositoryRenamedEventSubscriber.RepositoryRenamedEvent("allowed", "myRepo", "myOtherRepo", "admin", "admin@scm-manager.org", true),
      event,
      myOtherRenamedEvent
    );
  }

  @Test
  @SubjectAware(value = "Trainer Red", permissions = "*")
  void shouldCleanupOldEventsOutsideOfStoreSize() {
    LandingpageConfig config = new LandingpageConfig();
    config.setMyEventsStoreSize(10);
    configStoreFactory.withType(LandingpageConfig.class).withName(Context.STORE_NAME).build().set(config);

    for (int i = 0; i <= config.getMyEventsStoreSize(); i++) {
      myEventStore.add(new MyRepositoryEvent(MyRepositoryEvent.class.getSimpleName(), "allowed" + i, "myRepo"));
    }
    assertThat(myEventStore.getEvents()).hasSize(config.getMyEventsStoreSize() + 1);

    myEventStore.cleanup();
    List<MyEvent> events = myEventStore.getEvents();
    assertThat(events).hasSize(config.getMyEventsStoreSize());
    Iterator<MyEvent> iterator = events.iterator();
    for (int i = config.getMyEventsStoreSize(); i > 0; --i) {
      assertThat(iterator.next().getPermission()).isEqualTo("allowed" + i);
    }
  }
}
