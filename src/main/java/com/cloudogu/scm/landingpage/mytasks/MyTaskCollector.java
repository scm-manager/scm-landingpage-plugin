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

import jakarta.inject.Inject;
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
      tasks.forEach(taskList::add);
    }
    return taskList;
  }
}
