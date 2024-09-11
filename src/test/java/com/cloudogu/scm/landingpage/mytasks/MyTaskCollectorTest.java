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
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyTaskCollectorTest {

  @Test
  void shouldReturnCollectedTasks() {
    MyTaskProvider provider = mock(MyTaskProvider.class);
    TaskOne a = new TaskOne();
    TaskOne b = new TaskOne();

    Iterable<MyTask> tasks = ImmutableList.of(a, b);
    when(provider.getTasks()).thenReturn(tasks);

    MyTaskCollector collector = new MyTaskCollector(Collections.singleton(provider));
    List<MyTask> collectedTasks = collector.collect();
    assertThat(collectedTasks).containsOnly(a, b);
  }

  @Test
  void shouldReturnCombinedTasks() {
    MyTaskProvider providerOne = mock(MyTaskProvider.class);
    TaskOne a = new TaskOne();
    when(providerOne.getTasks()).thenReturn(Collections.singleton(a));

    MyTaskProvider providerTwo = mock(MyTaskProvider.class);
    TaskTwo b = new TaskTwo();
    when(providerTwo.getTasks()).thenReturn(Collections.singleton(b));

    MyTaskCollector collector = new MyTaskCollector(ImmutableSet.of(providerOne, providerTwo));
    List<MyTask> collectedTasks = collector.collect();
    assertThat(collectedTasks).containsOnly(a, b);
  }

  static class TaskOne extends MyTask {
    public TaskOne() {
      super(TaskOne.class.getSimpleName());
    }
  }

  static class TaskTwo extends MyTask {
    public TaskTwo() {
      super(TaskTwo.class.getSimpleName());
    }
  }

}
