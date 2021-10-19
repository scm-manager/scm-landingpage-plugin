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

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
