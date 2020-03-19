package com.cloudogu.scm.mytasks;

import lombok.Getter;

@Getter
public abstract class MyTask {

  private final String type;
  private final String link;

  public MyTask(String type, String link) {
    this.type = type;
    this.link = link;
  }
}
