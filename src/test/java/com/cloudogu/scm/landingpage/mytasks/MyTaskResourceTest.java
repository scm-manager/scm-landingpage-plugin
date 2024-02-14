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
package com.cloudogu.scm.landingpage.mytasks;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyTaskResourceTest {

  @Mock
  private MyTaskCollector collector;

  private RestDispatcher dispatcher;

  private final ObjectMapper mapper = new ObjectMapper();

  @BeforeEach
  void setUpResource() {
    dispatcher = new RestDispatcher();
    dispatcher.addSingletonResource(new MyTaskResource(collector));
  }

  @Test
  void shouldReturnCollectedTask() throws URISyntaxException, IOException {
    when(collector.collect()).thenReturn(Collections.singletonList(new SampleTask("awesome")));

    MockHttpRequest request = MockHttpRequest.get("/v2/landingpage/mytasks");
    MockHttpResponse response = new MockHttpResponse();

    dispatcher.invoke(request, response);

    MediaType contentType = (MediaType) response.getOutputHeaders().getFirst("Content-Type");
    assertThat(contentType.toString()).isEqualTo("application/vnd.scmm-mytasks+json;v=2");
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

    JsonNode node = mapper.readTree(response.getOutput());
    JsonNode tasks = node.get("_embedded").get("tasks");
    assertThat(tasks.isArray()).isTrue();

    JsonNode task = tasks.get(0);
    assertThat(task.get("type").asText()).isEqualTo(SampleTask.class.getName());
    assertThat(task.get("value").asText()).isEqualTo("awesome");
  }

  @Getter
  public static class SampleTask extends MyTask {

    private final String value;

    public SampleTask(String value) {
      super(SampleTask.class.getName());
      this.value = value;
    }
  }
}
