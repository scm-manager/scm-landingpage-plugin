package com.cloudogu.scm.mytasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

@Getter
public abstract class MyTask {

  private final String type;

  @JsonProperty("_links")
  @JsonSerialize(using = SelfLinkSerializer.class)
  private final String link;

  public MyTask(String type, String link) {
    this.type = type;
    this.link = link;
  }
}
