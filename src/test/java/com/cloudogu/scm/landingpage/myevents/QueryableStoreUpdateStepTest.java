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

package com.cloudogu.scm.landingpage.myevents;

import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.plugin.PluginLoader;
import sonia.scm.store.InMemoryByteConfigurationStore;
import sonia.scm.store.InMemoryByteConfigurationStoreFactory;
import sonia.scm.store.QueryableStore;
import sonia.scm.store.QueryableStoreExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({QueryableStoreExtension.class, MockitoExtension.class})
@QueryableStoreExtension.QueryableTypes(MyEvent.class)
class QueryableStoreUpdateStepTest {

  private final InMemoryByteConfigurationStoreFactory legacyStoreFactory = new InMemoryByteConfigurationStoreFactory();
  @Mock
  private PluginLoader pluginLoader;

  @Test
  void shouldMoveFromLegacyToQueryableStore(MyEventStoreFactory myEventStoreFactory) throws Exception {
    loadLegacyData();
    new QueryableStoreUpdateStep(myEventStoreFactory, legacyStoreFactory, pluginLoader).doUpdate();

    try (QueryableStore<MyEvent> store = myEventStoreFactory.get()) {
      assertThat(store.query().findAll()).hasSize(5);
    }
  }

  private void loadLegacyData() throws IOException {
    String xml = String.join(
      "\n",
      Resources.readLines(
        Resources.getResource("com/cloudogu/scm/landingpage/myevents/legacy-events.xml"),
        StandardCharsets.UTF_8)
    );

    ((InMemoryByteConfigurationStore<QueryableStoreUpdateStep.LegacyStoreEntry>) legacyStoreFactory
      .withType(QueryableStoreUpdateStep.LegacyStoreEntry.class)
      .withName("myevents")
      .build()
    ).setRawXml(xml);
  }
}
