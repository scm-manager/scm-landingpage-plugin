package com.cloudogu.scm.favourite;

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

import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(MockitoExtension.class)
class FavouriteRepositoryResourceTest {

  Repository REPOSITORY = RepositoryTestData.createHeartOfGold();

  @Mock
  private FavouriteRepositoryService service;

  private FavouriteRepositoryResource resource;
  private RestDispatcher dispatcher;

  @BeforeEach
  void init() {
    resource = new FavouriteRepositoryResource(service);

    dispatcher = new RestDispatcher();
    dispatcher.addSingletonResource(resource);
  }


  @Test
  void shouldFavorizeRepository() throws URISyntaxException {
    String userId = "trillian";
    MockHttpRequest request = MockHttpRequest.post("/v2/favorize/" + REPOSITORY.getNamespace() + "/" + REPOSITORY.getName() + "/" + userId);
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    verify(service).favorizeRepository(new NamespaceAndName(REPOSITORY.getNamespace(), REPOSITORY.getName()), userId);
    assertThat(response.getStatus()).isEqualTo(204);
  }

  @Test
  void shouldUnfavorizeRepository() throws URISyntaxException {
    String userId = "trillian";
    MockHttpRequest request = MockHttpRequest.post("/v2/unfavorize/" + REPOSITORY.getNamespace() + "/" + REPOSITORY.getName() + "/" + userId);
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    verify(service).unfavorizeRepository(new NamespaceAndName(REPOSITORY.getNamespace(), REPOSITORY.getName()), userId);
    assertThat(response.getStatus()).isEqualTo(204);
  }

}
