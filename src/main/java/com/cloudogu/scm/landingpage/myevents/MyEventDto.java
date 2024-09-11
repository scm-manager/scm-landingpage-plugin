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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * We could not use hal representation,
 * because we don't know each field of MyTasks so we could not map them to dto's.
 */
@Getter
public class MyEventDto {

  @JsonProperty("_embedded")
  private final Embedded embedded;

  public MyEventDto(Iterable<MyEvent> events) {
    this.embedded = new Embedded(events);
  }

  @Getter
  public static class Embedded {

    private final Iterable<MyEvent> events;

    private Embedded(Iterable<MyEvent> events) {
      this.events = events;
    }
  }
}
