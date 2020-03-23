package com.cloudogu.scm.mytasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

/**
 * We could not use hal representation,
 * because we don't know each field of MyTasks so we could not map them to dto's.
 */
@Getter
public class MyTasksDto {

  @JsonProperty("_links")
  @JsonSerialize(using = SelfLinkSerializer.class)
  private final String self;

  @JsonProperty("_embedded")
  private final Embedded embedded;

  public MyTasksDto(String self, Iterable<MyTask> tasks) {
    this.self = self;
    this.embedded = new Embedded(tasks);
  }

  @Getter
  public static class Embedded {

    private final Iterable<MyTask> tasks;

    private Embedded(Iterable<MyTask> tasks) {
      this.tasks = tasks;
    }
  }
}
