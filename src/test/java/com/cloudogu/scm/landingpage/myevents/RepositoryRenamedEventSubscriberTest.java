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
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryModificationEvent;
import sonia.scm.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositoryRenamedEventSubscriberTest {

  private Repository REPOSITORY = new Repository("1", "git", "nicest", "repo");

  @Mock
  private Subject subject;

  @Mock
  private MyEventStore store;

  @Mock
  private RepositoryModificationEvent event;

  @Mock
  private PrincipalCollection principalCollection;

  @Captor
  private ArgumentCaptor<RepositoryRenamedEventSubscriber.RepositoryRenamedEvent> eventCaptor;

  @InjectMocks
  private RepositoryRenamedEventSubscriber subscriber;

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
    Repository changedRepo = REPOSITORY.clone();
    changedRepo.setNamespace("Hitchhiker");
    changedRepo.setName("Heart-Of-Gold");

    when(event.getItem()).thenReturn(changedRepo);
    when(event.getOldItem()).thenReturn(REPOSITORY);
    when(event.getItem()).thenReturn(changedRepo);
    when(subject.getPrincipals()).thenReturn(principalCollection);
    when(principalCollection.oneByType(User.class)).thenReturn(trillian);

    subscriber.handleEvent(event);

    verify(store).add(eventCaptor.capture());

    RepositoryRenamedEventSubscriber.RepositoryRenamedEvent repositoryRenamedEvent = eventCaptor.getValue();
    assertThat(repositoryRenamedEvent.getPermission()).isEqualTo("repository:read:1");
    assertThat(repositoryRenamedEvent.getOldRepository()).isEqualTo(REPOSITORY.getNamespace() + "/" + REPOSITORY.getName());
    assertThat(repositoryRenamedEvent.getType()).isEqualTo(RepositoryRenamedEventSubscriber.RepositoryRenamedEvent.class.getSimpleName());
    assertThat(repositoryRenamedEvent.getUserMail()).isEqualTo(trillian.getMail());
    assertThat(repositoryRenamedEvent.getUsername()).isEqualTo(trillian.getName());
  }

}
