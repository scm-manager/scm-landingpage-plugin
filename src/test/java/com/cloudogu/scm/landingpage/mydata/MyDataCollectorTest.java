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

package com.cloudogu.scm.landingpage.mydata;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

    MyDataCollector collector = new MyDataCollector(singleton(provider));
    List<MyData> collectedData = collector.collect(emptyList());
    assertThat(collectedData).containsOnly(a, b);
  }

  @Test
  void shouldReturnCombinedData() {
    MyDataProvider providerOne = mock(MyDataProvider.class);
    DataOne a = new DataOne();
    when(providerOne.getData()).thenReturn(singleton(a));

    MyDataProvider providerTwo = mock(MyDataProvider.class);
    DataTwo b = new DataTwo();
    when(providerTwo.getData()).thenReturn(singleton(b));

    MyDataCollector collector = new MyDataCollector(ImmutableSet.of(providerOne, providerTwo));
    List<MyData> collectedData = collector.collect(emptyList());
    assertThat(collectedData).containsOnly(a, b);
  }

  @Test
  void shouldFilterProvidersByType() {
    MyDataProvider providerOne = mock(MyDataProvider.class);
    DataOne a = new DataOne();
    when(providerOne.getData()).thenReturn(singleton(a));
    when(providerOne.getType()).thenReturn(Optional.of("spaceship"));

    MyDataProvider providerTwo = mock(MyDataProvider.class);
    when(providerTwo.getType()).thenReturn(Optional.of("plant"));

    MyDataProvider providerThree = mock(MyDataProvider.class);
    DataTwo c = new DataTwo();
    when(providerThree.getData()).thenReturn(singleton(c));

    MyDataCollector collector = new MyDataCollector(ImmutableSet.of(providerOne, providerTwo, providerThree));
    List<MyData> collectedData = collector.collect(singletonList("plant"));
    assertThat(collectedData).containsOnly(a, c);
    verify(providerTwo, never()).getData();
    verify(providerTwo).getType();
  }

  @Test
  void shouldFilterDataByType() {
    MyDataProvider providerOne = mock(MyDataProvider.class);
    SpaceshipData a = new SpaceshipData();
    when(providerOne.getData()).thenReturn(singleton(a));

    MyDataProvider providerTwo = mock(MyDataProvider.class);
    PlantData b = new PlantData();
    when(providerTwo.getData()).thenReturn(singleton(b));

    MyDataCollector collector = new MyDataCollector(ImmutableSet.of(providerOne, providerTwo));
    List<MyData> collectedData = collector.collect(singletonList("plant"));
    assertThat(collectedData).containsOnly(a);
    verify(providerTwo).getData();
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

  static class SpaceshipData extends MyData {
    public SpaceshipData() {
      super(SpaceshipData.class.getSimpleName());
    }

    @Override
    public String getType() {
      return "spaceship";
    }
  }

  static class PlantData extends MyData {
    public PlantData() {
      super(PlantData.class.getSimpleName());
    }

    @Override
    public String getType() {
      return "plant";
    }
  }

}
