package com.cloudogu.scm.mytasks;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MyTaskCollector {

  private final Set<MyTaskProvider> providers;

  @Inject
  public MyTaskCollector(Set<MyTaskProvider> providers) {
    this.providers = providers;
  }

  public List<MyTask> collect() {
    List<MyTask> taskList = new ArrayList<>();
    for (MyTaskProvider provider : providers) {
      Iterable<MyTask> tasks = provider.getTasks();
      tasks.forEach(task -> taskList.add(task));
    }
    return taskList;
  }
}
