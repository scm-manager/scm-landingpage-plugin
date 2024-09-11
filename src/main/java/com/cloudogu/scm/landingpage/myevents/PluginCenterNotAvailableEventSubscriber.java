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
import sonia.scm.event.Event;
import sonia.scm.plugin.Extension;
import sonia.scm.plugin.PluginCenterErrorEvent;
import sonia.scm.plugin.PluginPermissions;

import jakarta.inject.Inject;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.time.Instant;

@Extension
@EagerSingleton
public class PluginCenterNotAvailableEventSubscriber {

  private final MyEventStore store;

  @Inject
  public PluginCenterNotAvailableEventSubscriber(MyEventStore store) {
    this.store = store;
  }

  @Subscribe
  public void handleEvent(PluginCenterErrorEvent pluginCenterErrorEvent) {
    store.add(new PluginCenterNotAvailableEvent(PluginPermissions.read().asShiroString()));
  }

  @Event
  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  @Getter
  @NoArgsConstructor
  static class PluginCenterNotAvailableEvent extends MyEvent {

    PluginCenterNotAvailableEvent(String permission) {
      super(PluginCenterNotAvailableEvent.class.getSimpleName(), permission);
    }
  }

}

