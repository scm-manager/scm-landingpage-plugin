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
