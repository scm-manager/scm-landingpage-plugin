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

package com.cloudogu.scm.landingpage.myevents;

import com.github.legman.Subscribe;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sonia.scm.EagerSingleton;
import sonia.scm.plugin.AvailablePlugin;
import sonia.scm.plugin.Extension;
import sonia.scm.plugin.InstalledPlugin;
import sonia.scm.plugin.PluginEvent;
import sonia.scm.plugin.PluginManager;
import sonia.scm.plugin.PluginPermissions;

import jakarta.inject.Inject;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Optional;

@Extension
@EagerSingleton
public class PluginInstalledEventSubscriber {

  private final PluginManager pluginManager;
  private final MyEventStore store;

  @Inject
  public PluginInstalledEventSubscriber(PluginManager pluginManager, MyEventStore store) {
    this.pluginManager = pluginManager;
    this.store = store;
  }

  @Subscribe
  public void handleEvent(PluginEvent pluginEvent) {
    if (pluginEvent.getEventType() == PluginEvent.PluginEventType.INSTALLED) {
      AvailablePlugin newPlugin = pluginEvent.getPlugin();
      Optional<InstalledPlugin> installedPlugin = pluginManager.getInstalled(newPlugin.getDescriptor().getInformation().getName());
      String permission = PluginPermissions.write().asShiroString();
      String pluginName = newPlugin.getDescriptor().getInformation().getDisplayName();
      String previousPluginVersion = null;

      if (installedPlugin.isPresent()) {
        previousPluginVersion = installedPlugin.get().getDescriptor().getInformation().getVersion();
      }

      String newPluginVersion = newPlugin.getDescriptor().getInformation().getVersion();
      store.add(new PluginInstalledEvent(permission, pluginName, previousPluginVersion, newPluginVersion));
    }
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  @Getter
  @NoArgsConstructor
  static class PluginInstalledEvent extends MyEvent {
    private String pluginName;
    private String previousPluginVersion;
    private String newPluginVersion;

    PluginInstalledEvent(String permission, String pluginName, String previousPluginVersion, String newPluginVersion) {
      super(PluginInstalledEvent.class.getSimpleName(), permission);

      this.pluginName = pluginName;
      this.previousPluginVersion = previousPluginVersion;
      this.newPluginVersion = newPluginVersion;
    }
  }

}
