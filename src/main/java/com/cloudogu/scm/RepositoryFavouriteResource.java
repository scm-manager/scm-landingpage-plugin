package com.cloudogu.scm;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("v2/")
public class RepositoryFavouriteResource {



  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getAll() {
    return null;
  }

}
