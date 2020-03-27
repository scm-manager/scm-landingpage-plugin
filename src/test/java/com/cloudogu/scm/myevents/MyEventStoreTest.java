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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.store.InMemoryConfigurationStoreFactory;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyEventStoreTest {

  private MyEventStore store;

  @Mock
  private Subject subject;

  @BeforeEach
  void bindSubject() {
    ThreadContext.bind(subject);
  }

  @BeforeEach
  void setUpObjectUnderTesting() {
    ConfigurationStoreFactory storeFactory = new InMemoryConfigurationStoreFactory();
    store = new MyEventStore(storeFactory);
  }

  @AfterEach
  void tearDownSubject() {
    ThreadContext.unbindSubject();
  }

  @Test
  void shouldStoreEvent() {
    MyEvent event = new MyEvent(MyEvent.class.getSimpleName(), "allowed", "/event");
    when(subject.isPermitted("allowed")).thenReturn(true);

    store.add(event);

    List<MyEvent> events = store.getEvents();
    assertThat(events.size()).isEqualTo(1);
    assertThat(events.iterator().next()).isEqualTo(event);
  }

  @Test
  void shouldOnlyGetPermittedEvents() {
    MyEvent event1 = new MyEvent(MyEvent.class.getSimpleName(), "allowed", "/event");
    doReturn(true).when(subject).isPermitted("allowed");

    MyEvent event2 = new MyEvent(MyEvent.class.getSimpleName(), "forbidden", "/event");
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
      MyEvent event = new MyEvent(MyEvent.class.getSimpleName(), "allowed", "/event" + i);
      store.add(event);
    }

    List<MyEvent> events = store.getEvents();
    assertThat(events.size()).isEqualTo(20);
  }

  @Test
  void shouldGetLatestEvents() {
    when(subject.isPermitted("allowed")).thenReturn(true);
    for (int i = 0; i <= 40; i++) {
      MyEvent event = new MyEvent(MyEvent.class.getSimpleName(), "allowed", "/event" + i);
      store.add(event);
    }

    List<MyEvent> events = store.getEvents();
    assertThat(events.iterator().next().getLink()).isEqualTo("/event40");
  }

  @Test
  void shouldRemoveOldestEntryIfQueueIsFull() {
    when(subject.isPermitted("allowed")).thenReturn(true);
    for (int i = 0; i <= 2000; i++) {
      MyEvent event = new MyEvent(MyEvent.class.getSimpleName(), "allowed", "/event" + i);
      store.add(event);
    }

    List<MyEvent> events = store.getEvents();
    assertThat(events.iterator().next().getLink()).isEqualTo("/event2000");
    assertThat(events.size()).isEqualTo(20);
  }

  @Test
  void shouldMarshallAndUnmarshallMyEvent() {
    MyEventStore.StoreEntry entry = new MyEventStore.StoreEntry();
    entry.getEvents().add(new MyEvent("1", "2", "3"));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    JAXB.marshal(entry, baos);
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

    MyEventStore.StoreEntry unmarshalled = JAXB.unmarshal(bais, MyEventStore.StoreEntry.class);

    MyEvent unmarshalledEvent = unmarshalled.getEvents().descendingIterator().next();
    MyEvent entryEvent = entry.getEvents().descendingIterator().next();

    assertThat(unmarshalledEvent.getLink()).isEqualTo(entryEvent.getLink());
    assertThat(unmarshalledEvent.getPermission()).isEqualTo(entryEvent.getPermission());
    assertThat(unmarshalledEvent.getType()).isEqualTo(entryEvent.getType());
  }

  @Test
  void shouldMarshallAndUnmarshallPushEvent() {
    MyEventStore.StoreEntry entry = new MyEventStore.StoreEntry();
    entry.getEvents().add(new RepositoryPushEventSubscriber.PushEvent("repository:1:read", "/repo/1", "repo/1", "Trillian", 1, Instant.now()));
    entry.getEvents().add(new RepositoryPushEventSubscriber.PushEvent("repository:2:read", "/repo/2", "repo/2", "Trillian", 2, Instant.now()));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    JAXB.marshal(entry, baos);
    System.out.println(baos);
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

    MyEventStore.StoreEntry unmarshalled = JAXB.unmarshal(bais, MyEventStore.StoreEntry.class);

    RepositoryPushEventSubscriber.PushEvent unmarshalledEvent = (RepositoryPushEventSubscriber.PushEvent) unmarshalled.getEvents().descendingIterator().next();
    RepositoryPushEventSubscriber.PushEvent entryEvent = (RepositoryPushEventSubscriber.PushEvent) entry.getEvents().descendingIterator().next();

    assertThat(unmarshalledEvent.getLink()).isEqualTo(entryEvent.getLink());
    assertThat(unmarshalledEvent.getPermission()).isEqualTo(entryEvent.getPermission());
    assertThat(unmarshalledEvent.getType()).isEqualTo(entryEvent.getType());
    assertThat(unmarshalledEvent.getAuthor()).isEqualTo(entryEvent.getAuthor());
  }
}
