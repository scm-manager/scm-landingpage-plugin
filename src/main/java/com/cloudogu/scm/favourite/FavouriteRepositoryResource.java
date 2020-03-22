package com.cloudogu.scm.favourite;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import sonia.scm.api.v2.resources.ErrorDto;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.web.VndMediaType;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("v2/")
public class FavouriteRepositoryResource {

  private final FavouriteRepositoryService service;

  @Inject
  public FavouriteRepositoryResource(FavouriteRepositoryService service) {
    this.service = service;
  }

  @POST
  @Path("favorize/{namespace}/{name}/{userId}")
  @Operation(
    summary = "Favorize repository",
    description = "Favorizes a repository for an user",
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
                                     @PathParam("name") String name,
                                     @PathParam("userId") String userId) {
    service.favorizeRepository(new NamespaceAndName(namespace, name), userId);
    return Response.noContent().build();
  }

  @POST
  @Path("unfavorize/{namespace}/{name}/{userId}")
  @Operation(
    summary = "Unfavorize repository",
    description = "Unfavorizes a repository for an user",
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
                                       @PathParam("name") String name,
                                       @PathParam("userId") String userId) {
    service.unfavorizeRepository(new NamespaceAndName(namespace, name), userId);
    return Response.noContent().build();
  }

}
