package com.cloudogu.scm.myevents;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;
import sonia.scm.store.DataStoreFactory;
import sonia.scm.store.InMemoryConfigurationStore;
import sonia.scm.store.InMemoryConfigurationStoreFactory;
import sonia.scm.store.InMemoryDataStore;
import sonia.scm.store.InMemoryDataStoreFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
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

}
