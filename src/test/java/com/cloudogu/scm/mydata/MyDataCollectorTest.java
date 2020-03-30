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
  void shouldReturnCollectedData() {
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
  void shouldReturnCombinedData() {
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
      super(DataOne.class.getSimpleName());
    }
  }

  static class DataTwo extends MyData {
    public DataTwo() {
      super(DataTwo.class.getSimpleName());
    }
  }

}
