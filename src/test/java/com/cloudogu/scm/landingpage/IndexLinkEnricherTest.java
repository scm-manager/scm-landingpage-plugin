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
    void shouldNotAppendLinks() {
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
    void shouldNotAppendLinks() {
      enricher.enrich(context, appender);

      verify(appender, never()).appendLink(any(), any());
    }
  }
}
