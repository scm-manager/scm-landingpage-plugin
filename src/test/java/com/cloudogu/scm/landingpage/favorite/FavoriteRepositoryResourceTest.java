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

import de.otto.edison.hal.HalRepresentation;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;
import sonia.scm.web.RestDispatcher;
import sonia.scm.web.api.RepositoryToHalMapper;

import java.net.URISyntaxException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteRepositoryResourceTest {

  Repository REPOSITORY = RepositoryTestData.createHeartOfGold();

  @Mock
  private FavoriteRepositoryService service;

  @Mock
  private RepositoryToHalMapper mapper;

  private RestDispatcher dispatcher;

  @BeforeEach
  void init() {
    FavoriteRepositoryResource resource = new FavoriteRepositoryResource(service, mapper);

    dispatcher = new RestDispatcher();
    dispatcher.addSingletonResource(resource);
  }


  @Test
  void shouldFavorizeRepository() throws URISyntaxException {
    MockHttpRequest request = MockHttpRequest.post("/v2/favorize/" + REPOSITORY.getNamespace() + "/" + REPOSITORY.getName());
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    verify(service).favorizeRepository(new NamespaceAndName(REPOSITORY.getNamespace(), REPOSITORY.getName()));
    assertThat(response.getStatus()).isEqualTo(204);
  }

  @Test
  void shouldUnfavorizeRepository() throws URISyntaxException {
    MockHttpRequest request = MockHttpRequest.post("/v2/unfavorize/" + REPOSITORY.getNamespace() + "/" + REPOSITORY.getName());
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    verify(service).unfavorizeRepository(new NamespaceAndName(REPOSITORY.getNamespace(), REPOSITORY.getName()));
    assertThat(response.getStatus()).isEqualTo(204);
  }

  @Test
  void shouldGetFavoriteRepositories() throws URISyntaxException {
    when(service.getFavoriteRepositories()).thenReturn(Collections.singletonList(REPOSITORY));
    when(mapper.map(REPOSITORY)).thenReturn(new HalRepresentation());

    MockHttpRequest request = MockHttpRequest.get("/v2/favorites/");
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    verify(service).getFavoriteRepositories();
    assertThat(response.getStatus()).isEqualTo(200);
  }

}
