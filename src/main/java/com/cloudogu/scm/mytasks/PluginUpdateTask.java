package com.cloudogu.scm.mytasks;

import lombok.Getter;

import java.time.Instant;

@Getter
public class PluginUpdateTask extends MyTask {

  private final int outdatedPlugins;
  private final Instant lastCheck;

  public PluginUpdateTask(int outdatedPlugins, Instant lastCheck) {
    super(PluginUpdateTask.class.getSimpleName(), "/admin/plugins/installed");
    this.outdatedPlugins = outdatedPlugins;
    this.lastCheck = lastCheck;
  }
}
