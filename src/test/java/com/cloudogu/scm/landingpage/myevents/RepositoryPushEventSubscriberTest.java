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

import com.google.common.collect.ImmutableList;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Person;
import sonia.scm.repository.PostReceiveRepositoryHookEvent;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.HookChangesetBuilder;
import sonia.scm.repository.api.HookContext;
import sonia.scm.user.User;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositoryPushEventSubscriberTest {

  private Repository REPOSITORY = new Repository("1", "git", "nicest", "repo");

  @Mock
  private MyEventStore store;

  @Mock
  private Subject subject;

  @Mock
  private PostReceiveRepositoryHookEvent event;

  @Mock
  private HookContext context;

  @Mock
  private HookChangesetBuilder changesetProvider;

  @Mock
  private PrincipalCollection principalCollection;

  @Captor
  private ArgumentCaptor<RepositoryPushEventSubscriber.PushEvent> eventCaptor;

  @InjectMocks
  private RepositoryPushEventSubscriber subscriber;

  @BeforeEach
  void bindSubject() {
    ThreadContext.bind(subject);
  }

  @AfterEach
  void tearDownSubject() {
    ThreadContext.unbindSubject();
  }

  @Test
  void shouldStoreEvent() {
    User trillian = new User("trillian", "Trillian", "tricia@hitchhiker.org");

    when(event.getRepository()).thenReturn(REPOSITORY);
    when(event.getContext()).thenReturn(context);
    when(context.getChangesetProvider()).thenReturn(changesetProvider);
    when(changesetProvider.getChangesets()).thenReturn(
      ImmutableList.of(
        new Changeset("1", 100L, Person.toPerson("trillian")),
        new Changeset("2", 200L, Person.toPerson("trillian")))
    );
    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.oneByType(User.class)).thenReturn(trillian);

    subscriber.handleEvents(event);

    verify(store).add(eventCaptor.capture());

    RepositoryPushEventSubscriber.PushEvent pushEvent = eventCaptor.getValue();
    assertThat(pushEvent.getPermission()).isEqualTo("repository:read:1");
    assertThat(pushEvent.getChangesets()).isEqualTo(2);
    assertThat(pushEvent.getRepository()).isEqualTo(REPOSITORY.getNamespace() + "/" + REPOSITORY.getName());
    assertThat(pushEvent.getAuthorDisplayName()).isEqualTo(trillian.getDisplayName());
    assertThat(pushEvent.getAuthorMail()).isEqualTo(trillian.getMail());
    assertThat(pushEvent.getAuthorName()).isEqualTo(trillian.getName());
    assertThat(pushEvent.getType()).isEqualTo(RepositoryPushEventSubscriber.PushEvent.class.getSimpleName());
  }

  @Test
  void shouldNotStoreEventWithoutChanges() {
    User trillian = new User("trillian", "Trillian", "tricia@hitchhiker.org");

    when(event.getRepository()).thenReturn(REPOSITORY);
    when(event.getContext()).thenReturn(context);
    when(context.getChangesetProvider()).thenReturn(changesetProvider);
    when(changesetProvider.getChangesets()).thenReturn(emptyList());

    subscriber.handleEvents(event);

    verify(store, never()).add(any());
  }
}
