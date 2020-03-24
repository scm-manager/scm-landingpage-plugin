package com.cloudogu.scm.mydata;

import com.cloudogu.scm.mytasks.SelfLinkSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

/**
 * We could not use hal representation,
 * because we don't know each field of MyTasks so we could not map them to dto's.
 */
@Getter
public class MyDataDto {

  @JsonProperty("_links")
  @JsonSerialize(using = SelfLinkSerializer.class)
  private final String self;

  @JsonProperty("_embedded")
  private final Embedded embedded;

  public MyDataDto(String self, Iterable<MyData> data) {
    this.self = self;
    this.embedded = new Embedded(data);
  }

  @Getter
  public static class Embedded {

    private final Iterable<MyData> data;

    private Embedded(Iterable<MyData> data) {
      this.data = data;
    }
  }
}
