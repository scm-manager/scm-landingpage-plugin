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
package com.cloudogu.scm.mydata;

import com.cloudogu.scm.favorite.FavoriteRepository;
import com.cloudogu.scm.favorite.FavoriteRepositoryProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import de.otto.edison.hal.HalRepresentation;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.repository.RepositoryTestData;
import sonia.scm.web.api.RepositoryToHalMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteRepositoryDataProviderTest {

  private final Repository REPOSITORY = RepositoryTestData.createHeartOfGold();
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Mock
  private RepositoryManager repositoryManager;
  @Mock
  private RepositoryToHalMapper mapper;
  @Mock
  private FavoriteRepositoryProvider favoriteRepositoryProvider;
  @Mock
  private FavoriteRepositoryProvider.FavoriteRepositoryStore store;
  @Mock
  private Subject subject;

  @InjectMocks
  private FavoriteRepositoryDataProvider dataProvider;

  @BeforeEach
  void initSubject() {
    ThreadContext.bind(subject);
  }

  @AfterEach
  void tearDownSubject() {
    ThreadContext.unbindSubject();
  }

  @Test
  void shouldReturnEmptyList() {
    FavoriteRepository favorite = new FavoriteRepository(ImmutableSet.of());
    when(favoriteRepositoryProvider.get()).thenReturn(store);
    when(store.get()).thenReturn(favorite);

    Iterable<MyData> data = dataProvider.getData();

    assertThat(data.iterator().hasNext()).isFalse();
  }

  @Test
  void shouldReturnFavoriteRepositories() {
    FavoriteRepository favorite = new FavoriteRepository(ImmutableSet.of(REPOSITORY.getId()));
    HalRepresentation halRepresentation = new HalRepresentation();
    when(favoriteRepositoryProvider.get()).thenReturn(store);
    when(store.get()).thenReturn(favorite);
    when(repositoryManager.get(REPOSITORY.getId())).thenReturn(REPOSITORY);
    when(mapper.map(REPOSITORY)).thenReturn(halRepresentation);

    FavoriteRepositoryData data = (FavoriteRepositoryData) dataProvider.getData().iterator().next();

    assertThat(data.getLink()).isEqualTo("/repo/hitchhiker/HeartOfGold");
    assertThat(data.getType()).isEqualTo(FavoriteRepositoryData.class.getSimpleName());
    assertThat(data.getRepository()).isEqualTo(halRepresentation);
  }
}
