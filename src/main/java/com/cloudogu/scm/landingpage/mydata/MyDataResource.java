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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import sonia.scm.api.v2.resources.ErrorDto;
import sonia.scm.web.VndMediaType;

import java.util.List;

@Path("v2/landingpage/mydata")
public class MyDataResource {

  private final MyDataCollector collector;

  private static final String MEDIATYPE = VndMediaType.PREFIX + "mydata" + VndMediaType.SUFFIX;

  @Inject
  public MyDataResource(MyDataCollector collector) {
    this.collector = collector;
  }

  @GET
  @Path("")
  @Produces(MEDIATYPE)
  @Operation(
    summary = "'My Data'",
    description = "Returns data relevant for the current user",
    tags = "Landingpage Plugin",
    operationId = "landingpage_my_data"
  )
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MEDIATYPE,
      schema = @Schema(implementation = MyDataDto.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public MyDataDto getData(@QueryParam("disabledTypes") List<String> disabledTypes) {
    return new MyDataDto(collector.collect(disabledTypes));
  }
}
