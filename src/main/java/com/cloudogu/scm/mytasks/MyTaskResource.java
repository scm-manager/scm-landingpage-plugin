package com.cloudogu.scm.mytasks;

import de.otto.edison.hal.Embedded;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import sonia.scm.web.VndMediaType;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

@Path("v2/landingpage/mytasks")
public class MyTaskResource {

  private final MyTaskCollector collector;

  private static final String MEDIATYPE = VndMediaType.PREFIX + "mytasks" + VndMediaType.SUFFIX;

  @Inject
  public MyTaskResource(MyTaskCollector collector) {
    this.collector = collector;
  }

  @GET
  @Produces(MEDIATYPE)
  public MyTasksDto getTasks(@Context UriInfo uriInfo) {
    String self = uriInfo.getAbsolutePath().toASCIIString();
    return new MyTasksDto(self, collector.collect());
  }

}
