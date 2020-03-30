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
