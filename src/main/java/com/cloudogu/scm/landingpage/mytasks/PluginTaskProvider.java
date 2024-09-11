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

package com.cloudogu.scm.landingpage.mytasks;

import sonia.scm.plugin.Extension;
import sonia.scm.plugin.InstalledPlugin;
import sonia.scm.plugin.PluginManager;
import sonia.scm.plugin.PluginPermissions;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Extension
public class PluginTaskProvider implements MyTaskProvider {

  private final PluginManager pluginManager;

  @Inject
  public PluginTaskProvider(PluginManager pluginManager) {
    this.pluginManager = pluginManager;
  }

  @Override
  public Iterable<MyTask> getTasks() {
    List<PluginUpdateTask> tasks = new ArrayList<>();
    if (PluginPermissions.write().isPermitted()) {
      List<InstalledPlugin> updatable = pluginManager.getUpdatable();
      int size = updatable.size();
      if (size > 0) {
        tasks.add(new PluginUpdateTask(size));
      }
    }
    return Collections.unmodifiableList(tasks);
  }
}
