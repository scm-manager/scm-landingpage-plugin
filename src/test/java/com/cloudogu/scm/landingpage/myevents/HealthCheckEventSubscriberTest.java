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

import com.cloudogu.scm.landingpage.myevents.HealthCheckEventSubscriber.HealthCheckFailureEvent;
import com.cloudogu.scm.landingpage.myevents.HealthCheckEventSubscriber.HealthCheckSucceededEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.repository.HealthCheckEvent;
import sonia.scm.repository.HealthCheckFailure;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HealthCheckEventSubscriberTest {

  public static final HealthCheckFailure FAILURE = new HealthCheckFailure("1", "high probability", "too probable");
  public static final Repository REPOSITORY = RepositoryTestData.createHeartOfGold();
  @Mock
  private MyEventStore store;
  @InjectMocks
  private HealthCheckEventSubscriber eventSubscriber;

  @Test
  void shouldStoreFailureEvent() {
    eventSubscriber.handleEvent(new HealthCheckEvent(REPOSITORY, emptyList(), singleton(FAILURE)));

    verify(store).add(argThat(argument -> {
      assertThat(argument).isInstanceOf(HealthCheckFailureEvent.class);
      HealthCheckFailureEvent event = (HealthCheckFailureEvent) argument;
      assertThat(event.getRepository()).isEqualTo(REPOSITORY.getNamespaceAndName().toString());
      assertThat(event.getPermission()).isEqualTo("repository:healthCheck:" + REPOSITORY.getId());
      return true;
    }));
  }

  @Test
  void shouldStoreSucceededEvent() {
    eventSubscriber.handleEvent(new HealthCheckEvent(REPOSITORY, singleton(FAILURE), emptyList()));

    verify(store).add(argThat(argument -> {
      assertThat(argument).isInstanceOf(HealthCheckSucceededEvent.class);
      HealthCheckSucceededEvent event = (HealthCheckSucceededEvent) argument;
      assertThat(event.getRepository()).isEqualTo(REPOSITORY.getNamespaceAndName().toString());
      assertThat(event.getPermission()).isEqualTo("repository:healthCheck:" + REPOSITORY.getId());
      return true;
    }));
  }
}
