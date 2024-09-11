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

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.plugin.AvailablePlugin;
import sonia.scm.plugin.InstalledPlugin;
import sonia.scm.plugin.PluginEvent;
import sonia.scm.plugin.PluginManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PluginInstalledEventSubscriberTest {

  private InstalledPlugin oldPlugin = PluginTestHelper.createInstalled("scm-hitchhiker-plugin");
  private AvailablePlugin newPlugin = PluginTestHelper.createAvailable("scm-hitchhiker-plugin", "1.1");

  @Mock
  private Subject subject;

  @Mock
  private MyEventStore store;

  @Mock
  private PluginEvent event;

  @Mock
  private PluginManager pluginManager;

  @Captor
  private ArgumentCaptor<PluginInstalledEventSubscriber.PluginInstalledEvent> eventCaptor;

  @InjectMocks
  private PluginInstalledEventSubscriber subscriber;

  @BeforeEach
  void bindSubject() {
    ThreadContext.bind(subject);
  }

  @AfterEach
  void tearDownSubject() {
    ThreadContext.unbindSubject();
  }

  @Test
  void shouldFireMyEvent() {
    when(event.getEventType()).thenReturn(PluginEvent.PluginEventType.INSTALLED);
    when(event.getPlugin()).thenReturn(newPlugin);
    when(pluginManager.getInstalled("scm-hitchhiker-plugin")).thenReturn(Optional.of(oldPlugin));

    subscriber.handleEvent(event);

    verify(store).add(eventCaptor.capture());

    PluginInstalledEventSubscriber.PluginInstalledEvent pluginInstalledEvent = eventCaptor.getValue();
    assertThat(pluginInstalledEvent.getType()).isEqualTo("PluginInstalledEvent");
    assertThat(pluginInstalledEvent.getPermission()).isEqualTo("plugin:write");
    assertThat(pluginInstalledEvent.getPreviousPluginVersion()).isEqualTo("1.0");
    assertThat(pluginInstalledEvent.getNewPluginVersion()).isEqualTo("1.1");
    assertThat(pluginInstalledEvent.getPluginName()).isEqualTo(newPlugin.getDescriptor().getInformation().getDisplayName());
  }

  @Test
  void shouldNotFireMyEventWhenNotCreatedEvent() {
    when(event.getEventType()).thenReturn(PluginEvent.PluginEventType.INSTALLATION_FAILED);
    subscriber.handleEvent(event);

    verify(store, never()).add(eventCaptor.capture());
  }

}
