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
