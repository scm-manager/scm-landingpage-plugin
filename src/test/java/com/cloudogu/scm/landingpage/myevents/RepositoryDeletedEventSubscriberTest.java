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
import sonia.scm.HandlerEventType;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryEvent;
import sonia.scm.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositoryDeletedEventSubscriberTest {

  private Repository REPOSITORY = new Repository("1", "git", "nicest", "repo");

  @Mock
  private Subject subject;

  @Mock
  private MyEventStore store;

  @Mock
  private RepositoryEvent event;

  @Mock
  private PrincipalCollection principalCollection;

  @Captor
  private ArgumentCaptor<RepositoryDeletedEventSubscriber.RepositoryDeletedEvent> eventCaptor;

  @InjectMocks
  private RepositoryDeletedEventSubscriber subscriber;

  @BeforeEach
  void bindSubject() {
    ThreadContext.bind(subject);
  }

  @AfterEach
  void tearDownSubject() {
    ThreadContext.unbindSubject();
  }

  @Test
  void shouldFireMyEvent() {
    User trillian = new User("trillian", "Trillian", "tricia@hitchhiker.org");

    when(event.getEventType()).thenReturn(HandlerEventType.DELETE);
    when(event.getItem()).thenReturn(REPOSITORY);
    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.oneByType(User.class)).thenReturn(trillian);

    subscriber.handleEvent(event);

    verify(store).add(eventCaptor.capture());

    RepositoryDeletedEventSubscriber.RepositoryDeletedEvent repositoryDeletedEvent = eventCaptor.getValue();
    assertThat(repositoryDeletedEvent.getPermission()).isEqualTo("repository:read:1");
    assertThat(repositoryDeletedEvent.getRepository()).isEqualTo(REPOSITORY.getNamespace() + "/" + REPOSITORY.getName());
    assertThat(repositoryDeletedEvent.getType()).isEqualTo(RepositoryDeletedEventSubscriber.RepositoryDeletedEvent.class.getSimpleName());
    assertThat(repositoryDeletedEvent.getDeleterDisplayName()).isEqualTo(trillian.getDisplayName());
    assertThat(repositoryDeletedEvent.getDeleterMail()).isEqualTo(trillian.getMail());
    assertThat(repositoryDeletedEvent.getDeleterName()).isEqualTo(trillian.getName());
  }

  @Test
  void shouldNotFireMyEventWhenNotCreatedEvent() {
    when(event.getEventType()).thenReturn(HandlerEventType.BEFORE_DELETE);
    subscriber.handleEvent(event);

    verify(store, never()).add(eventCaptor.capture());
  }

}
