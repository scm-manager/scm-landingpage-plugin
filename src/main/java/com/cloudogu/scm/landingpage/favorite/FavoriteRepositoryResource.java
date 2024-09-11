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

package com.cloudogu.scm.landingpage.favorite;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import sonia.scm.api.v2.resources.ErrorDto;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.web.VndMediaType;
import sonia.scm.web.api.RepositoryToHalMapper;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("v2/")
public class FavoriteRepositoryResource {

  private final FavoriteRepositoryService service;
  private final RepositoryToHalMapper mapper;

  @Inject
  public FavoriteRepositoryResource(FavoriteRepositoryService service, RepositoryToHalMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @POST
  @Path("favorize/{namespace}/{name}")
  @Operation(
    summary = "Favorize repository",
    description = "Favorizes a repository for the current user",
    tags = "Landingpage Plugin",
    operationId = "landingpage_favorize_repo"
  )
  @ApiResponse(
    responseCode = "204",
    description = "no content"
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
  public Response favorizeRepository(@PathParam("namespace") String namespace,
                                     @PathParam("name") String name) {
    service.favorizeRepository(new NamespaceAndName(namespace, name));
    return Response.noContent().build();
  }

  @POST
  @Path("unfavorize/{namespace}/{name}")
  @Operation(
    summary = "Unfavorize repository",
    description = "Unfavorizes a repository for the current user",
    tags = "Landingpage Plugin",
    operationId = "landingpage_favorize_repo"
  )
  @ApiResponse(
    responseCode = "204",
    description = "no content"
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
  public Response unfavorizeRepository(@PathParam("namespace") String namespace,
                                       @PathParam("name") String name) {
    service.unfavorizeRepository(new NamespaceAndName(namespace, name));
    return Response.noContent().build();
  }

  @GET
  @Path("favorites")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getFavoriteRepositories() {
    final List<Repository> favoriteRepositories = service.getFavoriteRepositories();
    return Response.ok().entity(new FavoriteRepositoriesDto(
      favoriteRepositories.stream()
        .map(mapper::map)
        .collect(Collectors.toList()))
    ).build();
  }

}
