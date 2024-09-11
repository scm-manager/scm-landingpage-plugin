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
