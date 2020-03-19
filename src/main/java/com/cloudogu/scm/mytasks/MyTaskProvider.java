package com.cloudogu.scm.mytasks;

import sonia.scm.plugin.ExtensionPoint;

@ExtensionPoint
public interface MyTaskProvider {

  Iterable<MyTask> getTasks();
}
