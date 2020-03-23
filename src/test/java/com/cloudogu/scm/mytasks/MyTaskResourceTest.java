package com.cloudogu.scm.mytasks;

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

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
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
    assertThat(node.get("_links").get("self").get("href").asText()).isEqualTo("/v2/landingpage/mytasks");

    JsonNode tasks = node.get("_embedded").get("tasks");
    assertThat(tasks.isArray()).isTrue();

    JsonNode task = tasks.get(0);
    assertThat(task.get("type").asText()).isEqualTo(SampleTask.class.getName());
    assertThat(task.get("_links").get("self").get("href").asText()).isEqualTo("/sample");
    assertThat(task.get("value").asText()).isEqualTo("awesome");
  }

  @Getter
  public static class SampleTask extends MyTask {

    private final String value;

    public SampleTask(String value) {
      super(SampleTask.class.getName(), "/sample");
      this.value = value;
    }
  }
}
