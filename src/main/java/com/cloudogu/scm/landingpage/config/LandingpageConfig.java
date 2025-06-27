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

import com.cloudogu.conveyor.GenerateDto;
import com.cloudogu.conveyor.Include;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;

@GenerateDto
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class LandingpageConfig {
  @Include
  @NotNull
  @NotEmpty
  private String instanceName = "mySCM";

  @Include
  @NotNull
  @NotEmpty
  private String myEventsCleanupExpression = "0 0 2 * * ?"; // Every day at 2:00 AM

  @Include
  @Min(1)
  private int myEventsStoreSize = 1000;
}
