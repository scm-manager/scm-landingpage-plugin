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
package com.cloudogu.scm.landingpage.favorite;

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

import java.net.URI;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositoryLinkEnricherTest {

  private Repository REPOSITORY = new Repository();

  @Mock
  private FavoriteRepositoryProvider storeProvider;
  @Mock
  private FavoriteRepositoryProvider.FavoriteRepositoryStore store;
  @Mock
  private Subject subject;
  @Mock
  private HalAppender appender;

  private HalEnricherContext context;

  @InjectMocks
  private RepositoryLinkEnricher enricher;

  @BeforeEach
  void setUp() {
    ScmPathInfoStore scmPathInfoStore = new ScmPathInfoStore();
    scmPathInfoStore.set(() -> URI.create("https://scm-manager.org/scm/api/"));
    Provider<ScmPathInfoStore> scmPathInfoStoreProvider = Providers.of(scmPathInfoStore);
    enricher = new RepositoryLinkEnricher(scmPathInfoStoreProvider, storeProvider);
    context = HalEnricherContext.of(REPOSITORY);
    lenient().when(storeProvider.get()).thenReturn(store);
  }

  @BeforeEach
  void prepareRepository() {
    REPOSITORY.setId("HeartOfGold");
    REPOSITORY.setNamespace("hitchhiker");
    REPOSITORY.setName("HeartOfGold");
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
      when(store.get()).thenReturn(new FavoriteRepository(ImmutableSet.of("heartOfGold", "puzzle42")));

      enricher.enrich(context, appender);

      verify(appender).appendLink("favorize", "https://scm-manager.org/scm/api/v2/favorize/hitchhiker/HeartOfGold");
    }

    @Test
    void shouldEnrichUnfavorizeLink() {
      when(store.get()).thenReturn(new FavoriteRepository(ImmutableSet.of("HeartOfGold")));

      enricher.enrich(context, appender);

      verify(appender).appendLink("unfavorize", "https://scm-manager.org/scm/api/v2/unfavorize/hitchhiker/HeartOfGold");
    }
  }
}
