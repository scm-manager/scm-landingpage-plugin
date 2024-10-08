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

import com.google.common.io.Resources;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.plugin.PluginLoader;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.store.InMemoryConfigurationStoreFactory;
import sonia.scm.user.User;

import jakarta.xml.bind.JAXB;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyEventStoreTest {

  private static final Instant NOW = Instant.now();

  private MyEventStore store;

  @Mock
  private Subject subject;
  @Mock
  private PluginLoader pluginLoader;

  @BeforeEach
  void bindSubject() {
    ThreadContext.bind(subject);
  }

  @BeforeEach
  void setUpObjectUnderTesting() {
    ConfigurationStoreFactory storeFactory = new InMemoryConfigurationStoreFactory();
    store = new MyEventStore(storeFactory, pluginLoader);
  }

  @AfterEach
  void tearDownSubject() {
    ThreadContext.unbindSubject();
  }

  @Test
  void shouldStoreEvent() {
    MyEvent event = new MyEvent(MyEvent.class.getSimpleName(), "allowed");
    when(subject.isPermitted("allowed")).thenReturn(true);

    store.add(event);

    List<MyEvent> events = store.getEvents();
    assertThat(events.size()).isEqualTo(1);
    assertThat(events.iterator().next()).isEqualTo(event);
  }

  @Test
  void shouldOnlyGetPermittedEvents() {
    MyEvent event1 = new MyEvent(MyEvent.class.getSimpleName(), "allowed");
    doReturn(true).when(subject).isPermitted("allowed");

    MyEvent event2 = new MyEvent(MyEvent.class.getSimpleName(), "forbidden");
    doReturn(false).when(subject).isPermitted("forbidden");

    store.add(event1);
    store.add(event2);

    List<MyEvent> events = store.getEvents();
    assertThat(events.size()).isEqualTo(1);
    assertThat(events.iterator().next()).isEqualTo(event1);
  }

  @Test
  void shouldOnlyGet20Events() {
    when(subject.isPermitted("allowed")).thenReturn(true);
    for (int i = 0; i <= 40; i++) {
      MyEvent event = new MyEvent(MyEvent.class.getSimpleName(), "allowed");
      store.add(event);
    }

    List<MyEvent> events = store.getEvents();
    assertThat(events.size()).isEqualTo(20);
  }

  @Test
  void shouldGetLatestEvents() {
    when(subject.isPermitted(anyString())).thenReturn(true);
    for (int i = 0; i <= 40; i++) {
      MyEvent event = new MyEvent(MyEvent.class.getSimpleName(), "allowed" + i);
      store.add(event);
    }

    List<MyEvent> events = store.getEvents();

    assertThat(events.iterator().next().getPermission()).isEqualTo("allowed40");
  }

  @Test
  void shouldRemoveOldestEntryIfQueueIsFull() {
    when(subject.isPermitted(anyString())).thenReturn(true);
    for (int i = 0; i <= 2000; i++) {
      MyEvent event = new MyEvent(MyEvent.class.getSimpleName(), "allowed" + i);
      store.add(event);
    }

    List<MyEvent> events = store.getEvents();
    assertThat(events.size()).isEqualTo(20);
    assertThat(events.iterator().next().getPermission()).isEqualTo("allowed2000");
  }

  @Test
  void shouldMarshallAndUnmarshallMyEvent() {
    MyEventStore.StoreEntry entry = new MyEventStore.StoreEntry();
    entry.getEvents().add(new MyEvent("1", "2"));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    JAXB.marshal(entry, baos);
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

    MyEventStore.StoreEntry unmarshalled = JAXB.unmarshal(bais, MyEventStore.StoreEntry.class);

    MyEvent unmarshalledEvent = unmarshalled.getEvents().descendingIterator().next();
    MyEvent entryEvent = entry.getEvents().descendingIterator().next();

    assertThat(unmarshalledEvent.getPermission()).isEqualTo(entryEvent.getPermission());
    assertThat(unmarshalledEvent.getType()).isEqualTo(entryEvent.getType());
  }

  @Test
  void shouldMarshallAndUnmarshallPushEvent() {
    User trillian = new User("trillian", "Trillian", "tricia@hitchhiker.org");
    MyEventStore.StoreEntry entry = new MyEventStore.StoreEntry();
    entry.getEvents().add(createPushEvent("repository:1:read", "repo/1", trillian, 1));
    entry.getEvents().add(createPushEvent("repository:2:read", "repo/2", trillian, 2));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    JAXB.marshal(entry, baos);
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

    MyEventStore.StoreEntry unmarshalled = JAXB.unmarshal(bais, MyEventStore.StoreEntry.class);

    RepositoryPushEventSubscriber.PushEvent unmarshalledEvent = (RepositoryPushEventSubscriber.PushEvent) unmarshalled.getEvents().descendingIterator().next();
    RepositoryPushEventSubscriber.PushEvent entryEvent = (RepositoryPushEventSubscriber.PushEvent) entry.getEvents().descendingIterator().next();

    assertThat(unmarshalledEvent.getPermission()).isEqualTo(entryEvent.getPermission());
    assertThat(unmarshalledEvent.getType()).isEqualTo(entryEvent.getType());
    assertThat(unmarshalledEvent.getAuthorName()).isEqualTo(entryEvent.getAuthorName());
    assertThat(unmarshalledEvent.getRepository()).isEqualTo(entryEvent.getRepository());
    assertThat(unmarshalledEvent.getDate()).isEqualTo(entryEvent.getDate());
    assertThat(unmarshalledEvent.getChangesets()).isEqualTo(entryEvent.getChangesets());
  }


  @Test
  void shouldUnmarshallEventsFromFile() throws IOException {
    URL resource = Resources.getResource("com/cloudogu/scm/landingpage/myevents/myevents.xml");
    InputStream inputStream = resource.openStream();
    MyEventStore.StoreEntry unmarshal = JAXB.unmarshal(inputStream, MyEventStore.StoreEntry.class);

    EvictingQueue<MyEvent> events = unmarshal.getEvents();

    assertThat(events.size()).isEqualTo(3);

    MyEvent latestEvent = events.descendingIterator().next();
    assertThat(latestEvent.getType()).isEqualTo("PushEvent");
    assertThat(latestEvent.getPermission()).isEqualTo("repository:read:EIRu8Tnsn2");
  }

  @Test
  void shouldMarkRepositoryEventsAsDeleted() {
    MyEvent event = new MyRepositoryEvent(MyRepositoryEvent.class.getSimpleName(), "allowed", "myRepo");
    MyEvent otherEvent = new MyRepositoryEvent(MyRepositoryEvent.class.getSimpleName(), "allowed", "myOtherRepo");
    MyEvent myRenamedEvent = new RepositoryRenamedEventSubscriber.RepositoryRenamedEvent("allowed", "myRepo", "myOtherRepo", "admin", "admin@scm-manager.org");
    MyEvent myOtherRenamedEvent = new RepositoryRenamedEventSubscriber.RepositoryRenamedEvent("allowed", "myRepoOther", "myRepo", "admin", "admin@scm-manager.org");

    when(subject.isPermitted("allowed")).thenReturn(true);

    store.add(event);
    store.add(otherEvent);
    store.add(myRenamedEvent);
    store.add(myOtherRenamedEvent);

    store.markRepositoryEventsAsDeleted("myOtherRepo");

    List<MyEvent> events = store.getEvents();
    assertThat(events).contains(
      new MyRepositoryEvent(MyEvent.class.getSimpleName(), "allowed", "myOtherRepo", true),
      new RepositoryRenamedEventSubscriber.RepositoryRenamedEvent("allowed", "myRepo", "myOtherRepo", "admin", "admin@scm-manager.org", true),
      event,
      myOtherRenamedEvent
    );
  }

  private RepositoryPushEventSubscriber.PushEvent createPushEvent(String permission, String repository, User user, int changesets) {
    return new RepositoryPushEventSubscriber.PushEvent(permission, repository , user.getName(), user.getDisplayName(), user.getMail(), changesets);
  }

}
