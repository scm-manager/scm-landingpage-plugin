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
package com.cloudogu.scm.mytasks;

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
import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
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
      when(subject.isPermitted(PluginPermissions.manage().asShiroString())).thenReturn(true);
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
      Instant now = Instant.now();
      when(clock.instant()).thenReturn(now);

      Iterable<MyTask> tasks = provider.getTasks();
      Iterator<MyTask> it = tasks.iterator();
      assertThat(it.hasNext()).isTrue();
      MyTask task = it.next();
      assertThat(it.hasNext()).isFalse();

      PluginUpdateTask updateTask = (PluginUpdateTask) task;
      assertThat(updateTask.getOutdatedPlugins()).isEqualTo(3);
      assertThat(updateTask.getLastCheck()).isEqualTo(now);
    }




  }

  @Test
  void shouldReturnEmptyIfUserIsNotPriledged() {
    lenient().when(pluginManager.getUpdatable()).thenThrow(AuthorizationException.class);

    Iterable<MyTask> tasks = provider.getTasks();
    assertThat(tasks).isEmpty();
  }

}
