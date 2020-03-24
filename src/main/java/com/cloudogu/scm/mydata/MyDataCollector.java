package com.cloudogu.scm.mydata;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MyDataCollector {

  private final Set<MyDataProvider> providers;

  @Inject
  public MyDataCollector(Set<MyDataProvider> providers) {
    this.providers = providers;
  }

  public List<MyData> collect() {
    List<MyData> dataList = new ArrayList<>();
    for (MyDataProvider provider : providers) {
      Iterable<MyData> tasks = provider.getData();
      tasks.forEach(dataList::add);
    }
    return dataList;
  }

}
