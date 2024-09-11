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
