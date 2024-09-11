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

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MyDataCollector {

  private final Set<MyDataProvider> providers;

  @Inject
  public MyDataCollector(Set<MyDataProvider> providers) {
    this.providers = providers;
  }

  public List<MyData> collect(List<String> disabledTypes) {
    List<MyData> dataList = new ArrayList<>();
    for (MyDataProvider provider : providers) {
      final Optional<String> providerType = provider.getType();
      if (providerType.isPresent() && disabledTypes.contains(providerType.get())) {
        continue;
      }
      for (MyData data : provider.getData()) {
        if (!disabledTypes.contains(data.getType())) {
          dataList.add(data);
        }
      }
    }
    return dataList;
  }

}
