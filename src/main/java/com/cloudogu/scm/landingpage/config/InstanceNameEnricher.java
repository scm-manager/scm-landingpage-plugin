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

package com.cloudogu.scm.landingpage.config;

import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.Index;
import sonia.scm.plugin.Extension;

import jakarta.inject.Inject;

@Extension
@Enrich(Index.class)
public class InstanceNameEnricher implements HalEnricher {

  private final LandingpageConfigAdapter configAdapter;

  @Inject
  public InstanceNameEnricher(LandingpageConfigAdapter configAdapter) {
    this.configAdapter = configAdapter;
  }

  @Override
  public void enrich(HalEnricherContext halEnricherContext, HalAppender halAppender) {
    LandingpageConfigDto configDto = LandingpageConfigDto.from(configAdapter.getConfiguration());
    halAppender.appendEmbedded("landingpageConfiguration", configDto);
  }
}
