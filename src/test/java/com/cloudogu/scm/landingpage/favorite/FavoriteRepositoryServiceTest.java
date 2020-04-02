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
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteRepositoryServiceTest {

  private final Repository REPOSITORY = RepositoryTestData.createHeartOfGold();

  @Mock
  private FavoriteRepositoryProvider storeProvider;

  @Mock
  private FavoriteRepositoryProvider.FavoriteRepositoryStore store;

  @Mock
  private RepositoryServiceFactory serviceFactory;

  @Mock
  private RepositoryService repositoryService;

  @Mock
  private Subject subject;

  @InjectMocks
  private FavoriteRepositoryService service;

  @BeforeEach
  void initSubject() {
    ThreadContext.bind(subject);
  }

  @AfterEach
  void tearDownSubject() {
    ThreadContext.unbindSubject();
  }

  @Nested
  class WithRepositoryService {
    @BeforeEach
    void initRepositoryService() {
      when(serviceFactory.create(REPOSITORY.getNamespaceAndName())).thenReturn(repositoryService);
      when(repositoryService.getRepository()).thenReturn(REPOSITORY);
      lenient().when(storeProvider.get()).thenReturn(store);
    }

    @Test
    void shouldFavorizeRepositoryForUser() {
      service.favorizeRepository(REPOSITORY.getNamespaceAndName());

      verify(store).add(REPOSITORY);
    }

    @Test
    void shouldThrowAuthorizationExceptionIfNotPermittedToFavorize() {
      doThrow(AuthorizationException.class).when(subject).checkPermission(anyString());

      assertThrows(AuthorizationException.class, () -> service.favorizeRepository(REPOSITORY.getNamespaceAndName()));
    }

    @Test
    void shouldUnfavorizeRepositoryForUser() {
      service.unfavorizeRepository(REPOSITORY.getNamespaceAndName());

      verify(store).remove(REPOSITORY);
    }

    @Test
    void shouldThrowAuthorizationExceptionIfNotPermittedToUnfavorize() {
      doThrow(AuthorizationException.class).when(subject).checkPermission(anyString());

      assertThrows(AuthorizationException.class, () -> service.unfavorizeRepository(REPOSITORY.getNamespaceAndName()));
    }
  }

}
