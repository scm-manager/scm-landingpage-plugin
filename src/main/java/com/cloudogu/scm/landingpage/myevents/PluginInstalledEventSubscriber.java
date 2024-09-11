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
