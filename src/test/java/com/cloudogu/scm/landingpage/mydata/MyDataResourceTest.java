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

package com.cloudogu.scm.landingpage.mydata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.web.RestDispatcher;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MyDataResourceTest {

  @Mock
  private MyDataCollector collector;

  private RestDispatcher dispatcher;

  private final ObjectMapper mapper = new ObjectMapper();

  @BeforeEach
  void setUpResource() {
    dispatcher = new RestDispatcher();
    dispatcher.addSingletonResource(new MyDataResource(collector));
  }

  @Test
  void shouldReturnCollectedData() throws URISyntaxException, IOException {
    when(collector.collect(emptyList())).thenReturn(singletonList(new SampleData("awesome")));

    MockHttpRequest request = MockHttpRequest.get("/v2/landingpage/mydata");
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    MediaType contentType = (MediaType) response.getOutputHeaders().getFirst("Content-Type");
    assertThat(contentType.toString()).isEqualTo("application/vnd.scmm-mydata+json;v=2");
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    JsonNode node = mapper.readTree(response.getOutput());
    JsonNode data = node.get("_embedded").get("data");
    assertThat(data.isArray()).isTrue();

    JsonNode task = data.get(0);
    assertThat(task.get("type").asText()).isEqualTo(SampleData.class.getName());
    assertThat(task.get("value").asText()).isEqualTo("awesome");
  }

  @Getter
  public static class SampleData extends MyData {

    private final String value;

    public SampleData(String value) {
      super(SampleData.class.getName());
      this.value = value;
    }
  }
}
