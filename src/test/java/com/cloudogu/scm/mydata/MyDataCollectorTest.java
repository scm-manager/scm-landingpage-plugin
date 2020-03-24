package com.cloudogu.scm.mydata;

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
class MyDataCollectorTest {

  @Test
  void shouldReturnCollectedTasks() {
    MyDataProvider provider = mock(MyDataProvider.class);
    DataOne a = new DataOne();
    DataTwo b = new DataTwo();

    Iterable<MyData> data = ImmutableList.of(a, b);
    when(provider.getData()).thenReturn(data);

    MyDataCollector collector = new MyDataCollector(Collections.singleton(provider));
    List<MyData> collectedData = collector.collect();
    assertThat(collectedData).containsOnly(a, b);
  }

  @Test
  void shouldReturnCombinedTasks() {
    MyDataProvider providerOne = mock(MyDataProvider.class);
    DataOne a = new DataOne();
    when(providerOne.getData()).thenReturn(Collections.singleton(a));

    MyDataProvider providerTwo = mock(MyDataProvider.class);
    DataTwo b = new DataTwo();
    when(providerTwo.getData()).thenReturn(Collections.singleton(b));

    MyDataCollector collector = new MyDataCollector(ImmutableSet.of(providerOne, providerTwo));
    List<MyData> collectedData = collector.collect();
    assertThat(collectedData).containsOnly(a, b);
  }

  static class DataOne extends MyData {
    public DataOne() {
      super(DataOne.class.getSimpleName(), "/one");
    }
  }

  static class DataTwo extends MyData {
    public DataTwo() {
      super(DataTwo.class.getSimpleName(), "/two");
    }
  }

}
