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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.HandlerEventType;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryEvent;
import sonia.scm.repository.RepositoryTestData;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class RemoveDeletedRepositoryFavoritesTest {

  @Mock
  FavoriteRepositoryService favoriteRepositoryService;

  @InjectMocks
  RemoveDeletedRepositoryFavorites removeDeletedRepositoryFavorites;

  @Test
  void shouldHandleDeleteEvent() {
    Repository repository = RepositoryTestData.createHeartOfGold();
    RepositoryEvent repositoryEvent = new RepositoryEvent(HandlerEventType.DELETE, repository);
    removeDeletedRepositoryFavorites.handle(repositoryEvent);
    verify(favoriteRepositoryService).unfavorizeRepositoryForAllUsers(repository);
  }

  @Test
  void shouldNotHandleOtherRepositoryEvents() {
    Repository repository = RepositoryTestData.createHeartOfGold();
    RepositoryEvent repositoryEvent = new RepositoryEvent(HandlerEventType.CREATE, repository);
    removeDeletedRepositoryFavorites.handle(repositoryEvent);
    verifyNoInteractions(favoriteRepositoryService);
  }

}
