package com.cloudogu.scm.mytasks;

import sonia.scm.plugin.Extension;
import sonia.scm.plugin.InstalledPlugin;
import sonia.scm.plugin.PluginManager;
import sonia.scm.plugin.PluginPermissions;

import javax.inject.Inject;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Extension
public class PluginTaskProvider implements MyTaskProvider {

  private final PluginManager pluginManager;
  private final Clock clock;

  @Inject
  public PluginTaskProvider(PluginManager pluginManager) {
    this(pluginManager, Clock.systemUTC());
  }

  PluginTaskProvider(PluginManager pluginManager, Clock clock) {
    this.pluginManager = pluginManager;
    this.clock = clock;
  }

  @Override
  public Iterable<MyTask> getTasks() {
    List<PluginUpdateTask> tasks = new ArrayList<>();
    if (PluginPermissions.manage().isPermitted()) {
      List<InstalledPlugin> updatable = pluginManager.getUpdatable();
      int size = updatable.size();
      if (size > 0) {
        tasks.add(new PluginUpdateTask(size, clock.instant()));
      }
    }
    return Collections.unmodifiableList(tasks);
  }
}
