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

import sonia.scm.web.VndMediaType;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

@Path("v2/landingpage/myevents")
public class MyEventResource {

  private final MyEventStore store;

  private static final String MEDIATYPE = VndMediaType.PREFIX + "myevents" + VndMediaType.SUFFIX;

  @Inject
  public MyEventResource(MyEventStore store) {
    this.store = store;
  }

  @GET
  @Path("")
  @Produces(MEDIATYPE)
  public MyEventDto getEvents(@Context UriInfo uriInfo) {
    return new MyEventDto(store.getEvents());
  }
}
