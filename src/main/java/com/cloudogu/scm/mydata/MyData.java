package com.cloudogu.scm.mydata;

import com.cloudogu.scm.mytasks.SelfLinkSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

@Getter
public class MyData {
  private final String type;

  @JsonProperty("_links")
  @JsonSerialize(using = SelfLinkSerializer.class)
  private final String link;

  public MyData(String type, String link) {
    this.type = type;
    this.link = link;
  }
}
