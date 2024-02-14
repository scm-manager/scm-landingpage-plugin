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
