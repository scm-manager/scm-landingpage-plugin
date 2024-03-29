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
