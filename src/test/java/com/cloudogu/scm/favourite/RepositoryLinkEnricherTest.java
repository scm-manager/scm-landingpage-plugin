package com.cloudogu.scm.favourite;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provider;
import com.google.inject.util.Providers;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;

import java.net.URI;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositoryLinkEnricherTest {

  private Repository REPOSITORY = RepositoryTestData.createHeartOfGold();

  private Provider<ScmPathInfoStore> scmPathInfoStoreProvider;
  @Mock
  private FavouriteRepositoryStore store;
  @Mock
  private Subject subject;
  @Mock
  private HalAppender appender;

  private HalEnricherContext context;

  @InjectMocks
  private RepositoryLinkEnricher enricher;

  @BeforeEach
  public void setUp() {
    ScmPathInfoStore scmPathInfoStore = new ScmPathInfoStore();
    scmPathInfoStore.set(() -> URI.create("https://scm-manager.org/scm/api/"));
    scmPathInfoStoreProvider = Providers.of(scmPathInfoStore);
    enricher = new RepositoryLinkEnricher(scmPathInfoStoreProvider, store);
    context = HalEnricherContext.of(REPOSITORY);
  }

  @BeforeEach
  void initSubject() {
    ThreadContext.bind(subject);
  }

  @AfterEach
  void tearDownSubject() {
    ThreadContext.unbindSubject();
  }

  @Test
  void shouldNotEnrichLinkIfUserNotPermitted() {
    enricher.enrich(context, appender);

    verify(appender, never()).appendLink(anyString(), anyString());
  }

  @Nested
  class WithPermission {

    @BeforeEach
    void permitSubject() {
      when(subject.isPermitted(anyString())).thenReturn(true);
    }

    @Test
    void shouldEnrichFavorizeLink() {
      when(store.get(REPOSITORY.getId())).thenReturn(new RepositoryFavorite(REPOSITORY.getId(), ImmutableSet.of("tricia", "dent")));
      when(subject.getPrincipal()).thenReturn("trillian");

      enricher.enrich(context, appender);

      verify(appender).appendLink("favorize", "https://scm-manager.org/scm/api/v2/favorize/hitchhiker/HeartOfGold/trillian");
    }

    @Test
    void shouldEnrichUnfavorizeLink() {
      when(store.get(REPOSITORY.getId())).thenReturn(new RepositoryFavorite(REPOSITORY.getId(), ImmutableSet.of("trillian")));
      when(subject.getPrincipal()).thenReturn("trillian");

      enricher.enrich(context, appender);

      verify(appender).appendLink("unfavorize", "https://scm-manager.org/scm/api/v2/unfavorize/hitchhiker/HeartOfGold/trillian");
    }
  }


}
