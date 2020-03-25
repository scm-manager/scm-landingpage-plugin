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
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyEventsCollectorTest {

  @Test
  void shouldReturnCollectedEvents() {
    MyEventProvider provider = mock(MyEventProvider.class);
    EventOne a = new EventOne();
    EventTwo b = new EventTwo();

    Iterable<MyEvent> events = ImmutableList.of(a, b);
    when(provider.getEvents()).thenReturn(events);

    MyEventCollector collector = new MyEventCollector(Collections.singleton(provider));
    List<MyEvent> collectedEvents = collector.collect();
    assertThat(collectedEvents).containsOnly(a, b);
  }

  @Test
  void shouldReturnCombinedEvents() {
    MyEventProvider providerOne = mock(MyEventProvider.class);
    EventOne a = new EventOne();
    when(providerOne.getEvents()).thenReturn(Collections.singleton(a));

    MyEventProvider providerTwo = mock(MyEventProvider.class);
    EventTwo b = new EventTwo();
    when(providerTwo.getEvents()).thenReturn(Collections.singleton(b));

    MyEventCollector collector = new MyEventCollector(ImmutableSet.of(providerOne, providerTwo));
    List<MyEvent> collectedEvents = collector.collect();
    assertThat(collectedEvents).containsOnly(a, b);
  }

  static class EventOne extends MyEvent {
    public EventOne() {
      super(EventOne.class.getSimpleName(), "/one");
    }
  }

  static class EventTwo extends MyEvent {
    public EventTwo() {
      super(EventTwo.class.getSimpleName(), "/two");
    }
  }

}
