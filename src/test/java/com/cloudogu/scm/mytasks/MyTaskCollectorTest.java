package com.cloudogu.scm.mytasks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
      super(TaskOne.class.getSimpleName(), "/one");
    }
  }

  static class TaskTwo extends MyTask {
    public TaskTwo() {
      super(TaskTwo.class.getSimpleName(), "/two");
    }
  }

}
