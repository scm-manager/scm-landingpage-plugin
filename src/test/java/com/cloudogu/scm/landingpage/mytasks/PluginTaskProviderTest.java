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

import com.google.common.collect.ImmutableList;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.plugin.InstalledPlugin;
import sonia.scm.plugin.PluginManager;
import sonia.scm.plugin.PluginPermissions;

import java.time.Clock;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PluginTaskProviderTest {

  @Mock
  private PluginManager pluginManager;

  @Mock
  private Clock clock;

  @InjectMocks
  private PluginTaskProvider provider;

  @Mock
  private Subject subject;

  @BeforeEach
  void bindSubject() {
    ThreadContext.bind(subject);
  }

  @AfterEach
  void unbindSubject() {
    ThreadContext.unbindSubject();
  }

  @Nested
  class Priviledged {

    @BeforeEach
    void assignPermissions() {
      when(subject.isPermitted(PluginPermissions.write().asShiroString())).thenReturn(true);
    }

    @Test
    void shouldReturnEmptyIterable() {
      when(pluginManager.getUpdatable()).thenReturn(Collections.emptyList());

      Iterable<MyTask> tasks = provider.getTasks();
      assertThat(tasks).isEmpty();
    }

    @Test
    void shouldReturnPluginUpdateTask() {
      List<InstalledPlugin> updates = ImmutableList.of(
        mock(InstalledPlugin.class),
        mock(InstalledPlugin.class),
        mock(InstalledPlugin.class)
      );
      when(pluginManager.getUpdatable()).thenReturn(updates);

      Iterable<MyTask> tasks = provider.getTasks();
      Iterator<MyTask> it = tasks.iterator();
      assertThat(it.hasNext()).isTrue();
      MyTask task = it.next();
      assertThat(it.hasNext()).isFalse();

      PluginUpdateTask updateTask = (PluginUpdateTask) task;
      assertThat(updateTask.getOutdatedPlugins()).isEqualTo(3);
    }
  }

  @Test
  void shouldReturnEmptyIfUserIsNotPriledged() {
    lenient().when(pluginManager.getUpdatable()).thenThrow(AuthorizationException.class);

    Iterable<MyTask> tasks = provider.getTasks();
    assertThat(tasks).isEmpty();
  }

}
