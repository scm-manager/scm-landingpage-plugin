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
package com.cloudogu.scm.landingpage;

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

import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndexLinkEnricherTest {

  @Mock
  private HalAppender appender;

  private HalEnricherContext context;

  @InjectMocks
  private IndexLinkEnricher enricher;

  @BeforeEach
  void setUp() {
    ScmPathInfoStore scmPathInfoStore = new ScmPathInfoStore();
    scmPathInfoStore.set(() -> URI.create("https://scm-manager.org/scm/api/"));
    com.google.inject.Provider<ScmPathInfoStore> scmPathInfoStoreProvider = Providers.of(scmPathInfoStore);
    enricher = new IndexLinkEnricher(scmPathInfoStoreProvider);
    context = HalEnricherContext.of();
  }

  @Nested
  class WithAuthenticatedUser {

    @BeforeEach
    void bindSubject() {
      Subject subject = mock(Subject.class);
      when(subject.isAuthenticated()).thenReturn(true);
      ThreadContext.bind(subject);
    }

    @AfterEach
    void removeSubject() {
      ThreadContext.unbindSubject();
    }

    @Test
    void shouldAppendFavoriteRepositoriesLink() {
      enricher.enrich(context, appender);

      verify(appender).appendLink("favoriteRepositories", "https://scm-manager.org/scm/api/v2/favorites");
    }

    @Test
    void shouldAppendTasksLink() {
      enricher.enrich(context, appender);

      verify(appender).appendLink("landingpageTasks", "https://scm-manager.org/scm/api/v2/landingpage/mytasks");
    }

    @Test
    void shouldAppendDataLink() {
      enricher.enrich(context, appender);

      verify(appender).appendLink("landingpageData", "https://scm-manager.org/scm/api/v2/landingpage/mydata");
    }

    @Test
    void shouldAppendEventsLink() {
      enricher.enrich(context, appender);

      verify(appender).appendLink("landingpageEvents", "https://scm-manager.org/scm/api/v2/landingpage/myevents");
    }
  }

  @Nested
  class WithAnonymous {

    @BeforeEach
    void bindSubject() {
      Subject subject = mock(Subject.class);
      ThreadContext.bind(subject);
    }

    @AfterEach
    void removeSubject() {
      ThreadContext.unbindSubject();
    }

    @Test
    void shouldAppendTasksLink() {
      enricher.enrich(context, appender);

      verify(appender, never()).appendLink(any(), any());
    }
  }

  @Nested
  class WithoutSubject {

    @BeforeEach
    void bindSubject() {
      Subject subject = mock(Subject.class);
      when(subject.isAuthenticated()).thenReturn(false);
      ThreadContext.bind(subject);
    }

    @AfterEach
    void removeSubject() {
      ThreadContext.unbindSubject();
    }

    @Test
    void shouldAppendTasksLink() {
      enricher.enrich(context, appender);

      verify(appender, never()).appendLink(any(), any());
    }
  }
}
