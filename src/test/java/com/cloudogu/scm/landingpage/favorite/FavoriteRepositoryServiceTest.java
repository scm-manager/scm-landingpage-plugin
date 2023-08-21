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

import org.apache.shiro.authz.AuthorizationException;
import org.github.sdorra.jse.ShiroExtension;
import org.github.sdorra.jse.SubjectAware;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.repository.RepositoryTestData;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SubjectAware("Trillian")
@ExtendWith({MockitoExtension.class, ShiroExtension.class})
class FavoriteRepositoryServiceTest {

  private final Repository REPOSITORY = RepositoryTestData.createHappyVerticalPeopleTransporter();
  private final Repository REPOSITORY_2 = RepositoryTestData.createHeartOfGold();
  private final Repository REPOSITORY_3 = RepositoryTestData.create42Puzzle();

  @Mock
  private FavoriteRepositoryProvider storeProvider;

  @Mock
  private FavoriteRepositoryProvider.FavoriteRepositoryStore store;

  @Mock
  private RepositoryServiceFactory serviceFactory;

  @Mock
  private RepositoryService repositoryService;

  @Mock
  private RepositoryManager repositoryManager;

  @InjectMocks
  private FavoriteRepositoryService service;

  @Nested
  class WithRepositoryService {
    @BeforeEach
    void initRepositoryService() {
      lenient().when(serviceFactory.create(REPOSITORY.getNamespaceAndName())).thenReturn(repositoryService);
      lenient().when(repositoryService.getRepository()).thenReturn(REPOSITORY);
      lenient().when(storeProvider.get()).thenReturn(store);
    }

    @Test
    @SubjectAware(permissions = "repository:read:*")
    void shouldFavorizeRepositoryForUser() {
      service.favorizeRepository(REPOSITORY.getNamespaceAndName());

      verify(store).add(REPOSITORY);
    }

    @Test
    void shouldThrowAuthorizationExceptionIfNotPermittedToFavorize() {
      assertThrows(AuthorizationException.class, () -> service.favorizeRepository(REPOSITORY.getNamespaceAndName()));
    }

    @Test
    @SubjectAware(permissions = "repository:read:*")
    void shouldUnfavorizeRepositoryForUser() {
      service.unfavorizeRepository(REPOSITORY.getNamespaceAndName());

      verify(store).remove(REPOSITORY);
    }

    @Test
    void shouldThrowAuthorizationExceptionIfNotPermittedToUnfavorize() {
      assertThrows(AuthorizationException.class, () -> service.unfavorizeRepository(REPOSITORY.getNamespaceAndName()));
    }

    @Test
    void shouldUnfavorizeRepositoryForAllUsers() {
      service.unfavorizeRepositoryForAllUsers(REPOSITORY);

      verify(store).removeFromAll(REPOSITORY);
    }

    @Test
    @SubjectAware(permissions = "repository:read:*")
    void shouldReturnFavoriteRepositories() {
      when(repositoryManager.get(REPOSITORY.getId())).thenReturn(REPOSITORY);
      when(store.get()).thenReturn(new FavoriteRepository(Collections.singleton(REPOSITORY.getId())));
      final List<Repository> favoriteRepositories = service.getFavoriteRepositories();
      assertThat(favoriteRepositories).hasSize(1);
      assertThat(favoriteRepositories.get(0).getId()).isEqualTo(REPOSITORY.getId());
    }

    @Test
    @SubjectAware(permissions = "repository:read:*")
    void shouldReturnFavoriteRepositoriesAlphabetically() {
      when(repositoryManager.get(REPOSITORY.getId())).thenReturn(REPOSITORY);
      when(repositoryManager.get(REPOSITORY_2.getId())).thenReturn(REPOSITORY_2);
      when(repositoryManager.get(REPOSITORY_3.getId())).thenReturn(REPOSITORY_3);
      when(store.get()).thenReturn(new FavoriteRepository(Set.of(REPOSITORY.getId(), REPOSITORY_2.getId(), REPOSITORY_3.getId())));
      final List<Repository> favoriteRepositories = service.getFavoriteRepositories();
      assertThat(favoriteRepositories).hasSize(3);
      assertThat(favoriteRepositories.get(0).getName()).isEqualTo(REPOSITORY_3.getName());
      assertThat(favoriteRepositories.get(1).getName()).isEqualTo(REPOSITORY.getName());
      assertThat(favoriteRepositories.get(2).getName()).isEqualTo(REPOSITORY_2.getName());
    }
  }

}
